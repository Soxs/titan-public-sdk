# Experience helpers

All three SDKs provide pure XP thresholds and virtual-level lookup. They require
no game connection, current skill snapshot, HTTP service or runtime capability.

| SDK | Threshold | Virtual level |
|---|---|---|
| C++ | `titan::experience::xpForLevel(level)` | `titan::experience::levelForXp(xp)` |
| Java | `net.titan.api.Experience.xpForLevel(level)` | `Experience.levelForXp(xp)` |
| JavaScript / TypeScript | `titan.experience.xpForLevel(level)` | `titan.experience.levelForXp(xp)` |

The behavior matches RuneLite 1.13.6 `Experience.getXpForLevel` and
`getLevelForXp`: level inputs are **1 through 126**, and XP inputs are
**0 through 2,147,483,647**. XP exactly at a threshold yields that level.
XP above the level-126 threshold remains level 126. The familiar **200,000,000**
skill cap is an informational constant, not the accepted XP ceiling.

Invalid inputs throw `std::invalid_argument` in C++ and
`IllegalArgumentException` in Java. JavaScript rejects nonnumbers, nonfinite
numbers and fractional numbers with `TypeError`; out-of-range integers throw
`RangeError`. Numeric strings and BigInts are not coerced. Negative zero is zero.

```cpp
#include <titan/experience.h>
const auto next = titan::experience::xpForLevel(100); // 14,391,160
const auto virtualLevel = titan::experience::levelForXp(200000000); // 126
```

```java
import net.titan.api.Experience;
int next = Experience.xpForLevel(100);
int virtualLevel = Experience.levelForXp(200_000_000);
```

```ts
const next = titan.experience.xpForLevel(100);
const virtualLevel = titan.experience.levelForXp(200_000_000);
```

C++ and JS expose `maxRealLevel=99`, `maxVirtualLevel=126` and
`maxSkillXp=200000000`. Java exposes `MAX_REAL_LEVEL`, `MAX_VIRT_LEVEL`
and `MAX_SKILL_XP`. These helpers do not calculate combat level or read live XP.

## Reproducible parity

The canonical 126-entry integer table lives in
[experience.h](../titan/experience.h). The Java table is a checked-in port;
JavaScript directly calls the C++ functions. No floating-point calculation or
table generation occurs in a plugin build or at runtime.

Run the optional offline oracle with the original local API JAR and a JDK 11+:

```sh
python scripts/check_experience_parity.py --api-jar path/to/runelite-api-1.13.6.jar --java-home path/to/jdk
```

The script verifies the original artifact's SHA-256 before loading its real
`Experience` class in an isolated class loader. It compares every C++/Java
threshold, every threshold's neighboring XP, interval midpoints, a deterministic
10,000-value sample, both input extremes, constants and invalid-input exceptions.
It compiles only the standalone Java helper/oracle into an ignored audit directory.
It does not download the original or add RuneLite to the public SDK.

Focused native targets `experience_tests` and `experience_quickjs_tests`
execute both native functions and the actual QuickJS facade, including all
threshold boundaries and strict JS argument handling. Java has `ExperienceTest`;
the TS fixture is `tests/typescript/experience_sdk_parity.ts`.
