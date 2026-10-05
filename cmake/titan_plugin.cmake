# Shared native plugin build and staging helpers. Repository builds reuse the
# staging functions while retaining their existing build/plugins output layout.
include_guard(GLOBAL)
include("${CMAKE_CURRENT_LIST_DIR}/titan_dev_session.cmake")

function(_titan_json_escape out value)
    string(REPLACE "\\" "/" value "${value}")
    string(REPLACE "\"" "\\\"" value "${value}")
    string(REPLACE "\n" "\\n" value "${value}")
    set(${out} "${value}" PARENT_SCOPE)
endfunction()

function(_titan_configure_native_staging target)
    get_target_property(_slug ${target} TITAN_SLUG)
    get_target_property(_slugs ${target} TITAN_SLUGS)
    if(NOT _slug MATCHES "^[A-Za-z0-9_-]+$")
        message(FATAL_ERROR "Native plugin slug must contain only letters, digits, '_' or '-'")
    endif()
    string(REPLACE "," ";" _slugs "${_slugs}")
    set(_slugs_json "")
    foreach(_item IN LISTS _slugs)
        if(NOT _item MATCHES "^[A-Za-z0-9_-]+$")
            message(FATAL_ERROR "Invalid native plugin slug '${_item}'")
        endif()
        if(_slugs_json)
            string(APPEND _slugs_json ", ")
        endif()
        string(APPEND _slugs_json "\"${_item}\"")
    endforeach()
    set(_root "${CMAKE_BINARY_DIR}/.titan/dev/${_slug}")
    set(_config "$<IF:$<BOOL:$<CONFIG>>,$<CONFIG>,Default>")
    set(_spec "${_root}/${_config}/stage-spec.json")
    set(_manifest "${_root}/${_config}/session.json")
    _titan_resolve_client_root(_client_root "")
    set_target_properties(${target} PROPERTIES
        TITAN_SESSION_ROOT "${_root}"
        TITAN_STAGE_SPEC "${_spec}"
        TITAN_DEV_MANIFEST "${_manifest}"
        TITAN_CLIENT_ROOT "${_client_root}"
        TITAN_LOADER_MODE "DebuggableLoadLibrary"
        TITAN_RUNTIME_FILES_JSON "")
    file(STRINGS "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/../titan/detail/abi.h" _version_line
        REGEX "constexpr[ \t]+uint32_t[ \t]+kSdkVersion[ \t]*=[ \t]*[0-9]+")
    string(REGEX MATCH "= *([0-9]+)" _match "${_version_line}")
    set(_sdk_version "${CMAKE_MATCH_1}")
    if(NOT _sdk_version)
        set(_sdk_version 0)
    endif()
    set(_pdb "")
    if(MSVC)
        set(_pdb "$<TARGET_PDB_FILE:${target}>")
    endif()
    _titan_json_escape(_source "${CMAKE_SOURCE_DIR}")
    _titan_json_escape(_root_json "${_root}")
    file(GENERATE OUTPUT "${_spec}" CONTENT "{
  \"source_dll\": \"$<TARGET_FILE:${target}>\",
  \"source_pdb\": \"${_pdb}\",
  \"runtime_dlls\": [\"$<JOIN:$<TARGET_RUNTIME_DLLS:${target}>,\",\">\"],
  \"slug\": \"${_slug}\",
  \"slugs\": [${_slugs_json}],
  \"session_root\": \"${_root_json}\",
  \"source_root\": \"${_source}\",
  \"build_config\": \"${_config}\",
  \"sdk_version\": ${_sdk_version},
  \"native_abi_version\": 1,
  \"titan_install_root\": \"$<TARGET_GENEX_EVAL:${target},$<TARGET_PROPERTY:${target},TITAN_CLIENT_ROOT>>\",
  \"loader_mode\": \"$<TARGET_PROPERTY:${target},TITAN_LOADER_MODE>\",
  \"runtime_files\": [$<TARGET_PROPERTY:${target},TITAN_RUNTIME_FILES_JSON>]
}")
    add_custom_command(TARGET ${target} POST_BUILD
        COMMAND powershell -NoProfile -ExecutionPolicy Bypass -File
            "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/titan_stage_native.ps1" -SpecPath "${_spec}" -ReuseIfUnchanged
        COMMENT "[titan-dev:${_slug}] stage native DLL and symbols"
        VERBATIM)
endfunction()

# Declare data files, dependent DLLs, or a directory to snapshot with the plugin.
# DESTINATION is relative to the generation directory; directories preserve
# their contents beneath it. Declare generated files' build dependencies too.
function(titan_plugin_runtime_file)
    cmake_parse_arguments(TRF "" "TARGET;SOURCE;DESTINATION" "" ${ARGN})
    if(NOT TARGET "${TRF_TARGET}" OR NOT TRF_SOURCE OR NOT TRF_DESTINATION)
        message(FATAL_ERROR "titan_plugin_runtime_file requires TARGET, SOURCE and DESTINATION")
    endif()
    get_filename_component(_source "${TRF_SOURCE}" ABSOLUTE BASE_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
    _titan_json_escape(_source "${_source}")
    _titan_json_escape(_dest "${TRF_DESTINATION}")
    if(IS_ABSOLUTE "${_dest}" OR _dest MATCHES "(^|/)\\.\\.(/|$)")
        message(FATAL_ERROR "Native runtime destination must stay within its generation directory")
    endif()
    get_target_property(_entries ${TRF_TARGET} TITAN_RUNTIME_FILES_JSON)
    if(_entries)
        string(APPEND _entries ",")
    else()
        set(_entries "")
    endif()
    string(APPEND _entries "{\"source\":\"${_source}\",\"destination\":\"${_dest}\"}")
    set_property(TARGET ${TRF_TARGET} PROPERTY TITAN_RUNTIME_FILES_JSON "${_entries}")
endfunction()

function(titan_add_plugin)
    cmake_parse_arguments(TP "" "TARGET;SLUG" "SOURCES;SLUGS" ${ARGN})
    if(NOT TP_TARGET OR NOT TP_SOURCES)
        message(FATAL_ERROR "titan_add_plugin requires TARGET and SOURCES")
    endif()
    if(TP_SLUG AND TP_SLUGS)
        message(FATAL_ERROR "Use SLUG or SLUGS, not both")
    endif()
    if(TP_SLUGS)
        list(GET TP_SLUGS 0 TP_SLUG)
    elseif(NOT TP_SLUG)
        set(TP_SLUG "${TP_TARGET}")
    endif()
    if(NOT TP_SLUGS)
        set(TP_SLUGS "${TP_SLUG}")
    endif()
    string(REPLACE ";" "," _slugs "${TP_SLUGS}")
    add_library(${TP_TARGET} SHARED ${TP_SOURCES})
    target_link_libraries(${TP_TARGET} PRIVATE titan_sdk)
    set_target_properties(${TP_TARGET} PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/plugins"
        TITAN_SLUG "${TP_SLUG}" TITAN_SLUGS "${_slugs}"
        MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    _titan_configure_native_staging(${TP_TARGET})
endfunction()
