cmake_minimum_required(VERSION 3.24)
project(my_plugin LANGUAGES CXX)

# Rename `my_plugin` above and in the titan_add_plugin / titan_add_dev_session
# calls below to your plugin's slug. The slug must match the first argument
# of TITAN_PLUGIN_META in your source file.

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Select an SDK checkout with an env var or -D cache var. When this example
# lives under titan-public-sdk/examples/native-plugin, its parent SDK is used
# automatically. A standalone copy needs an explicit SDK path:
#
#   TITAN_PLUGIN_SDK_ROOT  -> your titan-public-sdk checkout
#                            git clone https://github.com/Soxs/titan-public-sdk.git
#
# TITAN_CLIENT_ROOT is optional. When unset, the dev loop reads the
# launcher repository state at `%USERPROFILE%/.titanclient/repository`
# and uses the latest synced release containing controller.exe. Set it
# only when you want to target a custom install or local source build.
#
# Per-build overrides via `cmake -DTITAN_PLUGIN_SDK_ROOT=... -DTITAN_CLIENT_ROOT=...`
# still work and take priority over the env vars.
set(_titan_default_sdk "$ENV{TITAN_PLUGIN_SDK_ROOT}")
if(NOT _titan_default_sdk AND
   EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/../../CMakeLists.txt" AND
   EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/../../titan/plugin.h")
    get_filename_component(_titan_default_sdk "${CMAKE_CURRENT_SOURCE_DIR}/../.." ABSOLUTE)
endif()
set(TITAN_PLUGIN_SDK_ROOT "${_titan_default_sdk}" CACHE PATH
    "titan-public-sdk checkout (environment override or bundled example's parent SDK)")
set(TITAN_CLIENT_ROOT "$ENV{TITAN_CLIENT_ROOT}" CACHE PATH
    "Optional TitanClient runtime directory containing controller.exe")

if(NOT TITAN_PLUGIN_SDK_ROOT)
    message(FATAL_ERROR
        "TITAN_PLUGIN_SDK_ROOT is not set. Clone https://github.com/Soxs/titan-public-sdk.git "
        "and pass `-DTITAN_PLUGIN_SDK_ROOT=C:/path/to/titan-public-sdk` to cmake, "
        "or set the matching environment variable. See README.md.")
endif()
# Pulls in the titan_sdk INTERFACE library AND the titan_add_plugin /
# titan_add_dev_session helper functions. The SDK exports its own
# CMakeLists.txt at the root so this is the one and only entry point.
add_subdirectory("${TITAN_PLUGIN_SDK_ROOT}" titan_sdk EXCLUDE_FROM_ALL)

# Declare the plugin DLL. SLUG must match TITAN_PLUGIN_META's first arg.
titan_add_plugin(
    TARGET  my_plugin
    SLUG    my_plugin
    SOURCES src/my_plugin.cpp
)

# Add stage/run/reload/watch targets. Changed successful builds publish an
# immutable DLL/PDB generation; Run reuses the live native development tab.
# Configuration succeeds even before a runtime is installed.
titan_add_dev_session(
    PLUGIN      my_plugin
    LOADER_MODE DebuggableLoadLibrary
)
