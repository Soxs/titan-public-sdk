# Keep acquisition in the template: the public SDK also contains a large Maven
# repository, so a full clone/archive is unsuitable for a native starter.
include_guard(GLOBAL)

function(_titan_validate_sdk root)
    foreach(_required IN ITEMS
            CMakeLists.txt titan/plugin.h titan/detail/native_abi.h
            cmake/titan_plugin.cmake cmake/titan_dev_session.cmake
            cmake/titan_stage_native.ps1 cmake/titan_launch_native.ps1
            cmake/titan_watch_native.ps1 cmake/titan_cleanup_native.cs)
        if(NOT EXISTS "${root}/${_required}")
            message(FATAL_ERROR
                "Titan SDK at '${root}' is missing '${_required}'. "
                "Choose a complete native titan-public-sdk checkout with "
                "-DTITAN_PLUGIN_SDK_ROOT=<path>, or clear that override to acquire the pinned SDK.")
        endif()
    endforeach()
endfunction()

function(_titan_sdk_git root output)
    execute_process(
        COMMAND "${CMAKE_COMMAND}" -E env GIT_TERMINAL_PROMPT=0 GCM_INTERACTIVE=Never
            "${GIT_EXECUTABLE}" -C "${root}" ${ARGN}
        RESULT_VARIABLE _result OUTPUT_VARIABLE _stdout ERROR_VARIABLE _stderr
        TIMEOUT 180)
    if(NOT _result EQUAL 0)
        message(FATAL_ERROR
            "Titan SDK Git operation failed (${_result}): ${_stderr}\n"
            "Reconfigure to retry, or select an existing native SDK with "
            "-DTITAN_PLUGIN_SDK_ROOT=<path>.")
    endif()
    string(STRIP "${_stdout}" _stdout)
    set(${output} "${_stdout}" PARENT_SCOPE)
endfunction()

