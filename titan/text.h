#pragma once

#include <stdexcept>
#include <string>
#include <string_view>

namespace titan::text {

/// Removes each <...> span through the next '>', matching RuneLite 1.13.6
/// Text.removeTags. Nested-looking spans and newlines are not special.
/// Unmatched brackets, entities and bytes outside spans remain unchanged.
/// This is game-text formatting removal, not HTML parsing or escaping.
inline std::string removeTags(std::string_view input) {
    std::string output;
    output.reserve(input.size());
    std::size_t cursor = 0;
    while (cursor < input.size()) {
        const auto open = input.find('<', cursor);
        const auto close = open == std::string_view::npos
            ? std::string_view::npos : input.find('>', open + 1);
        if (close == std::string_view::npos) {
            output.append(input.data() + cursor, input.size() - cursor);
            break;
        }
        if (open > cursor) output.append(input.data() + cursor, open - cursor);
        cursor = close + 1;
    }
    return output;
}

/// C strings must be nonnull. For embedded NUL bytes, pass a string/string_view.
inline std::string removeTags(const char* input) {
    if (!input) throw std::invalid_argument("game text must not be null");
    return removeTags(std::string_view(input));
}

} // namespace titan::text
