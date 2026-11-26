package net.titan.dev

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile

import java.util.jar.JarFile

/**
 * {@code net.titan.runelite} -- a Titan plugin written with the RuneLite API.
 *
 * <pre>
 * plugins { id 'net.titan.runelite' version '0.2.0' }
 * </pre>
 *
 * is the whole build. It brings RuneLite's API and plugin framework, Titan's own SDK and Lombok,
 * compiles for Java 11, and adds the {@code net.titan.dev} loop:
 *
 * <pre>
 *   run          build, and open Titan with the plugin; hot reload it when that tab is already open
 *   reload       build, and hot reload the open tab; add -t to do it on every save
 *   runDebug     run with a Java debugger port
 *   titanReport  say which RuneLite calls and events Titan does not supply yet (also part of build)
 * </pre>
 */
class TitanRuneLitePlugin implements Plugin<Project> {
    static final String REPOSITORY = 'https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases'
    private static final String DESCRIPTOR = 'Lnet/runelite/client/plugins/PluginDescriptor;'
    private static final String TITAN_DESCRIPTOR = 'Lnet/titan/api/plugins/PluginDescriptor;'

    @Override
    void apply(Project project) {
        // Titan's managed-instance library is owned by a Titan plugin object, which this is not.
        project.extensions.extraProperties.set('titanBundleAuthorization', 'false')
        project.pluginManager.apply('java')
        project.pluginManager.apply(TitanDevPlugin)

        def versions = new Properties()
        TitanRuneLitePlugin.getResourceAsStream('/net/titan/dev/versions.properties').withCloseable { versions.load(it) }
        def providers = project.providers
        def version = { String property, String key -> providers.gradleProperty(property).getOrElse(versions.getProperty(key)) }
        def repositoryUrl = providers.gradleProperty('titanMavenRepositoryUrl')
            .orElse(providers.environmentVariable('TITAN_MAVEN_REPOSITORY_URL')).getOrElse(REPOSITORY)

        project.repositories.exclusiveContent { content ->
            content.forRepository { project.repositories.maven { it.name = 'titan'; it.url = project.uri(repositoryUrl) } }
            content.filter { it.includeGroup('net.titan') }
        }
        project.repositories.mavenCentral()

        def dependencies = project.dependencies
        def runelite = "net.titan:titan-runelite:${version('titanRuneliteVersion', 'runelite')}"
        def sdk = "net.titan:titan-plugin-api:${version('titanSdkVersion', 'sdk')}"
        def lombok = "org.projectlombok:lombok:${version('lombokVersion', 'lombok')}"
        // Titan has all of these at run time. Nothing of them belongs in the plugin's JAR.
        ['compileOnly', 'testImplementation'].each { configuration ->
            dependencies.add(configuration, runelite)
            // RuneLite's own Guice is the one a RuneLite plugin is built with.
            dependencies.add(configuration, sdk) { transitive = false }
        }
        ['compileOnly', 'annotationProcessor', 'testCompileOnly', 'testAnnotationProcessor'].each { dependencies.add(it, lombok) }

        project.extensions.configure(JavaPluginExtension) {
            it.sourceCompatibility = JavaVersion.VERSION_11
            it.targetCompatibility = JavaVersion.VERSION_11
        }
        project.tasks.withType(JavaCompile).configureEach {
            options.encoding = 'UTF-8'
            options.release = 11
        }
        // The author and version in Titan's plugin list. A project may write the file itself instead.
        def metadata = project.tasks.register('titanPluginProperties') {
            def target = project.layout.buildDirectory.file('generated/titan/runelite-plugin.properties')
            def values = providers.provider {
                [author: (project.findProperty('author') ?: project.group ?: 'Unknown').toString(), version: project.version.toString()]
            }
            inputs.property('values', values)
            outputs.file(target)
            doLast {
                def line = { String text -> text.replace('\\', '\\\\').replace('\r', ' ').replace('\n', ' ') }
                target.get().asFile.setText(values.get().collect { key, value -> "${key}=${line(value)}\n" }.join(''), 'ISO-8859-1')
            }
        }
        project.tasks.named('jar') {
            preserveFileTimestamps = false
            reproducibleFileOrder = true
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            from(metadata)
        }

        def verify = project.tasks.register('verifyTitanPlugin') {
            group = 'titan dev'
            description = 'Check that the JAR holds a RuneLite plugin Titan can load.'
            dependsOn 'jar'
            def jar = project.tasks.named('jar').flatMap { it.archiveFile }
            inputs.file(jar)
            doLast { verifyJar(jar.get().asFile) }
        }
        def report = project.tasks.register('titanReport') {
            group = 'titan dev'
            description = 'Say which RuneLite calls and events of this plugin Titan does not supply yet.'
            dependsOn verify
            def jar = project.tasks.named('jar').flatMap { it.archiveFile }
            doLast { writeReport(project, jar.get().asFile) }
        }
        ['check', 'stageTitanDevPlugin'].each { name -> project.tasks.named(name) { dependsOn verify } }
        ['runTitanClient', 'runViaTitanDebug', 'reloadTitanClient'].each { name -> project.tasks.named(name) { mustRunAfter report } }
        project.tasks.named('build') { dependsOn report }

        [run     : ['runTitanClient', 'Build, and open Titan with this plugin. Hot reloads it when that tab is already open.'],
         reload  : ['reloadTitanClient', 'Build, and hot reload the open Titan tab. Add -t to do it on every save.'],
         runDebug: ['runViaTitanDebug', 'Run with a Java debugger port, 5005 unless titanJavaDebugPort says otherwise.']].each { name, target ->
            project.tasks.register(name) {
                group = 'titan'
                description = target[1]
                dependsOn report, target[0]
            }
        }
    }

