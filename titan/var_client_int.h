/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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

/// @file titan/var_client_int.h
/// @brief RuneLite-compatible client-side integer variable ids.

#pragma once

namespace titan {

/// Client-side only, content-developer integer ids.
namespace VarClientInt {

constexpr int TOOLTIP_TIMEOUT = 1;

/// 0 = no tooltip displayed, 1 = tooltip displaying.
constexpr int TOOLTIP_VISIBLE = 2;

/// Current message layer mode. See RuneLite's InputType.
constexpr int INPUT_TYPE = 5;

constexpr int BANK_SCROLL = 51;

/// The game sets this to the same value as CAMERA_ZOOM_RESIZABLE_VIEWPORT.
constexpr int CAMERA_ZOOM_FIXED_VIEWPORT = 73;
constexpr int CAMERA_ZOOM_RESIZABLE_VIEWPORT = 74;

constexpr int MEMBERSHIP_STATUS = 103;
constexpr int INVENTORY_TAB = 171;

/// Time to block keypresses until.
constexpr int BLOCK_KEYPRESS = 187;

constexpr int WORLD_MAP_SEARCH_FOCUSED = 190;

/// Return the compatibility-catalog identifier for @p id, or nullptr when the
/// id is not named in this header.
inline const char* nameOf(int id) {
    switch (id) {
        case TOOLTIP_TIMEOUT: return "TOOLTIP_TIMEOUT";
        case TOOLTIP_VISIBLE: return "TOOLTIP_VISIBLE";
        case INPUT_TYPE: return "INPUT_TYPE";
        case BANK_SCROLL: return "BANK_SCROLL";
        case CAMERA_ZOOM_FIXED_VIEWPORT: return "CAMERA_ZOOM_FIXED_VIEWPORT";
        case CAMERA_ZOOM_RESIZABLE_VIEWPORT: return "CAMERA_ZOOM_RESIZABLE_VIEWPORT";
        case MEMBERSHIP_STATUS: return "MEMBERSHIP_STATUS";
        case INVENTORY_TAB: return "INVENTORY_TAB";
        case BLOCK_KEYPRESS: return "BLOCK_KEYPRESS";
        case WORLD_MAP_SEARCH_FOCUSED: return "WORLD_MAP_SEARCH_FOCUSED";
        default: return nullptr;
    }
}

}  // namespace VarClientInt

}  // namespace titan
