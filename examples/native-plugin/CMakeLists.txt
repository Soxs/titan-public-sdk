cmake_minimum_required(VERSION 3.24)
project(my_plugin LANGUAGES CXX)

# Rename `my_plugin` above and in the titan_add_plugin / titan_add_dev_session
# calls below to your plugin's slug. The slug must match the first argument
# of TITAN_PLUGIN_META in your source file.

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Optional override: cache value > environment > bundled parent SDK. Otherwise
# acquire only the pinned C++ SDK files into build/_deps, without the Maven repo.
set(TITAN_PLUGIN_SDK_ROOT "" CACHE PATH "Optional existing titan-public-sdk checkout")
include(cmake/AcquireTitanSdk.cmake)
titan_acquire_sdk(TITAN_PLUGIN_SDK_ROOT)

# Run discovers the normal Titan installation when this override is unset.
# Configure/build does not require an installed runtime.
set(TITAN_CLIENT_ROOT "$ENV{TITAN_CLIENT_ROOT}" CACHE PATH
    "Optional TitanClient runtime directory containing controller.exe")
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
