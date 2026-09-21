package net.titan.api.config;

/// Common metadata contract shared by every typed plugin setting holder
/// ({@link BoolSetting}, {@link IntSetting}, {@link StringSetting},
/// {@link ColorSetting}, {@link ComboSetting}). Mirrors the multi-type
/// setting surface of the C++ {@code setting.h} SDK.
public interface ConfigSetting {
    enum Kind {
        BOOLEAN,
        INTEGER,
        STRING,
        COLOR,
        COMBO,
        PROTECTED_STRING,
        /// Value-less action button. Clicking it runs the plugin's attached
        /// runnable ({@link ButtonSetting}) instead of storing a value.
        BUTTON,
        /// Checkbox grid ({@link MatrixSetting}). The value is a cell bitmask,
        /// {@code bit = row * columnCount + column}.
        MATRIX
    }

    String key();
    String name();
    String section();
    String description();
    int position();
    boolean hidden();

    /// Show or hide this setting in the controller panel at runtime, the
    /// Java counterpart of C++ {@code titan::Setting::setHidden} and JS
    /// {@code setting.isHidden}. The declared {@link ConfigItem#hidden()}
    /// is the starting state.
    ///
    /// The value is untouched and stays saved, so a setting hidden behind a
    /// mode switch keeps whatever the user last chose. Plugin code reaches
    /// this through {@link ConfigManager#setHidden}, which also tells the
    /// host to repaint; calling it on a holder directly changes nothing on
    /// screen until the next snapshot.
    void setHidden(boolean hidden);

    Kind kind();
}
