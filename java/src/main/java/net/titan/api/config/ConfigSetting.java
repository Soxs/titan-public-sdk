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
        PROTECTED_STRING
    }

    String key();
    String name();
    String section();
    String description();
    int position();
    boolean hidden();
    Kind kind();
}
