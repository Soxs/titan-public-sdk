/// @file titan/utils/dialogue.h
/// @brief Plugin-side RuneLite-style dialogue / continue / make /
///        quest-scroll helpers.
///
/// Header-only inline wrappers around `titan::state::widgets()` and
/// `titan::keyboard` that let plugins dismiss continue prompts, select a
/// dialog option by text, click the "Make" button, and close the
/// quest-scroll. Mirrors the `Dialogue::*` namespace in
/// `client/actions/dialogue.h`.
///
/// **What is here:**
///   - `continueDialogue()` -- presses Space on the active continue prompt.
///   - `continueMake()`, `closeQuestCompletion()` -- one-shot CC_OP clicks
///     against fixed packed widget ids.
///   - `inDialogue()`, `isQuestCompletionOpen()`,
///     `getContinueWidgetPackedId()` -- visibility queries built on
///     `titan::state::widgets().find()`.
///   - `hasOption(...)`, `selectOption(...)`, `handleDialogue(...)`
///     (SDK 38) -- dialog-option selection by case-insensitive text
///     match, backed by `titan::state::widgets().children(pack(219, 1))`;
///     `selectOption` presses the matched option's digit key.
///
/// **What is still NOT here:**
///   - `findWidgetWithText(...)` -- a recursive walk of every widget
///     root. The current SDK still has no "enumerate all widget roots"
///     primitive; callers that need it should scope their search to a
///     known parent and iterate `titan::state::widgets().children(parent)`
///     manually.
///
/// Like RuneLite's `handleDialogue`, continue prompts and dialog options are
/// answered from the keyboard: Space continues, and digit key N picks option
/// N, which is dynamic child N of (219, 1). Child 0 is the title/header and
/// is never selected. Keys go through the host's game-internal keyboard
/// injection (no OS-level input APIs); the synthetic menu actions they
/// replaced are kept, commented out, beside each call.
///
/// The Make button and quest-scroll close still dispatch through
/// `state::widgets().interact(opcode, identifier, param0, param1)` with the
/// RuneLite shape `{identifier=1, opcode=CC_OP(57), p0=-1, p1=<packed
/// widget id>}` -> `interact(CcOp, 1, -1, packedId)`.

#pragma once

#include "../client.h"
#include "../gamevals.h"
#include "../keyboard.h"
#include "../menu_action.h"

#include <array>
#include <cctype>
#include <cstdint>
#include <initializer_list>
#include <optional>
#include <string_view>
#include <vector>

