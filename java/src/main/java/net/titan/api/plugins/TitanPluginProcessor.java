package net.titan.api.plugins;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Compile-time annotation processor that generates {@code META-INF/titan-plugins}
 * from all {@link PluginDescriptor}-annotated classes in the compilation unit.
 *
 * The store's JarFileReader reads this manifest to discover which plugin IDs
 * a JAR artifact contains, without needing a JVM server-side.
 *
 * Plugin projects pick this up automatically when they declare:
 * <pre>
 *   compileOnly     "net.titan:titan-plugin-api:$version"
 *   annotationProcessor "net.titan:titan-plugin-api:$version"
 * </pre>
 */
@SupportedAnnotationTypes("net.titan.api.plugins.PluginDescriptor")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class TitanPluginProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<String> ids = new LinkedHashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(PluginDescriptor.class)) {
            PluginDescriptor descriptor = element.getAnnotation(PluginDescriptor.class);
            if (descriptor != null && !descriptor.id().isEmpty()) {
                ids.add(descriptor.id());
            }
        }

        if (ids.isEmpty()) {
            return false;
        }

        try {
            Filer filer = processingEnv.getFiler();
            FileObject resource = filer.createResource(
                StandardLocation.CLASS_OUTPUT, "", "META-INF/titan-plugins");
            try (Writer writer = resource.openWriter()) {
                for (String id : ids) {
                    writer.write(id);
                    writer.write('\n');
                }
            }
        } catch (IOException e) {
            // If the file already exists (incremental compilation), ignore.
            // The Filer throws FilerException on duplicate creation attempts.
        }

        return false;
    }
}
