# shared/CMakeLists.txt
#
# Root of the Titan plugin SDK. This file is identical in the in-tree
# SoxClientOSRS repo, its private full `titan-plugin-sdk` mirror, and the
# allowlisted public `titan-public-sdk` export.
#
# Consumed in two ways:
#
#   1. In-tree: the workspace root CMakeLists.txt does
#         add_subdirectory(shared)
#      The `titan_sdk` INTERFACE target is declared here so in-tree
#      plugins, the controller, and the injected client share a single
#      source of truth for include paths. The in-tree
#      `cmake/titan_plugin.cmake` helper is included BEFORE this file
#      runs, so the COMMAND-already-defined guard below keeps that
#      in-tree helper authoritative (output dir: `build/plugins/...`).
#
#   2. External plugin projects: the third-party root CMakeLists.txt
#      does
#         add_subdirectory(${TITAN_PLUGIN_SDK_ROOT} titan_sdk)
#      where `TITAN_PLUGIN_SDK_ROOT` points at a checkout of the
#      public `titan-public-sdk` checkout. No in-tree helper is present,
#      so the SDK-side `titan_add_plugin` / `titan_add_dev_session`
#      helpers under `cmake/` take effect. They stage output to
#      `${CMAKE_BINARY_DIR}/.titan/dev/<slug>/<config>/load/gen-N/` and publish a
#      dev-session manifest the controller consumes via
#      `--dev-manifest`.

option(TITAN_PROFILE_PERFORMANCE "Compile optional Titan and plugin performance probes" OFF)

if(NOT TARGET titan_sdk)
    add_library(titan_sdk INTERFACE)
    target_compile_definitions(titan_sdk INTERFACE
        $<$<BOOL:${TITAN_PROFILE_PERFORMANCE}>:TITAN_PROFILE_PERFORMANCE=1>)
    # Expose only the SDK root. Private integration code adds its own include
    # paths; an external plugin cannot accidentally find headers beside the SDK.
    target_include_directories(titan_sdk INTERFACE
        ${CMAKE_CURRENT_LIST_DIR}
    )
    target_compile_features(titan_sdk INTERFACE cxx_std_20)
    target_compile_options(titan_sdk INTERFACE
        $<$<CXX_COMPILER_ID:MSVC>:/bigobj>
    )
endif()

# SDK-side CMake helpers. The in-tree adapter shares the staging implementation
# while preserving its normal DLL output under `build/plugins/...`.
if(NOT COMMAND titan_add_plugin)
    include(${CMAKE_CURRENT_LIST_DIR}/cmake/titan_plugin.cmake)
endif()
if(NOT COMMAND titan_add_dev_session)
    include(${CMAKE_CURRENT_LIST_DIR}/cmake/titan_dev_session.cmake)
endif()
