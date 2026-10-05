// Minimal Titan plugin scaffold.
//
// Rename `MyPlugin` and the TITAN_PLUGIN_META arguments to match your
// plugin's identity. The first argument ("my_plugin" below) is the
// stable slug -- it identifies the plugin to the controller and is
// used as the persistence key for its settings. Treat it as permanent.

#include <titan/plugin.h>
#include <titan/setting.h>
#include <titan/client.h>

class MyPlugin : public titan::Plugin {
    TITAN_PLUGIN_META(
        /*id=*/          "my_plugin",
        /*name=*/        "My Plugin",
        /*description=*/ "Starter Titan plugin generated from titan-plugin-template.",
        /*author=*/      "Your Name",
        /*version=*/     "0.1.0",
        /*defaultEnabled=*/ true)

public:
    // Example boolean setting. Shows up in the plugin's settings panel.
    titan::BoolSetting greet{this, "greet", "Log a greeting on every game tick", true};

    MyPlugin() = default;

    void onGameTick(int32_t tick) override {
        if (greet && tick % 50 == 0) {
            titan::logf("my_plugin: hello from tick %d", tick);
        }
    }
};

TITAN_REGISTER_PLUGIN(MyPlugin, "my_plugin")
