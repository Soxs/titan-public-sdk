#pragma once

#include "abi.h"
#include <cstddef>
#include <optional>
#include <string>
#include <utility>
#include <variant>
#include <vector>

namespace titan {
using WidgetListenerArgument = std::variant<int32_t, std::string, int64_t>;
using WidgetListenerArguments = std::vector<WidgetListenerArgument>;

namespace detail {
template<class Read>
std::optional<WidgetListenerArguments> readWidgetListener(Read&& read) {
    struct Capture {
        WidgetListenerArguments args;
        size_t textBytes = 0;
        bool valid = true;
    } capture;
    const auto append = [](void* context, uint8_t tag, int64_t number, const char* text, uint32_t length) noexcept {
        auto& value = *static_cast<Capture*>(context);
        if (!value.valid) return;
        if (value.args.size() >= 1024 || (value.args.empty() && tag != 1)) {
            value.valid = false;
            return;
        }
        try {
            if (tag == 1 && number >= INT32_MIN && number <= INT32_MAX) value.args.emplace_back(static_cast<int32_t>(number));
            else if (tag == 5) value.args.emplace_back(number);
            else if (tag == 2 && (!length || text) && length <= 65536 && length <= 262144 - value.textBytes) {
                value.args.emplace_back(std::string(text ? text : "", length));
                value.textBytes += length;
            } else value.valid = false;
        } catch (...) { value.valid = false; }
    };
    const int32_t status = read(&capture, append);
    if (!capture.valid || (status != 0 && status != 1) || (status == 0) != capture.args.empty())
        return std::nullopt;
    return std::move(capture.args);
}
}
}
