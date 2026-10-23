#pragma once
#include <cstddef>

#include <array>
#include <cstdint>
#include <type_traits>

namespace titan::catalog {
// Pointer-free data export: hosts read this from PE file bytes without mapping
// the image or executing constructors. This is independent of Native ABI v1.
inline constexpr uint32_t kMagic = 0x54434C31;
inline constexpr uint32_t kVersion = 1;
inline constexpr const char* kExport = "TitanPluginCatalog";
struct Entry {
    char id[128]{};
    char name[128]{};
    char description[1024]{};
    char author[128]{};
    char version[64]{};
    char dependencies[2048]{}; // newline-separated ids
    uint32_t defaultEnabled = 0;
    uint32_t preload = 1; // conservative for legacy/custom declarations
};
struct Header {
    uint32_t magic = kMagic, version = kVersion;
    uint32_t entrySize = sizeof(Entry), count = 0;
};
template <size_t N> struct Catalog { Header header{kMagic, kVersion, sizeof(Entry), N}; Entry entries[N]; };
template <size_t N> constexpr void copy(char (&out)[N], const char* value) {
    if (!value) return;
    size_t i = 0;
    while (value[i] && i + 1 < N) { out[i] = value[i]; ++i; }
    if (value[i]) throw "Plugin catalog field exceeds capacity";
}
constexpr Entry metadata(const char* id, const char* name, const char* description,
                        const char* author, const char* version, bool enabled) {
    Entry e{};
    copy(e.id, id); copy(e.name, name); copy(e.description, description);
    copy(e.author, author); copy(e.version, version);
    e.defaultEnabled = enabled;
    return e;
}
template <typename Plugin, typename Base> constexpr Entry entry() {
    Entry e{};
    if constexpr (requires { Plugin::catalogMetadata(); }) {
        e = Plugin::catalogMetadata();
        // Hand-written dependency overrides need an explicit declaration;
        // otherwise keep their old eager behavior rather than guessing.
        e.preload = !std::is_same_v<decltype(&Plugin::dependencies), decltype(&Base::dependencies)>;
        if constexpr (requires { Plugin::kCatalogDependencies; }) {
            e.preload = 0;
            size_t pos = 0;
            for (const auto* id : Plugin::kCatalogDependencies) {
                if (pos) e.dependencies[pos++] = '\n';
                for (const char* p = id; *p; ++p) {
                    if (pos + 1 >= sizeof(e.dependencies)) throw "Too many plugin dependencies";
                    e.dependencies[pos++] = *p;
                }
            }
        }
        if constexpr (requires { Plugin::kCatalogPreload; }) e.preload = Plugin::kCatalogPreload;
    } else {
        copy(e.id, Plugin::kPluginId); copy(e.name, Plugin::kPluginId);
    }
    return e;
}
template <typename Base, typename... Plugins> constexpr auto make() {
    return Catalog<sizeof...(Plugins)>{{kMagic, kVersion, sizeof(Entry), sizeof...(Plugins)},
        {entry<Plugins, Base>()...}};
}
} // namespace titan::catalog