namespace titan {
namespace utils {
namespace Dialogue {

namespace detail {

struct ContinueCandidate {
    int32_t packedId = 0;
    bool requireContinueText = false;
    /// Dynamic-child slot beneath `packedId` that holds the prompt, or -1
    /// when the component itself is the prompt.
    int32_t dynamicSlot = -1;
};

inline constexpr std::array<ContinueCandidate, 10> kContinueCandidates = {{
    {gamevals::InterfaceID::LevelupDisplay::CONTINUE_, false},
    {gamevals::InterfaceID::Messagebox::CONTINUE_,     false},
    {gamevals::InterfaceID::Messagebox::CONTENT,        false},
    {gamevals::InterfaceID::ChatLeft::CONTINUE_,        false},
    {gamevals::InterfaceID::ChatRight::CONTINUE_,       false},
    {gamevals::InterfaceID::ObjectboxDouble::PAUSEBUTTON, false},
    {gamevals::InterfaceID::Messagebox::SAFEZONE,       false},
    {gamevals::InterfaceID::Chatbox::CLOSE_ICON,        true},
    {gamevals::InterfaceID::Chatbox::MES_TEXT,          true},
    // Objectbox has no fixed continue component: its prompt is dynamic child 2.
    {gamevals::InterfaceID::Objectbox::UNIVERSE,        true, 2},
}};

/// Case-insensitive equality, trimming surrounding whitespace on the widget
/// side so " Yes " still matches "Yes".
inline bool iequals(std::string_view text, std::string_view needle) {
    const auto isSpace = [](char value) {
        return std::isspace(static_cast<unsigned char>(value)) != 0;
    };
    while (!text.empty() && isSpace(text.front())) text.remove_prefix(1);
    while (!text.empty() && isSpace(text.back())) text.remove_suffix(1);
    if (text.size() != needle.size()) return false;
    for (std::size_t i = 0; i < text.size(); ++i) {
        if (std::tolower(static_cast<unsigned char>(text[i]))
            != std::tolower(static_cast<unsigned char>(needle[i]))) {
            return false;
        }
    }
    return true;
}

/// Case-insensitive substring search.
inline bool icontains(std::string_view haystack, std::string_view needle) {
    if (needle.empty()) return true;
    if (needle.size() > haystack.size()) return false;
    auto lower = [](unsigned char c) {
        return static_cast<char>(std::tolower(c));
    };
    for (std::size_t i = 0; i + needle.size() <= haystack.size(); ++i) {
        std::size_t j = 0;
        for (; j < needle.size(); ++j) {
            if (lower(static_cast<unsigned char>(haystack[i + j])) !=
                lower(static_cast<unsigned char>(needle[j]))) break;
        }
        if (j == needle.size()) return true;
    }
    return false;
}

inline bool isVisibleWidget(const Widget& widget) {
    return widget.visible();
}

/// @return The widget handle iff present and visible.
inline std::optional<Widget> resolveVisible(uint32_t packedId) {
    auto widget = titan::state::widgets().find(packedId);
    if (!widget) return std::nullopt;
    if (!isVisibleWidget(*widget)) return std::nullopt;
    return widget;
}

/// Build and dispatch a "click whole widget" menu action with
/// `param0 = -1`. Used for one-shot button widgets where the click
/// targets the parent (no dynamic-child slot).
///
/// The `(opcode, identifier)` pair varies by widget family. Captured
/// `DoAction` traces show:
///
///   - **CC_OP buttons** (Make, quest-scroll close, auto-retaliate, ...)
///     use `{opcode=CC_OP(57), identifier=1, p0=-1, p1=<packed widget id>}`.
///     Default here.
///   - **Continue prompts** (NPC dialog continue, level-up, minigame
///     dialog, tutorial-island prompts, ...) use
///     `{opcode=WIDGET_CONTINUE(30), identifier=0, p0=-1, p1=<packed widget id>}`.
///     Pass `opcode=Id::WidgetContinue, identifier=0` when targeting one
///     of those.
inline bool clickWholeWidget(
        uint32_t widgetPackedId,
        uint32_t opcode = static_cast<uint32_t>(MenuAction::Id::CcOp),
        int32_t identifier = 1) {
    return titan::state::widgets().interact(
        opcode,
        /*identifier=*/identifier,
        /*param0=*/-1,
        /*param1 (widget packed id)=*/static_cast<int32_t>(widgetPackedId));
}

}  // namespace detail

/// Click the "Make" button (270, 14) when the make-X interface is open.
/// @return true when the click was queued, false when the widget is missing
///         or hidden.
inline bool continueMake() {
    if (!detail::resolveVisible(gamevals::InterfaceID::Skillmulti::BOTTOM)) return false;
    return detail::clickWholeWidget(gamevals::InterfaceID::Skillmulti::BOTTOM);
}

/// @return The packed widget id of the active "click to continue" prompt,
///         or 0 when no continue widget is up. Walks the same candidate
///         list as the client-internal helper, including the text-gated
///         tutorial-island prompts (162, 42) / (162, 43) and Objectbox's
///         prompt. That one is dynamic child 2 of `Objectbox::UNIVERSE`,
///         which is what this returns for it; it is not clickable as a
///         whole widget, so advance it with `continueDialogue()`.
inline uint32_t getContinueWidgetPackedId() {
    for (const auto& candidate : detail::kContinueCandidates) {
        auto widget = titan::state::widgets().find(candidate.packedId);
        if (!widget || !detail::isVisibleWidget(*widget)) continue;

        if (candidate.dynamicSlot >= 0) {
            // children() reports each slot's visibility along its retained
            // path: the parent's ancestor chain plus the child's own flags.
            const auto children = titan::state::widgets().children(candidate.packedId);
            const auto slot = static_cast<std::size_t>(candidate.dynamicSlot);
            if (slot >= children.size() || !detail::isVisibleWidget(children[slot])) continue;
            widget = children[slot];
        }

        if (candidate.requireContinueText) {
            if (!detail::icontains(widget->text(), "Click here to continue")) continue;
        }

        return candidate.packedId;
    }
    return 0;
}

/// Press Space to advance the active continue prompt (level-up, NPC dialog
/// continue, minigame dialog, item box, tutorial-island prompt, ...).
/// @return true when a continue prompt was found and the key press was
///         queued.
inline bool continueDialogue() {
    uint32_t packedId = getContinueWidgetPackedId();
    if (!packedId) return false;
    // Mouse dispatch, disabled in favour of the keyboard. Before re-enabling:
    // a dynamic-slot candidate (Objectbox) must be clicked as
    // `p0=<slot> p1=<parent id>`, not as a whole widget.
    //
    // // Continue prompts use the WIDGET_CONTINUE / identifier=0 shape, not
    // // CC_OP. Verified against captured GAME-side DoAction records:
    // // manually clicking (231,5) emits `opcode=30 identifier=0 p0=-1 p1=<wid>`.
    // return detail::clickWholeWidget(
    //     packedId,
    //     static_cast<uint32_t>(MenuAction::Id::WidgetContinue),
    //     /*identifier=*/0);

    // Space advances every continue prompt, matching the host helper.
    return titan::keyboard::sendKey(TitanPluginSdk::KeyboardKey::SPACE);
}

/// @return true when a continue prompt or a multi-option dialog is visible.
inline bool inDialogue() {
    if (getContinueWidgetPackedId()) return true;
    return detail::resolveVisible(gamevals::InterfaceID::Chatmenu::OPTIONS).has_value();
}

/// @return true when the quest-completion scroll is open.
inline bool isQuestCompletionOpen() {
    if (detail::resolveVisible(gamevals::InterfaceID::Questscroll::CONTENT)) return true;
    return detail::resolveVisible(gamevals::InterfaceID::Questscroll::CLOSE_BUTTON).has_value();
}

/// Click the close button on the quest-completion scroll.
/// @return true when the click was queued.
inline bool closeQuestCompletion() {
    if (!isQuestCompletionOpen()) return false;
    if (!detail::resolveVisible(gamevals::InterfaceID::Questscroll::CLOSE_BUTTON)) return false;
    return detail::clickWholeWidget(gamevals::InterfaceID::Questscroll::CLOSE_BUTTON);
}

/// @return true when the multi-option dialog widget (219, 1) is visible
///         and at least one option text contains any of `needles` (case-
///         insensitive substring match). The title/header at slot 0 is not
///         an option. Returns false when the dialog is hidden, has no
///         dynamic children, or the host is pre-SDK-38 (child enumeration
///         unavailable).
inline bool hasOption(std::initializer_list<std::string_view> needles) {
    if (!detail::resolveVisible(gamevals::InterfaceID::Chatmenu::OPTIONS)) return false;

    const auto children = titan::state::widgets().children(gamevals::InterfaceID::Chatmenu::OPTIONS);
    if (children.empty()) return false;

    // Slot 0 is the dialog title/header. It is NOT needle-proof: a header
    // like "Travel to where?" contains "Travel", so options start at slot 1.
    for (auto needle : needles) {
        for (std::size_t i = 1; i < children.size(); ++i) {
            if (!detail::isVisibleWidget(children[i])) continue;
            if (detail::icontains(children[i].text(), needle)) return true;
        }
    }
    return false;
}

/// Pick the first dialog option whose text contains any of `needles`
/// (case-insensitive substring) by pressing its digit key: option N is
/// dynamic child N of widget (219, 1), matching "press 1 = first option"
/// in the RuneLite reference. Slot 0, the title/header, is never picked,
/// and within one needle an exact match beats an earlier substring match.
/// @return true when a matching option was found and the key press was
///         queued.
inline bool selectOption(std::initializer_list<std::string_view> needles) {
    if (!detail::resolveVisible(gamevals::InterfaceID::Chatmenu::OPTIONS)) return false;

    const auto children = titan::state::widgets().children(gamevals::InterfaceID::Chatmenu::OPTIONS);
    if (children.empty()) return false;

    // Options start at slot 1 (see hasOption). Within the options, an exact
    // match beats a substring: needles like "Yes" would otherwise take
    // "Yes, but first..." simply because it is listed earlier.
    const auto press = [&](std::size_t slot) {
        // Mouse dispatch, disabled in favour of the keyboard.
        //
        // return children[slot].interact(
        //     static_cast<uint32_t>(MenuAction::Id::WidgetContinue),
        //     /*identifier=*/0);

        // Option N is dynamic child N and answers to the digit key N.
        if (slot > 9) return false;
        const char key[2] = {static_cast<char>('0' + slot), '\0'};
        return titan::keyboard::sendString(key);
    };

    for (auto needle : needles) {
        std::optional<std::size_t> substringMatch;
        for (std::size_t i = 1; i < children.size(); ++i) {
            if (!detail::isVisibleWidget(children[i])) continue;
            const auto& text = children[i].text();
            if (detail::iequals(text, needle)) return press(i);
            if (!substringMatch && detail::icontains(text, needle)) {
                substringMatch = i;
            }
        }
        if (substringMatch) return press(*substringMatch);
    }
    return false;
}

/// Convenience: pick a matching dialog option when one is visible, otherwise
/// advance a continue prompt. Mirrors RuneLite's `handleDialogue(String...)`
/// -- press the digit for a matching multi-option dialog, or SPACE on
/// continue prompts.
/// @return true when either a continue prompt was advanced or an option
///         was selected.
inline bool handleDialogue(std::initializer_list<std::string_view> needles) {
    if (selectOption(needles)) return true;
    return continueDialogue();
}

}  // namespace Dialogue
}  // namespace utils
}  // namespace titan
