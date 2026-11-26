cmake_minimum_required(VERSION 3.24)
project(titan_sdk_tests LANGUAGES CXX)

enable_testing()
add_subdirectory(.. titan_sdk)

add_executable(rune_element_test rune_element_test.cpp)
target_link_libraries(rune_element_test PRIVATE titan_sdk)
add_test(NAME rune_element COMMAND rune_element_test)

add_executable(rune_element_equipment_test rune_element_equipment_test.cpp)
target_link_libraries(rune_element_equipment_test PRIVATE titan_sdk)
add_test(NAME rune_element_equipment COMMAND rune_element_equipment_test)

add_executable(rune_amounts_test rune_amounts_test.cpp)
target_link_libraries(rune_amounts_test PRIVATE titan_sdk)
add_test(NAME rune_amounts COMMAND rune_amounts_test)

add_executable(rune_pouch_test rune_pouch_test.cpp)
target_link_libraries(rune_pouch_test PRIVATE titan_sdk)
add_test(NAME rune_pouch COMMAND rune_pouch_test)

add_executable(runescape_bank_test runescape_bank_test.cpp)
target_link_libraries(runescape_bank_test PRIVATE titan_sdk)
add_test(NAME runescape_bank COMMAND runescape_bank_test)

add_executable(web_walker_v3_test web_walker_v3_test.cpp)
target_link_libraries(web_walker_v3_test PRIVATE titan_sdk)
add_test(NAME web_walker_v3 COMMAND web_walker_v3_test)

add_executable(pickaxe_test pickaxe_test.cpp)
target_link_libraries(pickaxe_test PRIVATE titan_sdk)
add_test(NAME pickaxe COMMAND pickaxe_test)

add_executable(axe_test axe_test.cpp)
target_link_libraries(axe_test PRIVATE titan_sdk)
add_test(NAME axe COMMAND axe_test)

add_executable(magic_catalog_dump magic_catalog_dump.cpp)
target_link_libraries(magic_catalog_dump PRIVATE titan_sdk)
add_test(NAME magic_catalog_coverage COMMAND magic_catalog_dump)

add_executable(magic_requirements_test magic_requirements_test.cpp)
target_link_libraries(magic_requirements_test PRIVATE titan_sdk)
add_test(NAME magic_requirements COMMAND magic_requirements_test)

find_package(Python3 3.9 COMPONENTS Interpreter REQUIRED)
add_test(NAME magic_wiki_audit COMMAND ${Python3_EXECUTABLE}
    ${CMAKE_CURRENT_SOURCE_DIR}/validate_spell_wiki.py $<TARGET_FILE:magic_catalog_dump>
    --audit ${CMAKE_CURRENT_SOURCE_DIR}/fixtures/spells-wiki.json)