function(titan_acquire_sdk output)
    # A nonempty cache/normal variable wins over the environment. Validate any
    # explicit choice before considering bundled inputs or doing network work.
    set(_selected "${TITAN_PLUGIN_SDK_ROOT}")
    if(NOT _selected AND NOT "$ENV{TITAN_PLUGIN_SDK_ROOT}" STREQUAL "")
        set(_selected "$ENV{TITAN_PLUGIN_SDK_ROOT}")
    endif()
    if(NOT _selected AND
       EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/../../CMakeLists.txt" AND
       EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/../../titan/plugin.h")
        set(_selected "${CMAKE_CURRENT_SOURCE_DIR}/../..")
    endif()
    if(_selected)
        get_filename_component(_selected "${_selected}" ABSOLUTE
            BASE_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
        _titan_validate_sdk("${_selected}")
        message(STATUS "[titan-sdk] Using ${_selected}")
        set(${output} "${_selected}" PARENT_SCOPE)
        return()
    endif()

    # Pin source content independently of the stable Native ABI major. Updating
    # this full commit ID is a deliberate template dependency update.
    set(_titan_sdk_revision "b2d551c6348fd420aff89dedc9e7421256af93b8")
    set(_repository "https://github.com/Soxs/titan-public-sdk.git")
    if(NOT _titan_sdk_revision MATCHES "^[0-9a-f]+$" )
        message(FATAL_ERROR "The pinned Titan SDK revision must be a full hexadecimal commit ID.")
    endif()
    string(LENGTH "${_titan_sdk_revision}" _revision_length)
    if(NOT _revision_length EQUAL 40)
        message(FATAL_ERROR "The pinned Titan SDK revision must contain exactly 40 hexadecimal digits.")
    endif()
    find_package(Git 2.27 QUIET)
    if(NOT GIT_FOUND)
        message(FATAL_ERROR
            "Git 2.27+ is required to acquire the pinned native SDK. Install Git, "
            "or set -DTITAN_PLUGIN_SDK_ROOT=<existing native SDK checkout>.")
    endif()

    set(_cache "${CMAKE_BINARY_DIR}/_deps/titan-sdk-${_titan_sdk_revision}")
    set(_owner "${_cache}/.titan-sdk-owner")
    set(_ready "${_cache}/.titan-sdk-ready")
    file(MAKE_DIRECTORY "${CMAKE_BINARY_DIR}/_deps")
    file(LOCK "${_cache}.lock" GUARD FUNCTION TIMEOUT 120 RESULT_VARIABLE _lock_result)
    if(NOT _lock_result EQUAL 0)
        message(FATAL_ERROR "Another configure is acquiring the Titan SDK: ${_lock_result}. Retry when it finishes.")
    endif()
    if(EXISTS "${_cache}")
        if(NOT EXISTS "${_owner}")
            message(FATAL_ERROR
                "Refusing to reuse unrecognized SDK cache '${_cache}'. "
                "Choose another build directory or select an SDK explicitly.")
        endif()
        file(READ "${_owner}" _owner_revision)
        string(STRIP "${_owner_revision}" _owner_revision)
        if(NOT _owner_revision STREQUAL _titan_sdk_revision)
            message(FATAL_ERROR "SDK cache ownership does not match the pinned revision: ${_cache}")
        endif()
    else()
        file(MAKE_DIRECTORY "${_cache}")
        file(WRITE "${_owner}" "${_titan_sdk_revision}\n")
    endif()

    if(EXISTS "${_ready}")
        file(READ "${_ready}" _ready_revision)
        string(STRIP "${_ready_revision}" _ready_revision)
        _titan_sdk_git("${_cache}" _head rev-parse --verify HEAD)
        if(NOT _ready_revision STREQUAL _titan_sdk_revision OR
           NOT _head STREQUAL _titan_sdk_revision)
            message(FATAL_ERROR "Cached Titan SDK revision changed: ${_cache}. Use a fresh build directory.")
        endif()
        # Only local index/worktree metadata is read. A completed checkout never
        # contacts origin during a normal reconfigure, even when offline.
        _titan_sdk_git("${_cache}" _missing ls-files --deleted --
            CMakeLists.txt plugin_sdk.h cmake titan THIRD_PARTY_NOTICES.md)
        if(NOT _missing STREQUAL "")
            message(FATAL_ERROR "Cached Titan SDK files are missing: ${_missing}. Use a fresh build directory.")
        endif()
        _titan_validate_sdk("${_cache}")
        message(STATUS "[titan-sdk] Using cached native SDK ${_titan_sdk_revision}")
        set(${output} "${_cache}" PARENT_SCOPE)
        return()
    endif()

    # An interrupted acquisition retains its owned directory and can retry.
    # Sparse patterns are anchored and non-cone: no other root files or Maven
    # blobs are checked out by accident. Blob filtering applies to the fetch;
    # checkout requests only the selected native files from the promisor remote.
    message(STATUS "[titan-sdk] Acquiring C++ files from pinned SDK ${_titan_sdk_revision}")
    _titan_sdk_git("${_cache}" _unused init --quiet)
    _titan_sdk_git("${_cache}" _unused config remote.origin.url "${_repository}")
    _titan_sdk_git("${_cache}" _unused config remote.origin.promisor true)
    _titan_sdk_git("${_cache}" _unused config remote.origin.partialclonefilter blob:none)
    _titan_sdk_git("${_cache}" _unused -c protocol.version=2 fetch
        --no-tags --depth=1 --filter=blob:none origin "${_titan_sdk_revision}")
    _titan_sdk_git("${_cache}" _unused sparse-checkout set --no-cone
        /CMakeLists.txt /plugin_sdk.h /cmake/ /titan/ /THIRD_PARTY_NOTICES.md)
    _titan_sdk_git("${_cache}" _unused checkout --detach "${_titan_sdk_revision}")
    _titan_validate_sdk("${_cache}")
    _titan_sdk_git("${_cache}" _head rev-parse --verify HEAD)
    if(NOT _head STREQUAL _titan_sdk_revision)
        message(FATAL_ERROR "Acquired SDK does not match its pinned revision.")
    endif()
    file(WRITE "${_ready}" "${_titan_sdk_revision}\n")
    set(${output} "${_cache}" PARENT_SCOPE)
endfunction()
