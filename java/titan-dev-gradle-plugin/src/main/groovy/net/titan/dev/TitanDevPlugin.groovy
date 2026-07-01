package net.titan.dev

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * {@code net.titan.dev} -- one-line onboarding for the TitanClient Java
 * plugin hot-reload dev loop.
 *
 * Applying this plugin (alongside {@code java}/{@code java-library}) adds the
 * Titan dev-session tasks that used to be copy-pasted into every plugin's
 * build.gradle: it stages the built JAR into a per-session {@code gen-N}
 * directory under {@code build/.titan/dev/<slug>/load/}, writes the
 * {@code session.json} manifest the controller reads, and launches/recycles a
 * dedicated DEV tab. After the first launch, a plain {@code build} restages the
 * next generation so the in-tab refresh button hot-reloads it.
 *
 * Tasks (group "titan dev"):
 *   stageTitanDevPlugin       build + stage the JAR into the next gen-N dir
 *   writeTitanDevSession      stage + write session.json
 *   writeTitanDevSessionDebug stage + write session.json with JDWP enabled
 *   runTitanClient            stage + write manifest + launch/recycle the DEV tab
 *   runViaTitan               alias for runTitanClient
 *   runViaTitanDebug          runTitanClient with JDWP on (default port 5005)
 *   stagePlugin               legacy: stage into ~/.titanclient/plugins
 */
class TitanDevPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def ext = project.extensions.create('titanDev', TitanDevExtension)
        def providers = project.providers
        def logger = project.logger
        def isWindows = System.getProperty('os.name').toLowerCase(Locale.ROOT).contains('windows')
        def titanLaunchTaskName = 'runViaTitan'

        def trimOrNull = { String value ->
            if (value == null) return null
            def trimmed = value.trim()
            return trimmed.isEmpty() ? null : trimmed
        }

        def slugify = { String value ->
            def source = trimOrNull(value) ?: 'java-plugin'
            def slug = source.toLowerCase(Locale.ROOT)
                .replaceAll(/[^a-z0-9_.-]+/, '-')
                .replaceAll(/^[-.]+/, '')
                .replaceAll(/[-.]+$/, '')
            if (slug.isEmpty()) {
                slug = 'java-plugin'
            }
            if (slug.length() <= 63) {
                return slug
            }
            def digest = MessageDigest.getInstance('SHA-1')
                .digest(slug.getBytes(StandardCharsets.UTF_8))
                .encodeHex()
                .toString()
                .substring(0, 8)
            def prefix = slug.substring(0, 54).replaceAll(/[-.]+$/, '')
            if (prefix.isEmpty()) {
                prefix = 'java-plugin'
            }
            "${prefix}-${digest}"
        }

        // Resolved lazily (at execution time) so the titanDev { } block and any
        // late-set Gradle properties are honoured.
        def resolveSessionSlug = {
            def explicit = trimOrNull(ext.sessionSlug.getOrNull())
                ?: trimOrNull(providers.gradleProperty('titanDevSessionSlug').orNull)
                ?: trimOrNull(providers.environmentVariable('TITAN_DEV_SESSION_SLUG').orNull)
                // Back-compat for early copies of the sample. Not required and
                // no longer expected to match any @PluginDescriptor id.
                ?: trimOrNull(providers.gradleProperty('titanPluginSlug').orNull)
                ?: trimOrNull(providers.environmentVariable('TITAN_PLUGIN_SLUG').orNull)
            slugify(explicit ?: project.name)
        }

        def normalizedPath = { File file ->
            file.toPath().toAbsolutePath().normalize().toString().replace('\\', '/')
        }

        def expandTitanPath = { String rawPath ->
            def value = trimOrNull(rawPath)
            if (value == null) {
                return null
            }

            def userProfile = System.getenv('USERPROFILE') ?: System.getProperty('user.home')
            value = value.replace('%USERPROFILE%', userProfile)
                .replace('%userprofile%', userProfile)

            if (value == '~') {
                return System.getProperty('user.home')
            }
            if (value.startsWith('~/') || value.startsWith('~\\')) {
                return new File(System.getProperty('user.home'), value.substring(2)).path
            }
            return value
        }

        def launcherReleaseFromRepositoryRoot = { File repositoryRoot ->
            if (repositoryRoot == null || !isWindows) {
                return null
            }

            def stateJson = new File(repositoryRoot, 'state.json')
            if (!stateJson.isFile()) {
                return null
            }

            try {
                def state = new JsonSlurper().parse(stateJson)
                def version = state?.current_version?.toString()
                if (version == null || !version.matches(/[A-Za-z0-9._-]{1,64}/)) {
                    return null
                }

                def candidate = new File(repositoryRoot, "releases/${version}")
                if (new File(candidate, 'controller.exe').isFile() && new File(candidate, 'manifest.json').isFile()) {
                    return candidate
                }
            } catch (ignored) {
                return null
            }

            return null
        }

        def configuredTitanClientRoot = {
            def fromExtension = trimOrNull(ext.clientRoot.getOrNull())
            if (fromExtension != null) {
                return expandTitanPath(fromExtension)
            }
            def gradleProperty = trimOrNull(providers.gradleProperty('titanClientRoot').orNull)
            if (gradleProperty != null) {
                return expandTitanPath(gradleProperty)
            }
            def environmentVariable = trimOrNull(providers.environmentVariable('TITAN_CLIENT_ROOT').orNull)
            if (environmentVariable != null) {
                return expandTitanPath(environmentVariable)
            }
            return null
        }

        def launcherRepositoryClientRoot = {
            launcherReleaseFromRepositoryRoot(new File(System.getProperty('user.home'), '.titanclient/repository'))
        }

        def titanClientRootCandidates = {
            def candidates = []
            def addCandidate = { File candidate ->
                if (candidate != null) {
                    candidates.add(candidate)
                }
            }

            addCandidate(launcherRepositoryClientRoot())

            if (isWindows) {
                def programFiles = System.getenv('ProgramFiles')
                if (programFiles) {
                    addCandidate(new File(programFiles, 'TitanClient'))
                }

                def programFilesX86 = System.getenv('ProgramFiles(x86)')
                if (programFilesX86) {
                    addCandidate(new File(programFilesX86, 'TitanClient'))
                }

                def localAppData = System.getenv('LOCALAPPDATA')
                if (localAppData) {
                    addCandidate(new File(localAppData, 'TitanClient'))
                    addCandidate(new File(localAppData, 'Programs/TitanClient'))
                }
            }

            def cursor = project.projectDir
            while (cursor != null) {
                addCandidate(new File(cursor, 'build/controller/Debug'))
                addCandidate(new File(cursor, 'build/controller/RelWithDebInfo'))
                addCandidate(new File(cursor, 'build/controller/Release'))
                cursor = cursor.parentFile
            }

            def seen = [] as Set
            candidates.findAll { seen.add(it.absolutePath.toLowerCase(Locale.ROOT)) }
        }

        def titanClientRootHelp = { File checkedController = null ->
            def lines = []
            if (checkedController != null) {
                lines << "TitanClient controller.exe was not found."
                lines << "Checked: ${checkedController.absolutePath}"
            } else {
                lines << "TitanClient runtime path is not configured, and no launcher repository/common install/source-build location was auto-detected."
            }
            lines << ''
            lines << "${titanLaunchTaskName} needs either the TitanLauncher repository root or the folder that directly contains controller.exe."
            lines << 'Use the repository/runtime folder path, not controller.exe itself and not the plugins folder.'
            lines << ''
            lines << 'Run TitanLauncher.exe once so it syncs a release into:'
            lines << '  %USERPROFILE%\\.titanclient\\repository'
            lines << ''
            lines << 'Set it in the titanDev block in build.gradle:'
            lines << '  titanDev { clientRoot = "%USERPROFILE%/.titanclient/repository" }'
            lines << ''
            lines << 'Or edit the titanClientRoot entry in gradle.properties:'
            lines << '  titanClientRoot=%USERPROFILE%/.titanclient/repository'
            lines << "  .\\gradlew.bat ${titanLaunchTaskName}"
            lines << ''
            lines << 'Or set it for this PowerShell session:'
            lines << '  $env:TITAN_CLIENT_ROOT = "%USERPROFILE%/.titanclient/repository"'
            lines << "  .\\gradlew.bat ${titanLaunchTaskName}"
            lines << ''
            lines << 'One-off Gradle property:'
            lines << "  .\\gradlew.bat ${titanLaunchTaskName} \"-PtitanClientRoot=%USERPROFILE%/.titanclient/repository\""
            lines.join(System.lineSeparator())
        }

        def resolveTitanController = {
            def configuredRoot = configuredTitanClientRoot()
            if (configuredRoot) {
                def configuredRootFile = new File(configuredRoot)
                def runtimeRoot = launcherReleaseFromRepositoryRoot(configuredRootFile) ?: configuredRootFile
                def controller = new File(runtimeRoot, 'controller.exe')
                if (!controller.isFile()) {
                    throw new GradleException(titanClientRootHelp(controller))
                }
                return controller
            }

            def controller = titanClientRootCandidates()
                .collect { new File(it, 'controller.exe') }
                .find { it.isFile() }
            if (controller != null) {
                logger.lifecycle("Auto-detected TitanClient at ${controller.parentFile}")
                return controller
            }

            throw new GradleException(titanClientRootHelp(null))
        }

        def titanEmbeddedJarFromEnvironment = {
            def value = providers.environmentVariable('TITAN_JAVA_EMBEDDED_JAR').orNull
            if (value == null || value.trim().isEmpty()) {
                return null
            }
            return new File(value.trim())
        }

        def titanEmbeddedHelp = { File controller, File checkedRuntime = null ->
            def lines = []
            lines << 'TitanClient embedded Java runtime was not found.'
            if (checkedRuntime != null) {
                lines << "Checked: ${checkedRuntime.absolutePath}"
            }
            lines << ''
            lines << 'Java plugins need titan-java-embedded.jar for metadata inspection and the in-process runtime.'
            lines << 'Use an installed TitanClient build that includes the Java runtime, or point clientRoot at a source build staged with Java enabled.'
            lines << ''
            lines << 'Expected installed/source-build layout:'
            lines << "  ${new File(controller.parentFile, 'java/titan-java-embedded.jar').absolutePath}"
            lines << ''
            lines << 'Maintainers can also set TITAN_JAVA_EMBEDDED_JAR to an absolute runtime JAR path.'
            lines.join(System.lineSeparator())
        }

        def verifyTitanJavaEmbedded = { File controller ->
            def envRuntime = titanEmbeddedJarFromEnvironment()
            if (envRuntime != null) {
                if (!envRuntime.isFile()) {
                    throw new GradleException(titanEmbeddedHelp(controller, envRuntime))
                }
                return
            }

            def colocatedRuntime = new File(controller.parentFile, 'java/titan-java-embedded.jar')
            if (!colocatedRuntime.isFile()) {
                throw new GradleException(titanEmbeddedHelp(controller, colocatedRuntime))
            }
        }

        def titanDevRoot = {
            project.layout.buildDirectory.dir(".titan/dev/${resolveSessionSlug()}").get().asFile
        }

        def titanDevLoadRoot = {
            new File(titanDevRoot(), 'load')
        }

        def titanDevSessionFile = {
            new File(titanDevRoot(), 'session.json')
        }

        def titanDevSessionExists = {
            titanDevSessionFile().isFile() ||
                new File(titanDevRoot(), 'session_id.txt').isFile()
        }

        def ensureSessionId = {
            def root = titanDevRoot()
            root.mkdirs()
            def sessionFile = new File(root, 'session_id.txt')
            if (sessionFile.isFile()) {
                def existing = sessionFile.text.trim()
                if (existing ==~ /[0-9a-fA-F-]+/) {
                    return existing
                }
            }
            def created = UUID.randomUUID().toString()
            sessionFile.text = created + System.lineSeparator()
            created
        }

        def readGeneration = {
            def genFile = new File(titanDevRoot(), 'gen.txt')
            if (!genFile.isFile()) return 0
            try {
                return Integer.parseInt(genFile.text.trim())
            } catch (Exception ignored) {
                return 0
            }
        }

        def generationNumber = { File dir ->
            def matcher = dir.name =~ /^gen-(\d+)$/
            if (!matcher.matches()) return -1
            Integer.parseInt(matcher.group(1))
        }

        def pruneStaleGenerations = { File loadRoot, int keep ->
            if (!loadRoot.isDirectory()) return
            def generations = loadRoot.listFiles()
                ?.findAll { it.isDirectory() && generationNumber(it) >= 0 }
                ?.sort { a, b -> generationNumber(b) <=> generationNumber(a) } ?: []
            generations.drop(keep).each {
                try {
                    project.delete(it)
                } catch (Exception ex) {
                    logger.lifecycle("Keeping locked Titan dev generation ${it}: ${ex.message}")
                }
            }
        }

        def stageTitanDevGeneration = {
            def sessionId = ensureSessionId()
            def nextGeneration = readGeneration() + 1
            def root = titanDevRoot()
            def loadRoot = titanDevLoadRoot()
            def devPluginDir = new File(loadRoot, "gen-${nextGeneration}")
            def jarFile = project.tasks.named('jar').get().archiveFile.get().asFile

            devPluginDir.mkdirs()
            project.copy {
                from jarFile
                into devPluginDir
            }
            new File(root, 'gen.txt').text = nextGeneration.toString() + System.lineSeparator()
            pruneStaleGenerations(loadRoot, 5)

            logger.lifecycle("Staged ${jarFile.name} for Titan dev session ${sessionId} into ${devPluginDir}")
            [
                sessionId   : sessionId,
                generation  : nextGeneration,
                devPluginDir: devPluginDir,
                jarFile     : jarFile
            ]
        }

        def resolveJavaDebugPort = { Integer defaultPort ->
            def raw = trimOrNull(providers.gradleProperty('titanJavaDebugPort').orNull)
                ?: trimOrNull(providers.environmentVariable('TITAN_JAVA_DEBUG_PORT').orNull)
            if (raw == null) {
                def fromExtension = ext.javaDebugPort.getOrNull()
                if (fromExtension != null) return fromExtension
            }
            if (raw == null && defaultPort != null) return defaultPort
            if (raw == null) return null
            try {
                def port = Integer.parseInt(raw)
                if (port <= 0 || port > 65535) {
                    throw new NumberFormatException('port out of range')
                }
                return port
            } catch (Exception ignored) {
                throw new GradleException(
                    "titanJavaDebugPort/TITAN_JAVA_DEBUG_PORT must be an integer from 1 to 65535: ${raw}")
            }
        }

        def writeDevSessionManifest = { Integer defaultDebugPort ->
            def controller = resolveTitanController()
            def root = titanDevRoot()
            def generation = readGeneration()
            if (generation <= 0) {
                throw new GradleException('No staged Titan dev generation found. Run stageTitanDevPlugin first.')
            }

            def jarFile = project.tasks.named('jar').get().archiveFile.get().asFile
            def devPluginDir = new File(titanDevLoadRoot(), "gen-${generation}")
            def stagedJar = new File(devPluginDir, jarFile.name)
            if (!stagedJar.isFile()) {
                throw new GradleException("Staged plugin JAR was not found: ${stagedJar}")
            }

            def slug = resolveSessionSlug()
            def manifest = [
                schema_version    : 1,
                session_id        : ensureSessionId(),
                slug              : slug,
                source_root       : normalizedPath(project.projectDir),
                build_config      : providers.gradleProperty('titanBuildConfig').orElse('Java').get(),
                sdk_version       : 0,
                titan_install_root: normalizedPath(controller.parentFile),
                dev_plugin_dir    : normalizedPath(devPluginDir),
                staged_jar        : normalizedPath(stagedJar),
                loader_mode       : 'DebuggableLoadLibrary',
                generation        : generation
            ]

            def debugPort = resolveJavaDebugPort(defaultDebugPort)
            if (debugPort != null) {
                manifest.java_debug_port = debugPort
            }

            root.mkdirs()
            titanDevSessionFile().text =
                JsonOutput.prettyPrint(JsonOutput.toJson(manifest)) + System.lineSeparator()
            def debugSuffix = debugPort == null ? '' : ", jdwp=${debugPort}"
            logger.lifecycle("Wrote Titan dev session ${titanDevSessionFile()} (session=${slug}, gen=${generation}${debugSuffix})")
        }

        // Tasks need the `jar`/`build` tasks, so register once the java plugin
        // is applied. Works for both `java` and `java-library`.
        project.pluginManager.withPlugin('java') {
            def titanPluginDir = project.file(providers.gradleProperty('titanPluginDir')
                .orElse(providers.environmentVariable('TITAN_PLUGIN_DIR'))
                .orElse(new File(System.getProperty('user.home'), '.titanclient/plugins').absolutePath)
                .get())

            project.tasks.register('stagePlugin', Copy) {
                group = 'titan dev'
                description = 'Legacy: build and stage this plugin into the per-user TitanClient DEV plugin directory.'
                dependsOn project.tasks.named('jar')
                from project.tasks.named('jar').flatMap { it.archiveFile }
                into titanPluginDir
                doLast {
                    logger.lifecycle("Staged ${project.tasks.jar.archiveFile.get().asFile.name} into ${titanPluginDir}")
                }
            }

            project.tasks.register('stageTitanDevPlugin') {
                group = 'titan dev'
                description = 'Build and stage this plugin into a Titan dev-session generation directory.'
                dependsOn project.tasks.named('jar')
                outputs.upToDateWhen { false }
                doLast {
                    stageTitanDevGeneration()
                }
            }

            project.tasks.named('build') {
                doLast {
                    if (!titanDevSessionExists()) return
                    try {
                        stageTitanDevGeneration()
                        writeDevSessionManifest(null)
                        logger.lifecycle("Refreshed active Titan dev session after build; click the in-tab refresh button to reload.")
                    } catch (Exception ex) {
                        logger.lifecycle("Skipped Titan dev-session refresh after build: ${ex.message}")
                    }
                }
            }

            project.tasks.register('writeTitanDevSession') {
                group = 'titan dev'
                description = 'Write the Titan dev-session manifest for the latest staged Java plugin generation.'
                dependsOn project.tasks.named('stageTitanDevPlugin')
                outputs.upToDateWhen { false }
                doLast {
                    writeDevSessionManifest(null)
                }
            }

            project.tasks.register('writeTitanDevSessionDebug') {
                group = 'titan dev'
                description = 'Write the Titan dev-session manifest with JDWP enabled for the latest staged Java plugin generation.'
                dependsOn project.tasks.named('stageTitanDevPlugin')
                outputs.upToDateWhen { false }
                doLast {
                    writeDevSessionManifest(5005)
                }
            }

            project.tasks.register('runTitanClient', Exec) {
                group = 'titan dev'
                description = 'Stage this plugin and launch/recycle its dedicated TitanClient DEV tab.'
                dependsOn project.tasks.named('writeTitanDevSession')
                doFirst {
                    def controller = resolveTitanController()
                    verifyTitanJavaEmbedded(controller)
                    workingDir controller.parentFile
                    commandLine controller, '--dev-mode',
                        '--dev-manifest', titanDevSessionFile(),
                        '--dev-session', resolveSessionSlug(),
                        '--launch-new-client'
                }
            }

            project.tasks.register('runViaTitan') {
                group = 'titan dev'
                description = 'Alias for runTitanClient.'
                dependsOn project.tasks.named('runTitanClient')
            }

            project.tasks.register('runViaTitanDebug', Exec) {
                group = 'titan dev'
                description = 'Stage this plugin, launch/recycle its Titan DEV tab, and enable JDWP on port 5005 by default.'
                dependsOn project.tasks.named('writeTitanDevSessionDebug')
                doFirst {
                    def controller = resolveTitanController()
                    verifyTitanJavaEmbedded(controller)
                    workingDir controller.parentFile
                    commandLine controller, '--dev-mode',
                        '--dev-manifest', titanDevSessionFile(),
                        '--dev-session', resolveSessionSlug(),
                        '--launch-new-client'
                }
            }
        }
    }
}