    /** Text constants of one class file. Enough to see annotations without loading anything. */
    private static Set<String> texts(InputStream stream) {
        def input = new DataInputStream(new BufferedInputStream(stream))
        if (input.readInt() != (int) 0xCAFEBABE) throw new IOException('Invalid class file')
        input.readInt()
        int count = input.readUnsignedShort()
        Set<String> result = []
        for (int i = 1; i < count; i++) {
            int tag = input.readUnsignedByte()
            switch (tag) {
                case 1: result << input.readUTF(); break
                case 3: case 4: case 9: case 10: case 11: case 12: case 17: case 18: input.skipBytes(4); break
                case 5: case 6: input.skipBytes(8); i++; break
                case 7: case 8: case 16: case 19: case 20: input.skipBytes(2); break
                case 15: input.skipBytes(3); break
                default: throw new IOException("Unsupported class constant tag: ${tag}")
            }
        }
        result
    }

    static void verifyJar(File file) {
        boolean plugin = false
        String titan = null
        List<String> bundled = []
        new JarFile(file).withCloseable { jar ->
            jar.entries().each { entry ->
                if (!entry.name.endsWith('.class') || entry.name.startsWith('META-INF/')) return
                if (entry.name.startsWith('net/runelite/') || entry.name.startsWith('net/titan/')) { bundled << entry.name; return }
                def found = jar.getInputStream(entry).withCloseable { texts(it) }
                plugin |= found.contains(DESCRIPTOR)
                if (found.contains(TITAN_DESCRIPTOR)) titan = entry.name
            }
        }
        if (bundled) throw new GradleException("${file.name} contains classes which Titan supplies itself, such as ${bundled[0]}.\n" +
            'Remove RuneLite and Titan from implementation/shadow dependencies: net.titan.runelite already adds them for compiling.')
        if (titan) throw new GradleException("${titan} is a Titan-style plugin (net.titan.api.plugins.PluginDescriptor).\n" +
            'A net.titan.runelite project builds RuneLite-style plugins: annotate the class with net.runelite.client.plugins.PluginDescriptor\n' +
            'and extend net.runelite.client.plugins.Plugin. Titan\'s SDK is still there: @Inject net.titan.api.Client, or net.titan.api.Titan.\n' +
            'For a Titan-style plugin apply net.titan.dev instead.')
        if (!plugin) throw new GradleException("${file.name} holds no plugin. Annotate one class with " +
            '@net.runelite.client.plugins.PluginDescriptor(name = "...") and extend net.runelite.client.plugins.Plugin.')
    }

    private static void writeReport(Project project, File jar) {
        def logger = project.logger
        File controller
        try { controller = project.extensions.getByType(TitanDevExtension).locateController.call() as File }
        catch (GradleException missing) {
            logger.lifecycle('Titan was not found on this machine, so nothing was checked against its RuneLite support.')
            return
        }
        def java = new File(controller.parentFile, 'java')
        def runtime = new File(java, 'compat/runelite').listFiles()?.findAll { it.name.endsWith('.jar') }?.sort()
        if (!runtime) {
            throw new GradleException("The Titan at ${controller.parentFile} has no RuneLite runtime (java/compat/runelite).\n" +
                'Update it with TitanLauncher, or set titanClientRoot to a build which has one.')
        }
        def executable = new File(System.getProperty('java.home'), System.getProperty('os.name').startsWith('Windows') ? 'bin/java.exe' : 'bin/java')
        def classpath = ([new File(java, 'titan-java-embedded.jar')] + runtime)*.absolutePath.join(File.pathSeparator)
        def output = project.layout.buildDirectory.file('reports/titan/runelite.json').get().asFile
        output.parentFile.mkdirs()
        def process = new ProcessBuilder(executable.absolutePath, '-cp', classpath,
            'net.titan.runelite.tools.CompatibilityCli', 'inspect', jar.absolutePath)
            .redirectOutput(output).redirectError(new File(output.parentFile, 'runelite.log')).start()
        if (process.waitFor() != 0) {
            logger.warn("Titan could not check this plugin. See ${new File(output.parentFile, 'runelite.log')}")
            return
        }
        def lines = problems(new JsonSlurper().parse(output, 'UTF-8') as Map)
        if (!lines) logger.lifecycle('Titan supplies every RuneLite call and event this plugin was checked for.')
        else {
            logger.warn('Not supplied by this Titan yet:')
            lines.each { logger.warn("  ${it}") }
            logger.warn('A missing call throws UnsupportedOperationException, and a missing event is never posted. ' +
                'The rest of the plugin still runs.')
        }
    }

    /** What an inspection says will not work, one line each. */
    static List<String> problems(Map inspection) {
        Set<String> result = new TreeSet<>()
        inspection.each { key, value ->
            if (!(value instanceof Collection)) return
            if (key ==~ /unmapped.*References/) value.each { result << "call   ${readable(it as String)}".toString() }
            if (key == 'unforwardedGameEvents') value.each { result << "event  ${(it as String).replace('net.runelite.api.events.', '')}".toString() }
        }
        result as List
    }
    private static String readable(String reference) {
        int arguments = reference.indexOf('(')
        (arguments < 0 ? reference : reference.substring(0, arguments)).replace('net.runelite.api.', '')
    }
}
