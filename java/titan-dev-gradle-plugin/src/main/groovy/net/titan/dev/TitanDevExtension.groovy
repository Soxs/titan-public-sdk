package net.titan.dev

import org.gradle.api.provider.Property

/**
 * Configuration for the {@code net.titan.dev} convention plugin.
 *
 * All values are optional. When unset the plugin falls back to the same
 * Gradle properties / environment variables the legacy inline dev block used,
 * so existing {@code gradle.properties} setups keep working unchanged:
 *
 * <pre>
 * titanDev {
 *     // Folder that directly contains controller.exe. Falls back to
 *     // -PtitanClientRoot / TITAN_CLIENT_ROOT, then common install and local
 *     // source-build locations.
 *     clientRoot = 'C:/Program Files/TitanClient'
 *
 *     // Dev-session slug. Falls back to -PtitanDevSessionSlug /
 *     // TITAN_DEV_SESSION_SLUG (then the legacy titanPluginSlug), else the
 *     // Gradle project name. Need not match any @PluginDescriptor id.
 *     sessionSlug = 'my-plugin'
 *
 *     // Default JDWP port for runViaTitanDebug. Falls back to
 *     // -PtitanJavaDebugPort / TITAN_JAVA_DEBUG_PORT, else 5005.
 *     javaDebugPort = 5005
 * }
 * </pre>
 */
abstract class TitanDevExtension {
    abstract Property<String> getClientRoot()
    abstract Property<String> getSessionSlug()
    abstract Property<Integer> getJavaDebugPort()
}
