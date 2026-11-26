#pragma once

#include <cstdlib>
#include <iostream>

// Keep checks active in Release builds and report failures without Windows assertion/debugger dialogs.
#define CHECK(condition) do { \
    if (!(condition)) { \
        std::cerr << __FILE__ << ':' << __LINE__ << ": failed: " #condition << '\n'; \
        std::exit(EXIT_FAILURE); \
    } \
} while (false)
