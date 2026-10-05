/*
 * Copyright (c) 2018, Kamiel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/// @file titan/var_client_str.h
/// @brief RuneLite-compatible client-side string variable ids.

#pragma once

namespace titan {

/// Client-side only, content-developer string ids.
namespace VarClientStr {

constexpr int CHATBOX_TYPED_TEXT = 335;
constexpr int INPUT_TEXT = 359;
constexpr int PRIVATE_MESSAGE_TARGET = 360;
constexpr int RECENT_FRIENDS_CHAT = 362;
constexpr int NOTIFICATION_TOP_TEXT = 387;
constexpr int NOTIFICATION_BOTTOM_TEXT = 388;

/// Return the compatibility-catalog identifier for @p id, or nullptr when the
/// id is not named in this header.
inline const char* nameOf(int id) {
    switch (id) {
        case CHATBOX_TYPED_TEXT: return "CHATBOX_TYPED_TEXT";
        case INPUT_TEXT: return "INPUT_TEXT";
        case PRIVATE_MESSAGE_TARGET: return "PRIVATE_MESSAGE_TARGET";
        case RECENT_FRIENDS_CHAT: return "RECENT_FRIENDS_CHAT";
        case NOTIFICATION_TOP_TEXT: return "NOTIFICATION_TOP_TEXT";
        case NOTIFICATION_BOTTOM_TEXT: return "NOTIFICATION_BOTTOM_TEXT";
        default: return nullptr;
    }
}

}  // namespace VarClientStr

}  // namespace titan
