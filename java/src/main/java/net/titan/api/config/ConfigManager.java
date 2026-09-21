package net.titan.api.config;

/// Programmatic writes to a plugin's own config, the Java counterpart of the
/// C++ {@code titan::Setting::set} members and the JS {@code setting.value}
/// accessor.
///
/// The injected {@link Config} proxy is read-only by design -- its methods are
/// annotated getters -- so this is how a plugin changes a value it declared.
/// Inject it like any other service:
///
/// <pre>
/// public class MyPlugin extends Plugin {
///     &#64;Inject private MyConfig config;
///     &#64;Inject private ConfigManager configManager;
///
///     &#64;Override
///     public void onGameTick(int tick) {
///         if (config.tripCount() &gt;= config.maxTrips()) {
///             configManager.set(MyConfig.class, "enabled", false);
///         }
///     }
/// }
/// </pre>
///
/// A write here is saved to the user's controller config exactly as an edit
/// made in the side panel is, so it survives a restart. Values pushed down BY
/// the controller do not come back through this interface, which is what keeps
/// a plugin write distinguishable from the host replaying saved state.
///
/// Every method takes the config INTERFACE plus the {@link ConfigItem#keyName()}
/// of the item, and returns false when the key is unknown, the plugin does not
/// own that config, or the value does not match the item's declared type --
/// never a thrown exception, so a stale key cannot kill a game tick.
///
/// {@code set} is overloaded only for the two primitive shapes, which can
/// never be null and so can never be ambiguous. The reference-typed writes
/// have distinct names ({@code setString}, {@code setEnum}, {@code setGrid}):
/// as overloads of {@code set} a bare {@code null} argument would match all
/// three and fail to compile at the call site.
///
/// Thread-safe. Writes may be made from any thread; the controller coalesces
/// repeated writes into a single save.
public interface ConfigManager {
    /// Write a {@code boolean} item (checkbox).
    <T extends Config> boolean set(Class<T> configType, String keyName, boolean value);

    /// Write an {@code int} item: slider, colour (packed 0xRRGGBB) or the raw
    /// cell bitmask of a checkbox grid.
    <T extends Config> boolean set(Class<T> configType, String keyName, int value);

    /// Write a {@code String} item, including a {@link ConfigItem#secret()} one.
    /// A null value writes the empty string.
    <T extends Config> boolean setString(Class<T> configType, String keyName,
                                         String value);

    /// Write an enum-backed combo item. The constant's ordinal is stored, which
    /// is what the combo's options are built from.
    <T extends Config> boolean setEnum(Class<T> configType, String keyName,
                                       Enum<?> value);

    /// Write a checkbox grid from a row-major grid. Cells outside the declared
    /// shape, and cells the plugin marked unavailable, are dropped.
    <T extends Config> boolean setGrid(Class<T> configType, String keyName,
                                       boolean[][] grid);

    /// Set one checkbox-grid cell. No-op (returns false) for an out-of-range or
    /// unavailable cell.
    <T extends Config> boolean setCell(Class<T> configType, String keyName,
                                       int row, int column, boolean on);

    /// Flip one checkbox-grid cell and return its NEW state. False also
    /// means "nothing happened" for an out-of-range or unavailable cell --
    /// the same contract as {@link MatrixSetting#toggle}, JS
    /// {@code matrixSetting.toggle} and C++ {@code MatrixSetting::toggleCell}.
    <T extends Config> boolean toggleCell(Class<T> configType, String keyName,
                                          int row, int column);

    /// Restore an item to the default its declaration gave, and save that.
    <T extends Config> boolean reset(Class<T> configType, String keyName);

    /// Show or hide an item in the controller panel. Presentation only: the
    /// value is untouched and stays saved, so a setting hidden behind a mode
    /// switch keeps what the user last chose.
    <T extends Config> boolean setHidden(Class<T> configType, String keyName,
                                         boolean hidden);

    /// Current visibility, or false when the key is unknown.
    <T extends Config> boolean isHidden(Class<T> configType, String keyName);
}
