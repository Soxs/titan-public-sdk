# Built-in fonts and skill icons

Titan's native, Java and JavaScript SDKs can obtain the same artwork and fonts
from the installed client. These resources work before login and with RuneLite
compatibility switched off. Each call gives the plugin its own data.

| Resource | C++ (`<titan/ui_assets.h>`) | Java (`net.titan.api`) | JavaScript / TypeScript |
|---|---|---|---|
| Standard Attack icon | `titan::skillIcons::get(titan::Skill::ATTACK)` | `SkillIcons.get(Skill.ATTACK)` | `titan.skillIcons.get(titan.Skill.ATTACK)` |
| Small Sailing icon | `titan::skillIcons::get(titan::Skill::SAILING, true)` | `SkillIcons.get(Skill.SAILING, true)` | `titan.skillIcons.get(titan.Skill.SAILING, true)` |
| Bold TrueType data | `titan::fonts::data(titan::fonts::Face::Bold)` | `Fonts.data(Fonts.Face.BOLD)` | `titan.fonts.data('bold')` |

C++ returns an optional vector of font bytes or an optional `SpriteImage`.
Java returns a byte array (empty when unavailable) or an `Optional<SpriteImage>`.
Java also offers `Fonts.font(Fonts.Face.BOLD)`, an optional 16-point AWT
`Font` that a plugin can pass to `Graphics2D.setFont` or a Swing component.
JavaScript returns `null` when it cannot supply the requested resource.
An older JavaScript runtime may predate the two namespaces. Plugins supporting
those clients can feature-detect with `titan.fonts?.data` and `titan.skillIcons?.get`.

### Drawing icons and using fonts

`titan.skillIcons.get(skill, small = false)` returns a fresh owned `SpriteImage`
for Titan skill IDs 0 through 23, including Sailing. Use `titan.Skill.ATTACK`,
`titan.Skill.SAILING`, or another Titan skill constant; RuneLite enum ordinals are
not the ID contract. Invalid arguments or unavailable data return `null`.
The normal and small variants preserve their bundled canvas dimensions, offsets
and straight ARGB pixels. Draw them with `titan.overlay.sprite` or
`titan.sprites.draw` from an overlay callback. Fetch once and retain the image
instead of copying it every rendered frame.

`titan.fonts.data('regular' | 'bold' | 'small')` returns a fresh `Uint8Array`
containing the exact bundled TTF. Editing either font bytes or image pixels does
not change the host resource or any other returned value. These reads require
no active game/cache snapshot and do not install fonts into the operating system.

HTML panels in all three SDKs can declare the bytes as a `font/ttf` bundle resource and use a
relative `@font-face` URL. The existing per-resource and total bundle limits
still apply; no remote font request is needed. See the complete
[TypeScript example](../js/ui-assets/example.ts).

Font bytes alone do not add a selectable font to `titan.overlay.screenText`.
That native text API still uses its existing renderer font. Native overlay font
selection/metrics would need a separate rendering contract; HTML/CSS font use
does not establish that capability.

The Windows renderer test loads the three unchanged fonts through the HTML
bundle and checks their different glyph widths. The public SDK export contains
the plugin APIs and example. The installed client's third-party notices identify
the source of the artwork and the licenses recorded in each font.
