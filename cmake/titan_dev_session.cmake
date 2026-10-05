# Native development targets shared by SDK consumers and repository plugins.
include_guard(GLOBAL)

function(_titan_detect_launcher_repository_root out_var)
    set(_detected "")
    set(_state "$ENV{USERPROFILE}/.titanclient/repository/state.json")
    if(EXISTS "${_state}")
        file(READ "${_state}" _json)
        string(JSON _version ERROR_VARIABLE _error GET "${_json}" current_version)
        if(NOT _error AND _version MATCHES "^[A-Za-z0-9._-]+$")
            set(_candidate "$ENV{USERPROFILE}/.titanclient/repository/releases/${_version}")
            if(EXISTS "${_candidate}/controller.exe")
                file(TO_CMAKE_PATH "${_candidate}" _detected)
            endif()
        endif()
    endif()
    set(${out_var} "${_detected}" PARENT_SCOPE)
endfunction()

function(_titan_resolve_client_root out_var explicit_root)
    set(_root "${explicit_root}")
    if(NOT _root)
        set(_root "${TITAN_CLIENT_ROOT}")
    endif()
    if(NOT _root)
        set(_root "$ENV{TITAN_CLIENT_ROOT}")
    endif()
    if(NOT _root AND TARGET controller)
        # The source-built runtime need not exist during configuration.
        set(_root "$<TARGET_FILE_DIR:controller>")
    endif()
    if(NOT _root)
        _titan_detect_launcher_repository_root(_root)
    endif()
    string(REPLACE "\\" "/" _root "${_root}")
    set(${out_var} "${_root}" PARENT_SCOPE)
endfunction()

function(titan_add_dev_session)
    cmake_parse_arguments(TDS "NO_IDE_CONFIG" "PLUGIN;LOADER_MODE;TITAN_CLIENT_ROOT;WATCH_ROOT" "" ${ARGN})
    if(NOT TARGET "${TDS_PLUGIN}")
        message(FATAL_ERROR "titan_add_dev_session requires an existing PLUGIN target")
    endif()
    get_target_property(_slug ${TDS_PLUGIN} TITAN_SLUG)
    get_target_property(_spec ${TDS_PLUGIN} TITAN_STAGE_SPEC)
    get_target_property(_manifest ${TDS_PLUGIN} TITAN_DEV_MANIFEST)
    if(NOT _slug OR NOT _spec)
        message(FATAL_ERROR "Call titan_add_plugin before titan_add_dev_session")
    endif()
    if(NOT TDS_LOADER_MODE)
        set(TDS_LOADER_MODE DebuggableLoadLibrary)
    endif()
    if(NOT TDS_WATCH_ROOT)
        set(TDS_WATCH_ROOT "${CMAKE_CURRENT_SOURCE_DIR}")
    endif()
    if(NOT TDS_LOADER_MODE MATCHES "^(Restart|DebuggableLoadLibrary|MemoryBlob)$")
        message(FATAL_ERROR "LOADER_MODE must be Restart, DebuggableLoadLibrary or MemoryBlob")
    endif()
    _titan_resolve_client_root(_client_root "${TDS_TITAN_CLIENT_ROOT}")
    set_target_properties(${TDS_PLUGIN} PROPERTIES
        TITAN_LOADER_MODE "${TDS_LOADER_MODE}" TITAN_CLIENT_ROOT "${_client_root}"
        TITAN_WATCH_ROOT "${TDS_WATCH_ROOT}")
    # Repeating the call may customize defaults established by the repo helper.
    if(TARGET titan_run_${_slug})
        return()
    endif()
    set(_scripts "${CMAKE_CURRENT_FUNCTION_LIST_DIR}")
    add_custom_target(titan_stage_${_slug}
        DEPENDS ${TDS_PLUGIN}
        COMMAND powershell -NoProfile -ExecutionPolicy Bypass -File
            "${_scripts}/titan_stage_native.ps1" -SpecPath "${_spec}" -ReuseIfUnchanged
        VERBATIM USES_TERMINAL)
    foreach(_action IN ITEMS run reload)
        add_custom_target(titan_${_action}_${_slug}
            DEPENDS titan_stage_${_slug}
            COMMAND powershell -NoProfile -ExecutionPolicy Bypass -File
                "${_scripts}/titan_launch_native.ps1" -ManifestPath "${_manifest}" -Action ${_action}
            VERBATIM USES_TERMINAL)
    endforeach()
    # A source build's Run target can prepare its controller and injected client;
    # Reload/Watch only rebuild the selected plugin, keeping iteration small.
    if(TARGET controller AND _client_root STREQUAL "$<TARGET_FILE_DIR:controller>")
        add_dependencies(titan_run_${_slug} controller)
        if(TARGET client)
            add_dependencies(titan_run_${_slug} client)
        endif()
    endif()
    add_custom_target(titan_watch_${_slug}
        COMMAND powershell -NoProfile -ExecutionPolicy Bypass -File
            "${_scripts}/titan_watch_native.ps1"
            -BuildDirectory "${CMAKE_BINARY_DIR}" -Target "titan_reload_${_slug}"
            -Configuration "$<CONFIG>" -SourceRoot "$<TARGET_PROPERTY:${TDS_PLUGIN},TITAN_WATCH_ROOT>"
            -SdkRoot "${_scripts}/.."
        VERBATIM USES_TERMINAL)
    # Source files are never rewritten by configuring a project. The starter's
    # README explains IDE target selection; custom launch configs remain owned
    # by the developer.
    message(STATUS "[titan-dev:${_slug}] build/stage/run/reload/watch targets ready")
endfunction()
