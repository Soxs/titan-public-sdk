# Game-text tag removal

Use the same pure operation from each SDK:

| SDK | Operation |
|---|---|
| C++ | `titan::text::removeTags(text)` from `<titan/text.h>` |
| Java | `net.titan.api.Text.removeTags(text)` |
| JavaScript / TypeScript | `titan.text.removeTags(text)` |

It matches pinned RuneLite 1.13.6 `net.runelite.client.util.Text.removeTags`,
whose pattern is `<([^>]*)>`. Each opening `<` through the next `>` is
removed, including empty spans, newlines and nested-looking text. Unmatched
brackets and text outside those spans remain unchanged. Entities are not decoded.

| Input | Output |
|---|---|
| `<col=ff0000>Red</col>` | `Red` |
| `<img=12>Player` | `Player` |
| `before<>after` | `beforeafter` |
| `<outer <inner>>` | `>` |
| `text<unfinished` | `text<unfinished` |
| `&lt;tag&gt;` | `&lt;tag&gt;` |

This removes game-text formatting; it is not HTML parsing, sanitizing or escaping.

```cpp
#include <titan/text.h>
std::string plain = titan::text::removeTags("<col=ff0000>Red</col>");
```

```java
String plain = net.titan.api.Text.removeTags("<col=ff0000>Red</col>");
```

```ts
const removeTags = titan.text.removeTags;
const plain = removeTags('<col=ff0000>Red</col>'); // Works detached from titan.text.
```

Empty input returns empty text. Java null throws `NullPointerException`;
JS null and all other nonstrings throw `TypeError` without coercion.
C++ accepts a `string_view` or C string, returns an owned `std::string`, and
throws `std::invalid_argument` for a null C string. Use a length-bearing
`string`/`string_view` for embedded NULs. C++ preserves UTF-8 bytes; Java/JS
preserve their UTF-16 strings, including unpaired surrogates outside tags.

## Original-class verification

All three public entry points use the same 33-case golden corpus:
`shared/java/src/test/resources/net/titan/api/game_text_golden.tsv`.
It includes malformed/nested spans, newlines, Unicode, embedded NULs, entities
and empty text. The offline oracle verifies those expected values against the
actual original client class in an isolated loader, plus Java null and unpaired
UTF-16 behavior. It checks the original client JAR's SHA-256 before loading it.

```sh
python scripts/check_game_text_parity.py --client-jar path/to/client-1.13.6.jar --dependencies path/to/pinned-runtime-jars --java-home path/to/jdk
```

The dependency directory supplies the existing original Guava 23.2-jre,
Commons Text 1.2 and Commons Lang 3.7 JARs required to initialize that class.
Nothing is downloaded, and none of those libraries is added to the public SDK.
The standalone oracle compiles into an ignored audit directory, outside Gradle.

Focused native targets are `game_text_tests` and `game_text_quickjs_tests`.
Java's `TextTest` and `tests/typescript/game_text_sdk_parity.ts` cover the
public Java/type contracts. The QuickJS test extracts the actual production
facade and executes it with no host functions installed.
