# Titan Plugin SDK -- public symbol inventory (v146)

This file is the authoritative contract of what plugin authors can rely on.
The native binary contract is Native ABI v1. SDK release numbers describe
source features; required capability contracts determine whether a DLL loads.
Anything not listed is internal and may change without notice.

For threading rules and the per-callback thread each event runs on, see the
[Threading guarantees](#threading-guarantees) section below.

For the version-over-version changelog, see the `--- Changelog ---` block at
the top of [shared/titan/detail/abi.h](detail/abi.h), or the mirrored copy
in `CHANGELOG.md` in the SDK distribution package.

---

## HTML side panels and overlays (SDK v145)

HTML surfaces are separate from existing ImGui panels. Their resources are
embedded in the C++ DLL, retained in the declaring Java plugin's JAR, or supplied
as a JS/TS in-source asset map. A released plugin needs no loose UI files.

| Operation | C++ | Java | JS / TypeScript |
| --- | --- | --- | --- |
| Side panel | `Plugin::htmlPanel(id, title, HtmlPanelBundle)` | repeatable `@HtmlSidePanel` | `panels: [{kind: "html", id, title, bundle}]` |
| Overlay | `Plugin::htmlOverlayPanel(id, bundle, HtmlOverlayOptions)` | repeatable `@HtmlOverlayPanel` | `this.htmlOverlayPanel({id, bundle})` or `htmlOverlays: [{kind: "html", id, bundle}]` |
| Canonical state | `panel.setState(json)` | `HtmlPanels.of(this).setState(id, json)` | `titan.htmlPanels.setState(this, id, value)` |
| Transient message | `panel.postMessage(type, json, correlationId)` | `HtmlPanels.of(this).postMessage(id, type, json, correlationId)` | `titan.htmlPanels.postMessage(this, id, type, value, correlationId)` |
| Incoming message | `panel.onMessage(callback)` | `Plugin.onHtmlPanelMessage(id, message)` / `onHtmlOverlayMessage(id, message)` | HTML definition or factory handle `onMessage(message)` |
| Overlay updates | `overlay.setVisible(bool)`, `setSize(width, height)` | `HtmlOverlays` facade | Factory handle `setVisible(bool)`, `setSize(width, height)` or `titan.htmlOverlays` facade |

The JS/TS class factory `Plugin.htmlOverlayPanel(options: HtmlOverlayPanelOptions)`
returns an owner-bound `HtmlOverlayPanelInstance` with `id`, `setState(value)`,
`postMessage(type, payload, correlationId?)`, `setVisible(bool)`,
`setSize(width, height)`, and `onMessage(callback)`. Its factory options may omit
`kind`; an explicit value must be `"html"`. Constructor-time state, size, and
visibility initialize the registered overlay. Configure definitions and callbacks
before `titan.register`; retired handles cannot act on a replacement plugin.

`<titan/html_panels.h>` is dependency-free apart from the C++ standard library.
`HtmlPanelBundle::text(path, mime, contents)` and `binary(path, mime, bytes, size)`
copy owned resources. Registration copies and validates the whole bundle.
Metadata and callbacks are configured in the plugin constructor and frozen
before publication. State and messages may subsequently change; immutable
resource reads and owned update snapshots are safe while the host pins the DLL.

HTML message callbacks run on the game thread through MainLoop under the plugin
lifecycle gate. Page JavaScript has only `titanHtml` state/message/resource and
page-local saved view-state operations, never plugin objects or game SDK access. C++ `htmlUiAvailable()` and
JS/TS `titan.htmlPanels.available` / `titan.htmlOverlays.available` report an
observed connected renderer for this client. False includes both unobserved and
unavailable, and can be returned before the first rendered activation or after
the last one closes. This is observational status, not an installation preflight.
Register HTML surfaces unconditionally in every SDK and choose visibility from
plugin state, never from this flag: suppressing the first activation based on
false would prevent availability from being established. Missing support never
prevents an otherwise compatible native plugin from loading; the controller
provides native diagnostics and retry controls when a requested renderer fails.

Side panels share the existing aggregate limit of eight and one ID namespace.
HTML overlays allow eight definitions per plugin and eight active per client,
with a separate overlay name namespace shared with native overlay panels.
Native-to-native overlay name behavior and native overlay limits are unchanged.
HTML overlays default to click-through, 220×160 CSS pixels, Dynamic anchor,
priority 50, and visible. Width is 80..600, height 24..600. Input bits opt into
buttons (1), scroll (2), and text (4) in C++/JS. Java's annotation uses
`interactive=true` to enable all three. Tooltip overlays remain click-through.

See [the complete bundle/bridge contract and examples](../docs/html_ui.md).

## Preview pills (SDK v142)

Short labels a plugin pins over its tab's thumbnail on the controller's Home
grid, so the state of every tab reads at a glance without opening it. Break
Handler is the first user: while a break runs, its card shows
`Breaking: 00:12:34`, counting down.

```cpp
#include <titan/preview_pills.h>

auto pills = titan::previewPills(*this);           // this plugin's pills
pills.set("break", {"Breaking:", titan::PanelTone::info,
                    std::chrono::minutes(12)});    // "Breaking: 00:12:00", ticking down
pills.set("task", {"Mining iron"});                // plain text, neutral tone
pills.clear("break");
```

| Symbol | Purpose |
| --- | --- |
| `titan::previewPills(plugin)` -> `titan::PreviewPills` | This plugin's pills. The host derives the owner from the plugin instance it loaded, never from a string the caller passes, so a plugin reaches only its own pills. |
| `titan::PreviewPill` | `text`, `tone` (`titan::PanelTone`), and `countdown` (`std::optional<std::chrono::milliseconds>`, time left from now). |
| `set(key, pill)` | Show or replace one pill; `bool`. |
| `clear(key)` / `clearAll()` | Remove one pill or all of this plugin's; `bool`. |

The same pills in every runtime (JS/TS and Java since SDK 142 / Java 0.1.67):

| Operation | C++ | JS / TypeScript | Java |
| --- | --- | --- | --- |
| Owner | `titan::previewPills(*this)` | `titan.previewPills`, passing the object given to `titan.register` as `plugin` | `net.titan.api.pills.PreviewPills.of(this)` |
| Set | `set(key, {text, tone, countdown})` -> `bool` | `set(plugin, key, {text, tone, countdownMs})` -> `boolean` | `set(key, PreviewPill.of(text).withTone(tone).withCountdown(duration))` -> `boolean` |
| Clear one | `clear(key)` -> `bool` | `clear(plugin, key)` -> `boolean` | `clear(key)` -> `boolean` |
| Clear all | `clearAll()` -> `bool` | `clearAll(plugin)` -> `boolean` | `clearAll()` -> `boolean` |

Tones are the `titan::PanelTone` / `titan.PanelTone` / `PanelTone` values the
side-panel badges use, so a pill matches the plugin's own panel.

- **Countdowns tick on their own.** A countdown is drawn after the text as
  `HH:MM:SS`, measured from the moment of the call. The controller ticks it,
  so set it once; it stops at `00:00:00` and stays there until the plugin
  clears or replaces the pill.
- **Limits.** Keys are 1-32 characters of `[A-Za-z0-9._:/-]`. A plugin shows
  at most 2 pills and a tab at most 8; a new pill past either limit is
  refused, never swapped for another. Text becomes one line of UTF-8, cut to
  63 bytes on a character boundary, and a pill needs text, a countdown or
  both. Countdowns past 999:59:59 are shortened to it. The controller cuts
  text that does not fit the card and folds pills that do not fit into a
  final `+N`.
- **Order.** Pills keep the order they were first set in; replacing one
  keeps its place.
- **Lifetime.** Only an enabled plugin may set a pill. Disabling, unloading
  or reloading the plugin (or rebuilding the JS runtime) drops every pill it
  set, so a pill never outlives the plugin that drew it.
- **Where they show.** Over the tab's thumbnail on the Home grid, stacked
  from its top-left corner; hovering one names the plugin that set it. Never
  in the game view.
- **Threading.** Every call takes only the host's pill-store lock, so it is
  safe from any thread and from inside your own locks. Calls from the
  plugin's constructor fail: the host binds the plugin once it has wrapped
  the instance.

---

## Cross-Tab Store (SDK v141)

Small values that every tab launched by the same controller sees, for the
life of one controller sign-in session. Use it for state that belongs to the
session rather than to one tab. Account Profiles is the first user: it
shares its unlocked vault password, so unlocking once unlocks every tab and
Lock in any tab locks them all.

```cpp
#include <titan/cross_tab.h>

auto ct = titan::crossTab(*this);                  // this plugin's namespace
ct.put("route.last", "lumbridge");                 // every tab sees it
ct.put("vault.password", password, {.secret = true});
if (auto value = ct.get("route.last")) use(value->bytes);  // non-secret keys only
titan::SecureBuffer held;                          // zeroed before it is freed
if (ct.getSecret("vault.password", held)) unlock(held.view());
ct.erase("vault.password");                        // every tab loses it
```

| Symbol | Purpose |
| --- | --- |
| `titan::crossTab(plugin)` -> `titan::CrossTab` | This plugin's namespace. The host derives it from the plugin instance it loaded, never from a string the caller passes, so a plugin reaches only its own keys. |
| `put(key, bytes, titan::CrossTabOptions{.secret})` / `erase(key)` | Unconditional writes; `bool`. |
| `putIf(key, bytes, expectedVersion, options)` / `eraseIf(key, expectedVersion)` | Conditional proposals; `std::optional<uint64_t>` write id. `expectedVersion` 0 means "only if absent". |
| `get(key)` -> `std::optional<titan::CrossTabValue>` | `bytes`, `version`, `pending`. Refuses secret keys. |
| `getSecret(key, titan::SecureBuffer&, titan::CrossTabInfo*)` | Reads a key into a buffer that zeroes its memory; false when the key is absent or redacted here. |
| `info(key)` -> `std::optional<titan::CrossTabInfo>` | `version`, `size`, `secret`, `pending`, `redacted`, without the value. |
| `titan::SecureBuffer` | Move-only bytes, zeroed before release: `data()`, `size()`, `empty()`, `view()`, `clear()`. |
| `Plugin::onCrossTabChanged(const titan::CrossTabChange&)` | `key`, `kind` (`Set` / `Erased` / `Rejected`), `origin` (`Remote` / `Replay` / `Outcome`), `cause` (`Writer` / `SessionReset` / `Limit` / `Conflict` / `NotPermitted`), `secret`, `redacted`, `version`, `writeId`. Never carries the value. |

The same store in every runtime (JS/TS and Java since SDK 141 / Java 0.1.66):

| Operation | C++ | JS / TypeScript | Java |
| --- | --- | --- | --- |
| Namespace | `titan::crossTab(*this)` | `titan.crossTab`, passing the object given to `titan.register` as `plugin` | `net.titan.api.crosstab.CrossTab.of(this)` |
| Put | `put(key, bytes, {.secret = true})` -> `bool` | `put(plugin, key, value, {secret: true})` -> `boolean`; `value` is a `string` (stored as UTF-8), `ArrayBuffer` or `Uint8Array` | `put(key, byte[], CrossTabOption.SECRET)` / `putString(key, String, ...)` -> `boolean` |
| Erase | `erase(key)` -> `bool` | `erase(plugin, key)` -> `boolean` | `erase(key)` -> `boolean` |
| Conditional put | `putIf(key, bytes, expectedVersion, options)` -> `std::optional<uint64_t>` | `putIf(plugin, key, value, expectedVersion: bigint, options)` -> `bigint \| null` | `putIf(key, byte[], long expectedVersion, options...)` -> `OptionalLong` |
| Conditional erase | `eraseIf(key, expectedVersion)` -> `std::optional<uint64_t>` | `eraseIf(plugin, key, expectedVersion: bigint)` -> `bigint \| null` | `eraseIf(key, long expectedVersion)` -> `OptionalLong` |
| Read a non-secret key | `get(key)` -> `std::optional<titan::CrossTabValue>` | `get(plugin, key)` -> `CrossTabValue \| null` (`bytes: ArrayBuffer`, `text()`, `version: bigint`, `pending`) | `get(key)` -> `Optional<CrossTabValue>` (`bytes()`, `text()`, `version()`, `pending()`) |
| Read a secret | `getSecret(key, titan::SecureBuffer&, titan::CrossTabInfo*)` -> `bool` | `getSecret(plugin, key)` -> `ArrayBuffer \| null` | `getSecret(key)` -> `byte[]`, or `null` |
| State without the value | `info(key)` -> `std::optional<titan::CrossTabInfo>` | `info(plugin, key)` -> `CrossTabInfo \| null` | `info(key)` -> `Optional<CrossTabInfo>` |
| Change event | `Plugin::onCrossTabChanged(const titan::CrossTabChange&)` | `Plugin.onCrossTabChanged(change)` | `@Subscribe` with `CrossTabChanged` |
| Change enums | `titan::CrossTabChange::Set` ... `NotPermitted` | `titan.CrossTabChangeKind` / `CrossTabChangeOrigin` / `CrossTabChangeCause` | `CrossTabChanged.Kind` / `Origin` / `Cause` (`SET` ... `NOT_PERMITTED`, `value()`) |

Versions and write ids are C++ `uint64_t`, JS/TS `bigint` and Java `long`;
the enum values equal the `CROSS_TAB_CHANGE_*`, `CROSS_TAB_ORIGIN_*` and
`CROSS_TAB_CAUSE_*` constants. JS and Java hand a secret out only as an
`ArrayBuffer` or `byte[]` the caller should zero once done: copies the
engine or the JVM makes cannot be scrubbed, and neither can a string passed
to `put` or `putString`. A JS call with any object other than the plugin's
own registered one fails. A Java `CrossTabChanged` also carries `pluginId()`,
because a subscriber registered without an owner hears every plugin's
changes.

- **Limits.** Keys are 1-63 characters of `[A-Za-z0-9._:/-]`. A value is raw
  bytes, at most 16 KiB; zero-length values are allowed. A namespace holds at
  most 64 keys, 128 KiB and 16 secrets; the whole store at most 4096 entries
  and 1 MiB. A tab holds at most 256 unacknowledged writes (512 KiB). A write
  that breaks a limit the tab can see returns false at once; one that breaks
  a store-wide limit is rejected later with `Limit`.
- **Lifetime.** The controller keeps the values in memory for one sign-in
  session. They outlive plugin reloads and tab restarts and are never written
  to disk. Titan sign-out, sign-in and controller exit forget them; each key
  that disappears this way arrives as `Erased` with cause `SessionReset`.
- **Unconditional writes** show in your tab at once (`pending`, version 0)
  and are resent until the controller acknowledges them; the last write the
  controller receives wins. You get no event for your own write unless the
  controller rejects it.
- **Every write is sent.** The outbox of unacknowledged writes does not
  coalesce: ten puts of one key are ten writes. Write when a value changes,
  not every tick.
- **Conditional writes** change nothing locally. The controller applies
  `putIf` / `eraseIf` only if the key's version still equals
  `expectedVersion`, and you hear `Outcome` (`Set` or `Erased`, with the new
  version) or `Rejected` (`Conflict` or another cause), carrying the write id.
- **`Rejected` means re-read.** Your tab has already reverted the key to the
  controller's state, and the event's `version` is that confirmed version (0
  when the key is absent). Read the key again before deciding what to do.
- **An erase reaches every tab**, including tabs that never held the key.
- **Replay.** Each time an instance binds (plugin load or reload, JS runtime
  rebuild) it gets one `Replay` event per key in its namespace, including
  keys its own tab wrote.
- **Secret is handling, not access control.** A secret is never logged,
  persisted, or put in settings, labels or gateway frames, and goes only to
  tabs this controller launched. Other (attached) tabs see it `redacted` and
  cannot write secrets. `get()` refuses secrets; read them with `getSecret()`.
  It does not hide a value from other plugins in the same tab: native plugins
  share the process, and JS and Java plugins share their runtime.
- **Callbacks** run on the game thread from the MainLoop drain, on the title
  and login screens too, for every loaded plugin whether or not it is
  enabled, with the host's dispatch lock held. Keep them cheap: note what
  changed and wake a worker. Queued changes to one key coalesce; outcomes
  never do.
- **Threading.** Every facade call takes only the store's own lock, so it is
  safe from any thread and from inside your own locks. Calls from the
  plugin's constructor fail: the host binds the namespace once it has wrapped
  the instance.
- **Namespaces are per runtime.** A JS or Java plugin with the same id as a
  native plugin never shares its keys.

---

## Queued GE buying (SDK v139 / Java 0.1.64)

One C++ host service executes purchases for native, JS/TS, Java, and the JS shell.
The SDKs expose request values and bindings; they do not run their own queues.
Use `<titan/utils/ge.h>` / `titan::utils::Ge`, `titan.utils.ge`, or Java
`net.titan.api.utils.Ge`.

Submit once while logged in; no plugin owner or callback context is required.
Keep the handle and poll `request(id)` on later ticks or shell evaluations.
C++/Java return 0 for rejected submissions; JS returns null. Rejection includes
invalid options, missing character, older hosts, shutdown, and the shared limit
of 256 retained requests. Do not resubmit every tick while a handle is pending.

Defaults match the buying utility: collect noted items to inventory, no
automatic opening, 3 attempts, and 10 seconds per offer. The total request
deadline defaults to 180 seconds, including navigation, slot waits and collection.
Options allow 1–10 attempts, 600–300000 ms per offer, and 1000–3600000 ms overall.
`autoOpen=true` opts into web walking to the GE and opening a clerk; otherwise
the queue waits for the interface. Any request opting in can open GE for the
queue. The three-argument `(itemId, quantity, bool)` overload means auto-open
in all SDKs.

Prices come from the in-game offer setup. Attempt one uses its guide price;
later attempts apply one additional +5% button press per previous attempt.
`maxUnitPrice` optionally caps the actual setup price before confirmation
(0 means no ceiling). The setup price reader currently supports positive signed
32-bit unit prices; unsupported/unreadable prices fail. Monetary snapshots and
totals are signed 64-bit, exposed as C++ int64_t, Java long, and JS bigint.
IDs/quantities remain signed 32-bit; JS request handles are bigint.

The scripter/user must supply enough coins in inventory or bank. Bank queries
use live-open-bank data, then the C++ ItemCache. Remembered gold is advisory:
it can be stale or unknown, and the game ultimately accepts or rejects payment.
This utility neither withdraws coins nor predicts cached balances from clicks.

Requests reserve an empty usable slot, select the item, set quantity/price,
and wait for a matching native offer. Other requests may be submitted while
earlier offers buy. Expired offers are aborted and collected before retrying
only the unfilled quantity. The final attempt also expires and is collected;
it cannot wait indefinitely. Collection takes priority over new submissions.
Coins go to the bank; items follow the requested item/note mode, with bank
fallback when inventory space is insufficient. Only the request's own slot
is touched; there is no collect-all operation.

`GeRequest` / C++ `RequestState` snapshots expose requestId, itemId, original
quantity, filled, remaining, attempts, slot, phase, unitPrice, spent and message.
Filled/spent include the current attempt and do not imply collection is finished.
Completed means the full purchase was observed and its slot cleared after
collection. Cancelled/Failed are terminal but are not successful purchases.
Check phase/message to distinguish partial failure and success.

`getRequests()` includes retained outcomes; `getExchangeQueue()` includes only
all pending work in the shared queue. `getQueueSize()`, `isExchanging()`,
and `getStatus()` are convenience queries. `abortRequest(id)` and
`clearExchangeQueue()` request abort-and-collect (the latter for the entire queue), rather than discarding live
offers. `release(id)` removes a terminal handle; call it after consuming the
outcome.

Disabling or unloading a plugin does not cancel its purchases. Use request
handles to cancel explicitly when needed. Logout, character changes, shutdown,
and the overall deadline stop
automation and leave existing offers for manual management. Requests are not
persisted or resumed automatically. Slot/item/quantity/price mismatches fail
without manipulating the replacement offer. Avoid sharing the GE interface
with manual actions or another automation while requests are active.

Dev Tools → **Grand Exchange Inspector** shows retained requests and their
auto-open setting, the observed search/quantity input, native offer slots, and
the last 64 action attempts. **Copy GE diagnostics** exports those values for
debugging. UI observations are captured on game ticks and show their age.
The inspector can cancel a selected request or release a terminal result.
Item search and quantity entry edit the observed input in short batches,
waiting for the next observed update before sending more text.

`addSellToQueue(itemId, quantity)` is explicitly unsupported and always returns
0/null without queueing anything.

```cpp
#include <titan/utils/ge.h>
titan::utils::Ge::BuyOptions options;
options.itemId = 4151;
options.quantity = 1;
options.maxUnitPrice = 5000000;
auto handle = titan::utils::Ge::addBuyToQueue(options); // submit once
// On later ticks:
if (auto r = titan::utils::Ge::request(handle); r && titan::utils::Ge::isComplete(*r)) {
    // Inspect r->phase, filled and message before releasing.
    titan::utils::Ge::release(handle);
}
```

```ts
const handle = titan.utils.ge.addBuyToQueue({
    itemId: 4151, quantity: 1, maxUnitPrice: 5000000n
});
// On later ticks:
const progress = handle === null ? null : titan.utils.ge.request(handle);
```

The JS shell can submit directly and open GE automatically:

```js
globalThis.geBuyId = titan.utils.ge.addBuyToQueue({itemId: 314, quantity: 1, autoOpen: true, maxUnitPrice: 100n});
return geBuyId;
```

Poll in a later shell evaluation (the replacer preserves bigint values):

```js
return JSON.stringify(titan.utils.ge.request(globalThis.geBuyId),
    (_, value) => typeof value === "bigint" ? value.toString() : value, 2);
```

```java
long handle = Ge.addBuyToQueue(new GeBuyOptions(
    4151, 1, true, false, true, 3, 10000, 180000, 5000000L));
// On later ticks:
Ge.request(handle).ifPresent(r -> {
    if (r.isComplete()) {
        // Inspect r.phase(), r.filled(), and r.message().
        Ge.release(handle);
    }
});
```

## Remembered bank items (SDK v138)

One C++ host-owned ItemCache serves every plugin/runtime. Java and JS expose
read-only bindings and snapshot query helpers; neither maintains another cache.
Include `<titan/item_cache.h>`
in C++; use `state::itemCache()`, JS/TS `state.itemCache`, or Java `ItemCache`.
`bank()` returns an owned snapshot (`nullopt` / `null` / `Optional.empty()` only
when unavailable). A supported cache with no observation has `known=false`;
a known empty bank has `known=true` and an empty `items` collection.

Snapshot fields are `account`, `known`, `live`, `loading`, `lastObservedAt`,
`error`, and `items` (slot, ID, quantity). Times are UTC Unix seconds, represented
as C++ `int64_t`, Java `long`, and JS `number`. Each item quantity is signed 32-bit;
count totals use C++/Java 64-bit integers and JS numbers (the bounded bank sum
fits exactly). Values are owned; Java and JS collections are immutable, and
cached Java items are read-only values without interaction dispatch.

`isLoaded` (a method in C++/Java, a property in JS) means the bank is known.
`getBankItems()`, `count(ids...)`, and `getItemsCountInBank(ids...)` exclude
zero-quantity placeholders. C++ accepts an ID or an initializer list of IDs.
`countByName(names, ignore)` matches case-insensitive substrings using item
definitions, counting each item once; exclusions win. Names/exclusions are
initializer lists in C++, arrays in JS, and arrays/varargs in Java.
`getItemsCountInBank` also accepts names and optional exclusions.

Bank `getAll`, `find`, `contains`, and `count` prefer a readable native container
while the bank is open, then fall back to this shared memory, then empty.
A valid empty live bank takes precedence over remembered contents. A retained
closed-bank native container is not treated as live evidence.
Withdrawal/action helpers still require an open bank and resolve slots from the
live container. Raw `itemContainer(BANK)` remains a native-container query.
Older hosts without the cache callback can still read an open bank; otherwise
bank reads return empty.

The current character is keyed by normalized display name. Snapshots are stored
under `%USERPROFILE%\.titanclient\item_cache`, loaded/saved off the game thread,
and reset from the active view on logout/account change. Name changes use a new
file. Opening the bank establishes a native observation for each login session;
a saved snapshot can be read before then. Once established, actual closed-bank
container changes update memory, but unchanged retained containers do not claim
freshness. `live=false` quantities are remembered and may be stale. Disk errors
retain current memory and populate `error`. Unknown counts return zero: use
`known` to distinguish them from confirmed zero stock.

GE bank collections are not inferred from clicks; confirmed collection
reconciliation belongs to the future GE utility. Dev Tools > Item Cache Inspector
shows the current character, status, age, items, quantities, and storage errors.
ThePlug serialized cache files are not imported; first open seeds Titan memory.

## Shared item prices (SDK v137)

The client owns one asynchronous OSRS Wiki price and item catalogue cache. All
plugins share its requests and results; enabling or disabling one plugin does
not start or stop another plugin's work. Queries copy owned values and never
perform network I/O. The first explicit request starts the worker lazily.

| Operation | C++ | JS / TypeScript | Java |
| --- | --- | --- | --- |
| Facade | `state::itemPrices()` | `state.itemPrices` | `Titan.itemPrices()` or `Client.itemPrices()` |
| Request catalogue | `requestCatalog()` | `requestCatalog()` | `requestCatalog()` |
| Request one price | `request(id)` | `request(id)` | `request(id)` |
| Cache/request status | `status()` | `status()` | `status()` |
| Item metadata | `item(id)` | `item(id)` | `item(id)` |
| Full metadata catalogue | `items()` | `items()` | `items()` |
| Cached price/status | `price(id)` | `price(id)` | `price(id)` |

Include `<titan/item_prices.h>` or `<titan/client.h>` in C++. Metadata fields are
`id`, `name`, `examine`, `members`, optional `buyLimit` and optional `highAlch`.
Price fields are `id`, optional `high`, `low`, `highTime`, `lowTime`, plus
`fetchedAt`, `lastAttemptAt`, `loading`, `pending` and `error`. High and low are
latest instant-buy and instant-sell prices. Times are UTC Unix seconds. Missing
amounts differ from zero. C++ uses `int64_t` / `std::optional<int64_t>`, Java uses
`long` / `OptionalLong`, and JS/TS uses `bigint` / `null` for every 64-bit field,
including timestamps and catalogue revisions. Java and JS values and collections
are immutable; C++ returns independent owned values.

`status().available` means a successful catalogue is cached; it does **not** mean
the service is absent when false. Loading, pending and error fields are meaningful
before the first success. Status also exposes `catalogLoading`, `catalogPending`,
`pendingCount`, `loadingItem` (`-1` idle, `0` catalogue, positive item ID),
`catalogRevision`, `catalogFetchedAt`, `catalogLastAttemptAt` and `error`.
Revision increases only when a new catalogue is committed. Cache your own
search index and rebuild it only when this revision changes. A concurrent
catalogue replacement may make `items()` return an empty collection; retry on a
later tick. Unknown metadata or unrequested/evicted prices return C++
`std::nullopt`, Java `Optional.empty()` or JS `null`. A queued price already has
a status record with absent amounts and `pending` or `loading` set.

Requests return true when queued, already queued/loading, or still cached within
their TTL/backoff. False means an invalid ID, a full queue, an older unsupported
host, or shutdown. Requests require positive IDs; there is no shared cancellation
operation. The FIFO queue holds at most 128 waiting requests, coalesces duplicate
IDs, and starts at most one network request per second. Successful price results
are cached for one minute and the catalogue for 24 hours. Failures retain stale
values and use a 15-second to 5-minute retry backoff. Calls during backoff return
the existing result; request again after the backoff to retry. Up to 256 item
prices are retained, evicting idle entries as needed. Responses and parser depth
are bounded; requests use short HTTP timeouts and an overall deadline.

These are public main-game prices, separate from personal Grand Exchange offers
and special-economy worlds. Requests contain only the public item ID. Client
unload preparation cancels queued work and joins the worker outside the Windows
loader lock before reporting readiness. All supported loader strategies must
complete `TitanPrepareForUnload` before releasing the client image; registered
modules also retain a worker reference. Process exit performs no worker joins.

## Grand Exchange offers (SDK v136)

Personal offers are owned snapshots from the native offer array. They remain valid
after later updates, logout, or event delivery. Slots are zero based; use the
returned collection size rather than assuming a slot count.

| Operation | C++ | JS / TypeScript | Java |
| --- | --- | --- | --- |
| Full snapshot available | `state::grandExchange().available()` | `state.grandExchange.available` | `Titan.client().isGrandExchangeAvailable()` |
| One slot | `state::grandExchange().offer(slot)` | `state.grandExchange.offer(slot)` | `Titan.client().getGrandExchangeOffer(slot)` |
| All slots | `state::grandExchange().offers()` | `state.grandExchange.offers()` | `Titan.client().getGrandExchangeOffers()` |
| Change event | `Plugin::onGrandExchangeOfferChanged(const GrandExchangeOfferChangedEvent&)` | `onGrandExchangeOfferChanged(event)` | `@Subscribe` with `GrandExchangeOfferChanged` |

C++ symbols are in `titan`, JS/TS symbols in `titan`, and Java offer types in
`net.titan.api` with the event in `net.titan.api.events`. Include
`<titan/grand_exchange.h>` or `<titan/client.h>` in C++. The C++ and JS/TS client
facades also expose `getGrandExchangeOffer`, `getGrandExchangeOffers`, and
`isGrandExchangeAvailable` aliases.

An offer exposes `slot`, `itemId`, `totalQuantity`, `quantitySold` (completed
quantity for either side), `price` (per unit), `spent` (completed gold for either
side), `state`, and raw `status` / `type`. C++ and Java use methods; JS/TS uses
readonly properties. C++ and Java also provide RuneLite-style `getItemId()`,
`getTotalQuantity()`, `getQuantitySold()`, `getPrice()`, `getSpent()`, and
`getState()` aliases. Price and spent are C++ `int64_t`, Java `long`, and JS/TS
`bigint`; use bigint arithmetic in scripts, for example `offer.price * 2n`.

States are `Empty`, `CancelledBuy`, `CancelledSell`, `Buying`, `Bought`,
`Selling`, `Sold`, and `Unknown` in C++; Java uses uppercase underscore names.
JS/TS supports both spellings. An unreadable or unavailable slot returns
C++ `std::nullopt`, JS/TS `null`, or Java `Optional.empty()`, while an unavailable full collection returns an empty
collection. A valid empty slot has state `Empty` / `EMPTY`. During login, an
individual captured slot can be available before the full collection is ready.
Full collection queries also wait for queued offer events and current callbacks
to finish delivery, including events deferred by a plugin lifecycle change.
During that interval, availability is false and the collection is empty; owned
event payloads and readable individual slots remain usable. Initialize a tracker
from the full collection in a client-tick callback once it is available, then
apply subsequent change events. Do not treat the initial empty notifications
as new trades or as collection of previously saved offers.

Change events contain an owned `offer` and its `slot`. Callbacks run on the game
thread at a safe dispatch phase after native processing; plugins never run inside
the offer replacement function. Each session begins with an empty notification
for every slot, followed by observed offers. Attaching while logged in also
seeds the existing nonempty offers. Separate native replacements remain separate
events even when their contents match or they occur within one tick. Read failures
do not produce empty-offer events. Logout/login and world hops reset the query
cache; ordinary scene loading retains it. Already captured events remain in FIFO
order, so the event snapshot can differ from a fresh query during delivery.

## The four shapes of the SDK

The plugin SDK is organised into **four** orthogonal groups of entry
points. Every public symbol that produces or operates on game state lives
under exactly one of them:

| Shape | C++ namespace | TS namespace | What it is |
| --- | --- | --- | --- |
| **Queries** | `titan::queries::*` | `titan.queries.*` | Chainable, filterable list views over live game collections. Read-only by default; entity wrappers expose `.interact(action)` for menu-driven dispatch. |
| **State** | `titan::state::*` | `titan.state.*` | Facades that read or manipulate a specific subsystem (client, camera, world map, widgets, skills / prayers / vars, walk, idle, login, hider, cache, collisions, item containers, item defs, world / world-hop). Each facade owns its own slice of the host. |
| **Utils** | `titan::utils::*` | `titan.utils.*` | Header-only helper namespaces composed over the queries + state primitives -- one-call wrappers for common compositional jobs (inventory predicates, dialogue continue, combat orb, equipment unequip, ...). No new ABI surface. |
| **Gamevals** | `titan::gamevals::*` | `titan.gamevals.*` | Generated native gameval constants and metadata lookups such as `ItemID::ABYSSAL_WHIP`, `ObjectID::BANKBOOTH`, and `QuestID::QUEST_ANIMALMAGNETISM`. |

Free helpers that are NOT subsystem facades stay at the top level:
`titan::log`, `titan::logf`, `titan::addChatMessage`, `titan::runOnClientTick`,
`titan::runOnRender`, plus the registration macros and `titan::Plugin`
itself.

If you can't decide whether a new helper goes under `state::` or
`utils::`: **ask "does this need to grow the ABI?"**. If yes, it's a new
state facade. If it can be implemented purely on top of existing state +
queries, it's a util.

---

## Naming and call-style conventions

The SDK exposes the same entry points to three languages (C++, TypeScript,
JavaScript). Each language uses its native idiom; the rules below are what
every public symbol follows so that a plugin author can guess the spelling
of a new symbol from its category.

### C++ (`titan::*`)

- Every accessor is a method (parens forced by the language).
- **No `get` prefix on facade accessors.** Modern C++ / Google style:
  `client().tick()`, `camera().yaw()`, `client().localPlayer()` -- not
  `getTick()` / `getYaw()` / `getLocalPlayer()`. The `()` already signals
  "this is a method", so the `get` would be redundant noise.
- **Predicates use `is*` / `has*` / `contains` prefixes.** `isOpen`,
  `isFull`, `isAnimating`, `hasAction`, `hasMoved`, `containsAny`. Bare
  predicate verbs (`open()`, `moved()`, `valid()`) are out of style.
- **Mutators are imperative verbs.** `drop`, `interact`, `walk.to(...)`,
  `setPlayers(true)` (on `HiderFacade`), `setUsername(...)`. Setter form
  uses `set*` so the read/write pair is symmetric (`setPlayers(true)` /
  `isPlayersHidden()`).
- **`find*` is reserved for "search returning optional".** Compare:
  `widgets().find(packedId)` (returns `std::optional<Widget>`,
  may miss) vs `widgets().findByText(query)` (same shape, different
  filter). The bare-noun form (`cache().item(id)`) is reserved for
  unambiguous lookups that return optional implicitly.
- **Util namespaces (`titan::utils::*`) keep verb-prefixed names**
  (`getAll`, `getSlot`, `getByIds`, `getByNames`). They are namespaced
  free functions, not facade methods, so the verb form makes the call
  site read naturally as a procedure invocation.

### TypeScript / JavaScript (`titan.*`)

- **Zero-arg primitive state lookups are properties** (no parens).
  Examples: `state.client.tick`, `state.client.loggedIn`,
  `state.camera.yaw`, `utils.inventory.isOpen`, `utils.inventory.size`,
  `utils.combat.specialAttackPercentage`. This matches the JS standard
  library: `Set.size`, `Array.length`, `Map.size` are all properties.
- **Methods are used when the call takes args, allocates a fresh
  collection, or has a side effect.** Examples:
  `state.widgets.find(id)`, `utils.inventory.drop(id)`,
  `utils.inventory.getAll()` (allocates), `utils.inventory.contains(id)`,
  `state.skills.boosted(skill)`. Same shape as JS `Set.has(x)` /
  `Map.get(k)` / `Array.includes(x)`.
- **Skill catalog names:** `titan.Skill.COOKING`-style uppercase members
  are canonical, and PascalCase aliases like `titan.Skill.Cooking` mirror
  the same ordinals. `state.skills.*(skill)` accepts integers `0..23` and
  throws for missing, non-integer, or out-of-range values.
- **Property/method symmetry with C++:** if C++ has `foo()` returning a
  primitive, TS / JS exposes a property `foo`. If C++ has `foo()`
  returning an allocated array / object, or `foo(args)`, TS / JS
  exposes a method `foo()` / `foo(args)`. Same nouns, same hierarchy,
  same semantics -- spelling adjusted per language.

### `::` vs `.` in C++ -- not a stylistic choice

`titan::queries::npcs()` mixes `::` and `.` because:

- `::` is the C++ scope-resolution operator; it walks namespaces and
  classes at compile time. `titan::queries::npcs` is a free function in
  a namespace.
- `.` is the C++ member-access operator on a value. Calling `npcs()`
  returns an `NpcQuery` instance; methods on that instance are accessed
  with `.` (e.g. `npcs().nameContains("Goblin").first()`).

The mix is forced by C++ semantics, not a styling lapse. The same
fluent-then-instance pattern is used throughout the modern C++ ecosystem
(`std::ranges::views::filter(...).transform(...)` etc.).

### Worked example -- the same accessor in all three languages

```cpp
// C++: every accessor is a method, no `get` prefix on facades, `is*` on predicates.
if (titan::utils::Inventory::isOpen() && !titan::utils::Inventory::isFull()) {
    titan::utils::Inventory::drop("Iron ore");
}
if (auto self = titan::state::client().localPlayer(); self) {
    titan::queries::npcs().nameContains("Goblin").first();
}
```

```ts
// TS: zero-arg primitives are properties; allocations / args / mutations are methods.
if (titan.utils.inventory.isOpen && !titan.utils.inventory.isFull) {
    titan.utils.inventory.drop("Iron ore");
}
const self = titan.state.client.localPlayer;        // property (zero-arg primitive lookup)
titan.queries.npcs().nameContains("Goblin").first(); // chainable query, methods all the way
```

The C++ method `state::client().localPlayer()` and the TS property
`state.client.localPlayer` describe the same accessor -- they just spell
it in each language's idiom.

---

## Global headers (public)

Plugins include these directly. `detail/` contains facade implementation and
the Native ABI contract headers needed by this public SDK. Prefer the fluent
headers below; publishing native contracts does not expose private client IPC.

| Header | Content |
| --- | --- |
| `<titan/plugin.h>` | `Plugin` base + registration macros and the Native ABI v1 factory |
| `<titan/worker.h>` | `PluginWorker` and `startPluginWorker(owner, fn)` with instance context and a lifetime lease retained through join |
| `<titan/client.h>` | Top-level free helpers (`titan::log`, `titan::logf`, `titan::addChatMessage`, `titan::runOnClientTick`, `titan::runOnRender`) plus the **state** facade factories under `titan::state::*` (`client()`, `camera()`, `hider()`, `audio()`, `cache()`, `vars()`, `skills()`, `prayers()`, `script()`, `widgets()`, `idle()`, `proxy()`, `login()`, `walk()`, `itemContainer()`, `itemDef()`, and `world::current() / list() / hop() / hopByListIndex() / hopIngame()`) |
| `<titan/query.h>` | **Query** factories under `titan::queries::*` -- `npcs()`, `players()`, `objects()`, `groundItems()`, `inventory()`, `projectiles()`, `graphicsObjects()`, `widgets([groupId])` |
| `<titan/collision.h>` | `titan::CollisionFlag::*` masks + `titan::state::collisions()` with live single-tile reads and SDK 112 immutable bulk cached-region/current-scene snapshots. |
| `<titan/definition_extras.h>` | Optional `titan::definitions` reads: `widgetModelId(address)`, `npcBaseId(worldViewId, hashIndex)`, and `itemWornActions(itemId)`. Live reads require the game thread; worn labels come from immutable cache definitions. Missing capability/data returns `nullopt`. Negotiated separately from native records; since SDK v146 every widget read also carries the model id as `Widget::modelId()`. |
| `<titan/web_walker.h>` | Read-only asynchronous `titan::webWalker()` path generation: `submit`, `poll`, `copySteps`, `cancel`, and `release` (SDK 112). |
| `<titan/web_walker_provider.h>` | Versioned walker provider contracts: V1 planning compatibility and V2 complete planning, payload, and execution service. The host currently selects external providers through the entitled `web_walker_provider` DEV policy. Normal consumers use `<titan/web_walker.h>`. |
| `<titan/world_point.h>` | Lightweight public `Tile`, `WorldPos`, and `WorldPoint` coordinate contract. `<titan/actor.h>` includes it for compatibility. |
| `<titan/world_map.h>` | Read-only `titan::state::worldMap()` snapshots and exact world/screen plus logical-pixel/tile transforms for the visible native world map, including interface scaling from logical widget units to physical overlay pixels (SDK 113). |
| `<titan/screenshot.h>` | Asynchronous full-frame game screenshots: `titan::screenshot()` / `titan::state::screenshot()` with `submit`, `poll`, `copyPng`, and `release`. The same image the controller's `/tabs` command returns -- the presented backbuffer after the AboveWidgets pass -- as PNG bytes (SDK 131). |
| `<titan/render.h>` | `titan::Overlay`, `titan::overlay()` draw API |
| `<titan/overlay_panel.h>` | `titan::OverlayPanel` (subclass of `titan::Overlay`), `titan::Anchor` enum, `titan::OverlayPanelStyle` struct -- structured HUD panels with anchor-based layout, Alt-drag repositioning, and full theming. The `Plugin::overlayPanel(name, anchor, [priority,] lambda)` factory in `<titan/plugin.h>` lives here too (definitions are inline in this header). Added in SDK 46. |
| `<titan/actor.h>` | `Player`, `Npc`, `TileObject`, `GroundItem`, `Item`, `Projectile`, `GraphicsObject`, `Actor`; it transitively includes `<titan/world_point.h>` for source compatibility. |
| `<titan/plugins.h>` | `titan::plugins()` plugin-manager handle plus the legacy ambient and SDK 97 instance-owned cross-plugin service registration overloads |
| `<titan/break_handler.h>` | `titan::BreakHandler`, `BreakCommand`, `BreakPhase`, and `BreakMode`: instance-based coordinated-break participation (SDK 97) |
| `<titan/cross_tab.h>` | Cross-Tab Store: `titan::crossTab(plugin)` returning `titan::CrossTab` with `put`, `erase`, `putIf`, `eraseIf`, `get`, `getSecret` and `info`; `titan::CrossTabOptions`, `titan::CrossTabInfo`, `titan::CrossTabValue` and the zeroing `titan::SecureBuffer`. `titan::CrossTabChange` is in `<titan/plugin.h>`. See [Cross-Tab Store](#cross-tab-store-sdk-v141) (SDK 141). |
| `<titan/preview_pills.h>` | Preview pills: `titan::previewPills(plugin)` returning `titan::PreviewPills` with `set`, `clear` and `clearAll`, and the `titan::PreviewPill` value (`text`, `tone`, `countdown`). See [Preview pills](#preview-pills-sdk-v142) (SDK 142). |
| `<titan/account_profiles.h>` | Versioned POD `titan.account_profiles.v1` service contract for sanitized Profiles status/snapshots, unlock-existing, and pollable account activation (SDK 97) |
| `<titan/inventory_id.h>` | `titan::InventoryID` enum + `inventoryIdName()` helper. Added in SDK 26. |
| `<titan/setting.h>` | `BoolSetting`, `IntSetting`, `IntInputSetting` (typed number box instead of a slider), `ColorSetting`, `ComboSetting`, `StringSetting`, `ProtectedStringSetting`, `ButtonSetting` (value-less action button; SDK 98), `MatrixRow` + `MatrixSetting` (checkbox grid; SDK 135) |
| `<titan/panel.h>` | `titan::Panel` (control builders, plus SDK 72 semantic `section`, `status`, styled buttons, password/multiline inputs, and paired collapsibles; SDK 96 layout containers `beginGroup`/`endGroup`, `beginChild`/`endChild`, `beginCard`/`endCard`, and `beginAlign`/`endAlign` with `Panel::Align`; SDK 104 `combo`, `beginDisabled`/`endDisabled` scope, `help` marker, and `badge` pill, plus a renderer fix so slider/combo labels sit above a full-width control), `titan::SidePanel` (per-panel id/title/icon/image + build & action callbacks; registered via `Plugin::panel(...)`). SDK 65+. |
| `<titan/events.h>` | `ScriptEvent`, `VarbitChangedEvent`, `ChatMessageEvent`, `HitsplatAppliedEvent`, `ActorSpotAnimEvent`, `AnimationChangedEvent`, `MenuClickEvent`, `MouseButtonEvent` (SDK 115) |
| `<titan/gamevals.h>` | Generated native gameval constants under `titan::gamevals::{ItemID,ObjectID,NpcID,InterfaceID,VarbitID,InventoryID,VarPlayerID,VarClientID,DBTableID,DbRowID,QuestID,AnimationID,SpotanimID}` plus metadata helpers (`byId`, `QuestID::byQuestId`, `QuestID::byRowId`) and source-catalog metadata. Added in SDK 87. |
| `<titan/utils/inventory.h>` | Header-only inline `titan::utils::Inventory::*` -- inventory state predicates (`isOpen`, `isFull`, `isEmpty`, `size`, `emptySlots`), reads (`getAll`, `get`, `getSlot`, `getByIds`, `getByNames`), predicates (`contains`, `count`, `containsAny`, `containsAll`) and `drop(id)`. Composed over `titan::queries::inventory()` + `titan::state::widgets()`. Added in SDK 41. |
| `<titan/utils/dialogue.h>` | Header-only inline `titan::utils::Dialogue::*` -- continue / make / quest-scroll wrappers built on `titan::state::widgets()`. Composition only. |
| `<titan/utils/combat.h>` | Header-only inline `titan::utils::Combat::*` -- special-attack orb + auto-retaliate toggle built on `titan::state::widgets()` / `titan::state::client()` / `titan::state::vars()`. Composition only. |
| `<titan/utils/prayers.h>` | Header-only inline `titan::utils::Prayers::*` -- active-state reads plus raw toggle and idempotent set/enable/disable actions for every `Prayer` enum value. Composed over `titan::state::vars()` / `titan::state::widgets()`. Added in SDK 102. |
| `<titan/utils/mouse.h>` | Header-only inline `titan::utils::Mouse::*` -- action click-point resolution for menu entries, using the same target-aware native clickbox/projected-footprint resolver as synthetic dispatch. Added in SDK 94. |
| `<titan/utils/equipment.h>` | Header-only inline `titan::utils::Equipment::*` -- equipment container queries + unequip dispatch built on `titan::state::itemContainer()` / `titan::state::itemDef()` / `titan::state::widgets()`. `unequip` returns action accepted / queued; confirm state via `onItemContainerChanged`. Composition only. Added in SDK 40. |
| `<titan/utils/ge.h>` | Host-driven queued buying: `Ge::addBuyToQueue`, `request`, `getRequests`, `getExchangeQueue`, `getQueueSize`, `isExchanging`, `getStatus`, `abortRequest`, `clearExchangeQueue`, `release`; options and request phases. Selling stub returns 0. SDK 139. |
| `<titan/utils/bank.h>` | Header-only inline `titan::utils::Bank::*` -- bank state reads (`isOpen`, `isGeOpen`, `isNotedMode`, `getBankTab`), container queries (`contains`, `count`, `find`), actions (`close`, `depositAll`, `depositEquipment`, `withdrawItem`, `withdrawAllItem`, `withdrawItemAmount`, `open`), PIN helpers (`isPinVisible`, `pinRequestedDigitIndex`, `typePin`), and `LoadoutRunner` class. Composed over `titan::state::widgets()` / `titan::state::vars()` / `titan::state::itemContainer()` / `titan::queries::objects()` / `titan::keyboard::typeString()`. Added in SDK 44. |
| `<titan/utils/deposit_box.h>` | Header-only inline `titan::utils::DepositBox::*` -- bank deposit box state reads (`isOpen`, `isDepositAllSelected`) and component-operation actions (`close`, `depositInventory`, `depositWorn`, `depositLootingBag`, `selectDepositAll`) built on `titan::state::widgets()` / `titan::state::vars()`. Composition only. Added in SDK 133. |
| `<titan/keyboard.h>` | Inline `titan::keyboard::*` facade -- `sendString`, `sendKey`, `typeString`, `cancelTypeString`, `isTyping`. Routes through `IBackend` keyboard virtuals. Added in SDK 44. |
| `<titan/bank_sets.h>` | `titan::BankSets::kBooths`, `kChests`, `kDepositBoxes`, `kAllBanksAndChests` constexpr arrays + `isBankObject(id)` helper. Added in SDK 44. |
| `<titan/equipment_slot.h>` | `titan::EquipmentSlot` enum + `EquipmentSlotInfo::{name, slotWidgetPackedId, isValid, fromOrdinal}` helpers. Added in SDK 40. |
| `<titan-plugin-sdk.d.ts>` | TypeScript types for JS plugins (mirrors the same three-shape layout: `titan.queries`, `titan.state`, `titan.utils`) |

---

## Top-level free helpers

| Symbol | Purpose | Thread |
| --- | --- | --- |
| `titan::log(msg)` | Write to the client log | any |
| `titan::logf(fmt, ...)` | Printf-style log (bounded 1 KB buffer) | any |
| `titan::addChatMessage(type, name, msg, sender)` | Inject a local chat line (client-only, no packet). See `<titan/client.h>`. | any -- dispatched to game thread |
| `titan::runOnClientTick(fn)` | Queue a callback onto the next client-tick drain (SDK 116+: the callback thunk is freed exactly once even when the queued task is dropped) | any |
| `titan::runOnRender(fn)` | Queue a callback onto the next SwapBuffers frame (SDK 116+: same drop-safe cleanup guarantee) | any |

---

## Queries (`titan::queries::*`)

Chainable, filterable list views. Free factories returning value-type
query objects; finalize with `forEach`, `first`, `toVector`, `count`,
`exists`, etc.

| Factory | Returns | Highlights |
| --- | --- | --- |
| `titan::queries::npcs()` | `NpcQuery` | `id(n)`, `ids({...})`, `hasAction(a)` / `hasAction({a, b})`, `combatLevelAbove/Below/Between`, `exclude(npc)` / `exclude({...})` (SDK 101), `notTargetedByOtherPlayers()`, `interactingWith(Actor)`, `interactingWithLocal()`, `notInteracting()`, `isAnimating()`, `notAnimating()`, `animation(id)`, `overheadActive([icon])`, `overrideTransform(id)`, `sizeEquals(s)`, + LocatableQuery filters |
| `titan::queries::players()` | `PlayerQuery` | `excludingSelf()`, `interactingWith(Actor)`, `interactingWithLocal()`, `notInteracting()`, `isAnimating()`, `notAnimating()`, `animation(id)`, `isIdle()`, `isSkulled()`, `overheadActive([icon])`, `combatLevelAbove/Below/Between(n)`, + LocatableQuery |
| `titan::queries::objects(radius)` | `ObjectQuery` | `id(n)`, `ids({...})`, `hasAction(a)` / `hasAction({a, b})` (any match; SDK 99), `ofType(t)`, `layer(id)`, + LocatableQuery |
| `titan::queries::groundItems(radius)` | `GroundItemQuery` | `id(n)`, `ids({...})`, `minQuantity(n)`, `maxQuantity(n)`, `canLoot()`, + LocatableQuery |
| `titan::queries::inventory()` | `InventoryQuery` | `id(n)`, `ids({...})`, `slot(idx)`, `slotsAnyOf({...})`, `slotsBetween(lo, hi)`, `minQuantity(n)`, `maxQuantity(n)`, `hasAction(a)`, `isNoted()` (SDK 100), `excludeIds({...})`, `excludeNames({...})`, `totalQuantity()`, `exists()`, `count()`, `forEach`, `first`, `toVector` |
| `titan::queries::projectiles()` | `ProjectileQuery` | `spotAnim(id)`, `targetingEntity(decodedHash)`, `fromEntity(decodedHash)`, `targetingActor(actor)`, `fromActor(actor)`, `startedAfterTick(t)`, `endsBeforeTick(t)`, `activeDuring(t)`, + LocatableQuery |
| `titan::queries::graphicsObjects()` | `GraphicsObjectQuery` | `spotAnim(id)`, `onPlane(plane)`, `startedAfterTick(t)`, `startedBeforeTick(t)`, + LocatableQuery |
| `titan::queries::widgets([groupId])` | `WidgetQuery` | Loaded flat widgets plus recursive dynamic descendants. `packedId(id)`, `group(id)`, `child(componentId)`, `slot(index)`, `textContains(text)` / `textContains({a, b})` (any match; SDK 101), `textEquals(text)`, `isVisible()`, `isHidden()`, `type(id)`, `contentType(id)`, `itemId(id)`, `children()` (up to 2048 per parent since SDK 124; clipping sets the flag), `isTruncated()`, + generic terminals |

Interaction query filters (`interactingWith(...)`, `interactingWithLocal()`,
`notInteracting()`, `notTargetedByOtherPlayers()`) use active interaction
state, not raw cached target ids.

Shared base filters available on all queries: `where(pred)`, `when(cond, fn)`, `sortBy(cmp)`, `count()`, `any()`, `empty()`, `first()`, `forEach(fn)`, `toVector()` / `toArray()`.

Named entity queries additionally expose `nameContains(needle)`, `nameEquals(name)`, and `namesAnyOf(needles)`. Widget queries intentionally expose text-specific filters instead.

For widget queries, `packedId(id)` prefers flat-table matches over colliding
dynamic fallback ids. This keeps `.packedId(parent).children()` scoped to the
intended parent while preserving dynamic-only matches when no flat widget owns
the id.

Shared locatable filters (NPC, Player, Object, GroundItem, Projectile, GraphicsObject): `within(radius, origin)`, `within(worldArea)` (SDK 100), `nearestTo(origin)`, `nearest()`, `onTile(tile)`, `atWorldPoint(wp)`, `sortedByDistanceTo(origin)`.

### Entity wrappers (live handles, returned from queries)

Query result lists are membership snapshots, but the mutable objects inside
them are live handles. Holding a `Player`, `Npc`, `TileObject`, `GroundItem`,
`Widget`, inventory slot, or similar mutable client-backed handle across ticks
keeps the same identity while accessors read the current native state. Call
`snapshot()` when an explicit frozen copy is needed, and `exists()` to test
whether the underlying entity is still present. Actor `pathQueue()` entries
carry the same `worldViewId` as the actor handle.

`TileObject` and `GroundItem` became live cross-tick handles in **SDK 105**
(previously frozen snapshots), bringing C++/JS in line with the Java SDK, which
always behaved this way. A `TileObject` re-resolves against its tile matched on
`layer` + `id`; a `GroundItem` matched on item `id`. `interact()` re-resolves
first, so a despawned object is a safe no-op rather than an action into stale
scene memory. The correction kept the payload layout unchanged; Native ABI v1
now freezes that layout independently of SDK release numbers.

| Class | Stable methods |
| --- | --- |
| `Player` | `name`, `tile`, `worldPoint`, `localPoint`, `pathQueue`, `tileX/Y`, `plane`, `worldX/Y`, `preciseX/Y`, `orientation`, `animation`, `movementPose`, `idlePose`, `combatLevel`, `hashIndex`, `interactingIndex`, `interactingType`, `entityPtr`, `overheadIcon`, `skullIcon`, `isOverheadActive` / `isOverheadActive(HeadIcon)`, `isSkulled`, `isStationary`, `isAnimating`, `isIdle`, `isHidden`, `isInteracting`, `distanceTo`, `hasLineOfSight`, `isInMeleeDistance`, `interacting`, `composition` / `getPlayerComposition` |
| `Npc` | Everything on `Player` that applies (minus `isIdle` / `isSkulled`) + `id`, `sizeX/Y`, `toWorldArea`, `pathQueue`, `overheadIcon`, `hasHeadIconOverride`, `isOverheadActive` / `isOverheadActive(HeadIcon)`, `isAnimating`, `hasAction`, `interact(action)` |
| `TileObject` | `name`, `typeName`, `id`, `tileX/Y`, `plane`, `sizeX/Y`, `packedId`, `entityPtr`, `sceneTypecode`, `sceneObjectType` / `shape`, `orientation`, `animation` / `getAnimation`, `tile`, `worldPoint`, `localPoint`, `hasAction`, `hasLineOfSight`, `isInMeleeDistance`, `interact(action)` (live-resolved slot), `exists` (SDK 105), `snapshot()` (SDK 105) |
| `GroundItem` | `name`, `id`, `quantity`, `ownershipType`, `ownership`, `canLoot`, `tileX/Y`, `plane`, `tile`, `worldPoint`, `localPoint`, `distanceTo`, `hasLineOfSight`, `isInMeleeDistance`, `interact(action)`, `exists` (SDK 105), `snapshot()` (SDK 105) |
| `Item` | `name`, `id`, `quantity`, `slot`, `interact(action)` (ordinary action first, then live opcode-43 subOps), `useOn(Item\|Npc\|TileObject\|GroundItem)` (the `GroundItem` overload, SDK 129, queues the `WidgetTarget` -> `ItemUseOnGroundItem` selected pair through `executeSelectedActionPair` with the source item pinned; requires a live `slot`; JS recognises ground-item targets by shape ahead of locs; Java routes through `InteractionBackend.useInventoryItemOnGroundItem`) |
| `Projectile` | `spotAnimId`, `startTick`, `endTick`, `startX/Y`, `targetX/Y`, `height`, `sceneX/Y`, `sourceEntity`, `targetEntity`, `sourceEntityType`, `targetEntityType`, `rawSourceEntity`, `rawTargetEntity`, `sourceActor`, `targetActor`, `plane`, `basePtr`, `tile`, `worldPoint`, `localPoint`, `hasMoved`, `hasLineOfSight`, `isInMeleeDistance` |
| `GraphicsObject` | `spotAnimId`, `startCycle`, `plane`, `height`, `preciseX/Y`, `sceneX/Y`, `worldX/Y`, `basePtr`, `worldViewPtr`, `seqPtr`, `animationId`, `currentFrame`, `frameCycle`, `loopCount`, `totalCycle`, `animation`, `tile`, `worldPoint`, `localPoint`, `hasLineOfSight`, `isInMeleeDistance` |
| `Actor` | `isPlayer`, `isNpc`, `isEmpty`, `asPlayer`, `asNpc`, `name`, `tile`, `worldPoint`, `localPoint`, `pathQueue`, `interact` (forwards for NPCs), `isInteracting`, `isOverheadActive` / `isOverheadActive(HeadIcon)` |

Ground item ownership values are exposed as `titan::GroundItemOwnership`
and `titan.GroundItemOwnership`: `None`/`NONE` = 0,
`SelfPlayer`/`SELF_PLAYER` = 1, `OtherPlayer`/`OTHER_PLAYER` = 2,
`GroupIronman`/`GROUP_IRONMAN` = 3. Unknown or unreadable ownership is
reported as `0xFFFFFFFF`; `canLoot` treats that as lootable for normal
accounts and not lootable for iron accounts.

For `Player`, `Npc`, and variant `Actor`, `tile` / `worldPoint` represent the
logical server position (`PathQueue[0]` when available). `localPoint` represents
the render/interpolated `PreciseX/Y` position and may legitimately differ while
the actor is moving. The active movement target is the final element of
`pathQueue` when the queue is non-empty.

SDK v81 adds instance-coordinate conversion to world-position values and
locatable wrappers. C++ `WorldPoint::fromLocalInstance()` converts a current
local-instance point to its true source-world point, and
`WorldPoint::toLocalInstance()` converts a source-world point back into the
currently loaded instance; both return `std::optional<WorldPoint>`.
`Locatable<T>` exposes matching convenience methods through `worldPoint()`.
Java exposes the same as `WorldPoint.fromLocalInstance()`,
`WorldPoint.toLocalInstance()`, `Locatable.fromLocalInstance()`,
`Locatable.toLocalInstance()`, and the direct `Client.fromLocalInstance(point)`
/ `Client.toLocalInstance(point)` bridge methods returning
`Optional<WorldPoint>`. JS exposes
`titan.worldPoint.fromLocalInstance(point)`,
`titan.worldPoint.toLocalInstance(point)`, plus wrapper methods on locatable
objects.

SDK v85 makes locatable snapshots WorldView-aware. `Tile`, `WorldPoint`,
`LocalPoint`, `WorldArea`, players, NPCs, tile objects, ground items, and
graphics objects carry `worldViewId`; general entity getters enumerate active
WorldViews, and locatable queries expose current/top-level/specific WorldView
filters. Entity render and interaction helpers use the entity's WorldView id
where the host has enough native context to route the action.

---

## State (`titan::state::*`)

Subsystem facades. Each factory returns a value-type facade that wraps
the underlying host call. Read accessors are direct; mutations are
auto-marshalled onto the game thread where required.

| Factory | Returns | Highlights |
| --- | --- | --- |
| `titan::state::client()` | `ClientFacade` | `snapshot()`, `tick()`, `gameCycle()` (native 20 ms `Client.GameCycle`, distinct from `tick()`, SDK v125+), `plane()`, `playerCount()`, `sceneSizeX()` / `sceneSizeY()` (SDK v52+), `loggedIn()`, `localPlayer()`, `runEnergy()` (0-10000, SDK v45+), `weight()` (signed kg, SDK v45+), `accountType()`, `isIronman()` / `isIronMan()`, `isGroupIronman()` / `isGroupIronMan()` (SDK v50+), `currentWorldViewId()`, `currentWorldViewPtr()`, `worldViewPtr(id)`, `topLevelWorldViewPtr()`, top-level base/plane/scene plus projected local-player tile (SDK v85+), `isInInstance()`, `instanceTemplateChunks()` (SDK v81+), `getLocalDestinationLocation()` / `getWorldDestinationLocation()` (SDK v82+), `invokeMenuAction(opcode, id, p0, p1)` / `invokeMenuAction(MenuAction::Entry)` (SDK v37+) |
| `titan::state::camera()` | `CameraFacade` | `snapshot()`, `yaw()`, `pitch()`, `zoom()`, `posX()` / `posY()` / `posZ()` world-space camera position (SDK v39+; fields were always in `CameraState`), `interfaceScale()` -> `InterfaceScale{scaleX, scaleY, canvasOriginX, canvasOriginY, valid}` plus `interfaceScaleX()` / `interfaceScaleY()` convenience -- live UI-frame -> physical scale factor for aligning plugin-drawn pixel geometry at 150%/200% in-game interface scaling (SDK v109+; identity 1.0 when unavailable) |
| `titan::state::worldMap()` | `WorldMapFacade` | `snapshot()` returns coherent physical/logical viewports, the logical-to-physical interface transform, global centre, distinct current/target zoom, and validated logical pixels-per-tile scale. `worldToScreen` returns physical overlay pixels; `screenToWorld` inverse-transforms them before applying RLPL/RuneLite map math; scalar pixel/tile helpers use logical units (SDK 113). Returns unavailable atomically when any generated field, scale proof, native pointer/state, viewport, or transform is missing or inconsistent. |
| `titan::state::hider()` | `HiderFacade` | `setPlayers(v)`, `setNpcs(v)`, `setSelf(v)`, `setScene(v)`, `isPlayersHidden()`, `isNpcsHidden()`, `isSelfHidden()`, `isSceneHidden()` |
| `titan::state::audio()` | `AudioFacade` | `setPlaybackDisabled(v)`, `playbackDisabled()` -- global discrete sound-effect suppression. Added in SDK 69. |
| `titan::state::cache()` | `CacheFacade` | `item(id)`, `npc(id)`, `obj(id)`, `varbit(id)` -> `std::optional<Def>`. Varbit definitions come from Titan-owned JS5 cache data. SDK 106 `ItemDef::subOps` is the raw fixed 5-parent-by-20-submenu opcode-43 label matrix; empty strings preserve positional gaps. |
| `titan::state::vars()` | `VarsFacade` | `varbit(id)`, `varp(id)`, `varClientInt(id)`, `setVarClientInt(id,value)`, `varClientString(id)`, `setVarClientString(id,value)`, `varClientLong(id)`, `setVarClientLong(id,value)`. Varbit reads use Titan-owned JS5 definitions plus direct varp reads; they do not call native GET_VARBIT. VarClient reads are helper-backed optionals; writes queue onto the client tick. Optional long support fails closed when unavailable. Skill / prayer queries moved to `skills()` / `prayers()` in SDK v37. |
| `titan::state::skills()` | `SkillsFacade` | `boosted(int\|Skill)`, `real(int\|Skill)`, `experience(int\|Skill)` (SDK v37+). |
| `titan::state::prayers()` | `PrayersFacade` | `isActive(int\|Prayer)` (SDK v37+). |
| `titan::state::script()` | `ScriptFacade` | `run(id, args)`, `runAndGetInt(id, args)`, `questState(id)` |
| `titan::state::widgets()` | `WidgetsFacade` | Compatibility facade: `find(packedId)`, `children(parentPackedId)` (SDK v38+; preserves empty native slots; sized to the true child count via the SDK 124 sizing probe, up to 2,048 per call), `findByText(query)` (SDK v39+; primary-text substring match, case-sensitive), `setText(packedId, text)` (SDK v51+), `setText(parentPackedId, slot, text)` (SDK v63+), `WidgetsFacade::pack(group, child)`, `interact(opcode, identifier, param0, param1)`. `identifier` is the menu-entry identifier, not a widget packed id; `interact` returns action accepted / queued, not state already changed. Returned `Widget` handles expose accessor methods such as `packedId()`, `dynamicParentPackedId()`, `dynamicChildSlot()`, `rootPackedId()`, `dynamicPath()`, `spriteId()` (SDK v110+, primary native sprite id or `-1`), `modelType()` and `modelId()` (SDK v146+; the client's model source kind, matching RuneLite's `WidgetModelType` from 0 none to 6 NPC chathead by index, and the model, NPC or item id it selects, each `-1` when unavailable), `text()`, `exists()`, `snapshot()`, `setText(text)`, and `interact(opcode, identifier[, childSlot])`. Accessor reads resolve the retained live path through the host's internal widget resolver; `WidgetSnapshot` is the explicit frozen value returned by `snapshot()`. Operations re-resolve the retained path and fail closed for stale segments. Current offset bundles use native EASTL range assignment for widget text up to 256 UTF-8 bytes; older bundles retain the 22-byte inline fallback. |
| `titan::state::idle()` | `IdleFacade` | `remaining()` (ms), `reset()` |
| `titan::state::proxy()` | `ProxyFacade` | `list()` returns only stable proxy id/label; `setRoute(id)` selects a proxy (`""` means Direct); `status()` reports route generation/kind, sanitized failure stage/code, and whether the current generation's egress probe is ready. The measured egress address, endpoints, and credentials are never exposed. SDK 97. |
| `titan::state::login()` | `LoginFacade` | `snapshot()`, `state()` (including native `LoginGameState::Loading` = 25 in SDK v126+), `isLoggedIn()` (native `LoginGameState::LoggedIn`), `isWorldReady()` (local player / world view / scene ready), `setUsername/setPassword/...`, `setCharacter(...)`, `resetCharacter()`, and SDK 97 `submitLauncherCredentials()`. For a staged Jagex profile, the submit call holds Enter through a login-screen update and releases it on the next poll or cancellation. It is guarded to the exact login screen and Jagex launcher index and reports operation acceptance, not completed authentication. The later post-authentication `advanceClickToPlay()` action remains separate. |
| `titan::state::walk()` | `WalkFacade` | `toScene(sx, sy)`, `toWorld(wx, wy, plane)`, `to(Tile)`, `to(WorldPoint)` |
| `titan::state::itemContainer(id)` | `optional<ItemContainerSnapshot>` | Native `ClientInvCache` container read. Pass `titan::InventoryID::*` or a raw int. Returns nullopt when the cache has no matching entry or its analyzer-provided layout fails validation. Added in SDK 26; fixed ABI export capacity raised to 2,048 occupied entries in SDK 111. |
| `titan::state::itemDef(id)` | `optional<ItemComposition>` | Runtime ItemDef (varbit/varp transforms + inventory-action positional gaps preserved). SDK 107 adds the fixed 5-by-20 live opcode-43 `subOps` matrix. Falls back to the raw cache definition when `ITEM_DEF_LOOKUP` is unmapped; check `.runtimeResolved`. Mirrors RuneLite's `Client.getItemDefinition`. Added in SDK 26; action buffer widened in SDK 60. |
| `titan::state::collisions()` | `CollisionsFacade` | `flag(plane,x,y)` raw collision int, `isBlocked(plane,x,y,dx,dy)` one-tile step predicate (SDK v39+), `cachedRegion(regionId)` and `currentScene([expectedWidth, expectedHeight])` immutable bulk snapshots (SDK 112); supplying dimensions from `ClientSnapshot` enables one fixed-ABI copy pass. |
| `titan::webWalker()` / `titan::state::webWalker()` | `WebWalkerFacade` | Submit a read-only route request, poll its summary, copy typed walk/transport/teleport steps, cancel, and release. Costs are integer points in C++, JS, Java, and fixed ABI/provider records: legacy authored cost `1` is `10` points and one walked tile is `5` points (the requested 0.5 weighting). Defaults enable transports, normal/equipped-item teleports, POH routes, and wilderness avoidance; minigames and charters default off (SDK 112). |
| `titan::screenshot()` / `titan::state::screenshot()` | `ScreenshotFacade` | Queue one capture of the next presented frame, poll for `Ready` / `Failed`, copy the PNG, release. Same pixels and encoder as the controller's `/tabs` screenshot: the full backbuffer after the AboveWidgets overlay pass. At most 4 handles outstanding; a request that never sees a presented frame fails after ~5 s. Callable from any plugin callback thread. Mirrored as `titan.screenshot` in JS/TS and `Titan.screenshot()` in Java (SDK 131). |

The SDK 106 item cache submenu shape is identical across runtimes:
`titan::ItemDef::subOps` in C++, `titan.ItemDef.subOps: string[][]` in
JavaScript/TypeScript, and `ItemDefinition.subOps(): List<List<String>>` in
Java 0.1.32. All expose exactly five parent slots and twenty submenu slots per
parent; the Java lists are deeply unmodifiable.

SDK 107 exposes the same shape on the runtime definition:
`titan::ItemComposition::subOps`, `titan.ItemComposition.subOps`, and
`ItemComposition.subOps()` in Java 0.1.33. When `runtimeResolved` is true these
labels come from the live analyzer-described ItemDef vectors; cache fallback
uses the raw opcode-43 matrix. Empty slots remain positional in every runtime.

### WorldView and instance conversion (SDK v81)

| Surface | Symbols |
| --- | --- |
| C++ | `ClientFacade::currentWorldViewPtr()`, `worldViewPtr(id)`, `topLevelWorldViewPtr()`, `isInInstance()`, `instanceTemplateChunks()`; `WorldPoint::fromLocalInstance()`, `WorldPoint::toLocalInstance()`; `Locatable<T>::fromLocalInstance()`, `Locatable<T>::toLocalInstance()` |
| JS / TS | `titan.worldView.current()`, `titan.worldView.get(id)`, `titan.worldView.topLevel()`; `titan.state.client.isInInstance()`, `titan.state.client.getInstanceTemplateChunks()`; `titan.worldPoint.fromLocalInstance(point)`, `titan.worldPoint.toLocalInstance(point)`; locatable wrapper methods `fromLocalInstance()` / `toLocalInstance()` |
| Java | `Client.currentWorldViewId()`, `currentWorldViewPtr()`, `worldViewPtr(id)`, `topLevelWorldViewPtr()`, top-level base/plane/scene/projected-local-player accessors, `isInInstance()`, `instanceTemplateChunks()`, `fromLocalInstance(point)`, `toLocalInstance(point)`; `WorldPoint.fromLocalInstance()`, `WorldPoint.toLocalInstance()`; `Locatable.fromLocalInstance()`, `Locatable.toLocalInstance()` |

Instance template chunks are exposed as a fixed `4 x 13 x 13` table. Native ABI
storage is flattened plane-major, and invalid cells are `-1` because packed
chunk value `0` can represent a valid source chunk.

### Client destination location (SDK v82)

| Surface | Symbols |
| --- | --- |
| C++ | `ClientFacade::getLocalDestinationLocation() -> optional<LocalPoint>`, `ClientFacade::getWorldDestinationLocation() -> optional<WorldPoint>` |
| JS / TS | `titan.state.client.getLocalDestinationLocation() -> LocalPoint \| null`, `titan.state.client.getWorldDestinationLocation() -> WorldPoint \| null` |
| Java | `Client.getLocalDestinationLocation() -> Optional<LocalPoint>`, `Client.getWorldDestinationLocation() -> Optional<WorldPoint>` |

The local destination is the minimap red-flag target in scene-local precise
coordinates. It returns empty/null when the minimap state is unavailable or
the destination flag is inactive.

### `titan::state::world::*` (world / world-hop)

| Function | Returns | Highlights |
| --- | --- | --- |
| `titan::state::world::current()` | `optional<int32_t>` | Live current-world id from the game singleton. `nullopt` when `CURRENT_WORLD_OFFSET` is unmapped on this revision. Added in SDK 28. |
| `titan::state::world::list()` | `vector<World>` | Full snapshot of the native `GameWorld::m_list`. Empty when list globals / field offsets aren't mapped (level-3 unavailable). Added in SDK 28. |
| `titan::state::world::metadata()` | `vector<WorldMetadata>` | Jagex SLR-backed world metadata: id, flags, host, activity, location, region code, population, and measured ping cache. Added in SDK 83. |
| `titan::state::world::refreshMetadata()` | `bool` | Queues an asynchronous SLR refresh and background ping probes. Added in SDK 83. |
| `titan::state::world::hop(id)` | `bool` | **Title-screen** hop: native `changeWorld` call for the list entry matching `id`. Use only when not logged in; see `hopIngame` for the logged-in path. Auto-marshalled onto the game thread (MainLoop phase). Requires level-3 availability. Added in SDK 28. |
| `titan::state::world::hopByListIndex(idx)` | `bool` | Lower-tier title-screen hop: by position in `m_list`. Works with level-2 availability (no need for GameWorld field offsets). Added in SDK 28. |
| `titan::state::world::hopIngame(id)` | `bool` | **In-game** hop: drives the native 3x `CC_OP` footer-click sequence (opens logout tab, opens switcher, selects world, confirms). Returns "accepted?" -- the real transition completes asynchronously across several ClientTicks. Use when `titan::state::login().isLoggedIn()` is true. Auto-marshalled onto the game thread (ClientTick phase). Added in SDK 31. |

JS / TS expose the SLR metadata as `titan.state.world.metadata()` and
`titan.state.world.refreshMetadata()`. Java exposes the same surface as
`Client.worldMetadata()` and `Client.refreshWorldMetadata()`.

### Other facade-shaped roots

These are not strictly state subsystems but are factories returning value
facades; they live at the top level of `namespace titan`:

| Factory | Returns | Highlights |
| --- | --- | --- |
| `titan::overlay()` | `OverlayDraw` | `tileQuad`, `tileRegion`, WorldView-aware tile/text/projection/height helpers, `entityBox`, `entityClickbox`, `entityHull`, `tileObjectClickbox`, `tileObjectHull`, `textAtWorld`, `screenText`, `screenRect`, `screenLine`, `worldToScreen`, `tileToScreen`, `tileHeight` |
| `titan::plugins()` | `PluginsFacade` | `all()`, `get(id)`, `find(id)`, `self()` returning `PluginHandle` |
| `titan::service<T>(id)` | `ServiceRef<T>` | Leased lookup using the current plugin callback context. Keep the move-only service reference for one operation; its destruction releases the lease. Publication always requires an explicit owner. |
| `titan::registerService(owner, id, ptr)` / `titan::service<T>(owner, id)` | `bool` / `ServiceRef<T>` | Explicit-owner publication and leased lookup. Publication is tied to the native instance and load generation, survives ordinary UI disable, and is withdrawn on unload. `nullptr` unregisters that owner's entry. |
| `titan::BreakHandler` | Static instance-based utility | `<titan/break_handler.h>`: `registerPlugin(plugin, configurable)`, `start`, `stop`, `unregister`, command polling, pause/defer/error/running reports, and coordinator-only registration snapshots/publish/clear (SDK 97); registration-free `observe` / `isBreakInProgress` (SDK 144). |

> **Colour convention (SDK 67+):** every colour argument across the whole plugin
> API — overlay draws, panel styling, side-panel elements, and colour settings —
> is plain **ARGB** (`0xAARRGGBB`), in C++, JS, and Java alike. The host performs
> the single ARGB→ImGui-ABGR red/blue swap internally at the draw/panel root, so
> plugins never swap channels themselves. (Plugins written before SDK 67 that
> pre-swapped to ABGR must now author plain ARGB.)

### Break Handler participation (SDK v97)

Break Handler uses the owning plugin instance on every call. This lets helpers
in a multi-class plugin retain and forward the owner while the host still
rejects foreign, stale, disabled, or unloaded instances. The host copies the
stable plugin id and load generation during each call; it never retains a C++
pointer, Java object, JavaScript value, or cross-DLL callback.
Native registration and report requests cross the DLL boundary as
size/version-tagged fixed-capacity POD records. IDs, display labels, and report
reasons are copied into those records; the host validates their version,
enum values, and in-buffer terminators before consulting the registry. Report
epoch zero means the exact epoch already recorded by that registration's last
`poll`; a non-zero raw ABI epoch must match that observation. Java and
JavaScript validate their owner object and then enter the same canonical host
registry without exposing those native records as public language objects.

| Runtime | Utility and command types |
| --- | --- |
| C++ | `titan::BreakHandler`; `titan::BreakCommand`, `BreakPhase`, `BreakMode` from `<titan/break_handler.h>` |
| Java 0.1.24+ | `net.titan.api.BreakHandler`; `BreakCommand`, `BreakPhase`, `BreakMode` |
| JavaScript / TypeScript | `titan.breakHandler`; `titan.BreakCommand`, `titan.BreakPhase`, `titan.BreakMode` |

Java and JavaScript expose `register(owner, configurable = true)`,
`start(owner)`, `stop(owner)`, `unregister(owner)`, `poll(owner)`,
`shouldBreak(owner)`, `isBreakActive(owner)`, `shouldResume(owner)`,
`paused(owner)`, `defer(owner, retryAfterMs, reason)`,
`error(owner, code, reason)`, `running(owner)`, and (SDK 144)
`observe(owner)` / `isBreakInProgress(owner)`. C++ uses the otherwise
identical `registerPlugin` / `unregister` spellings because
`register` is a reserved C++ keyword. `unregisterPlugin` remains as a native
compatibility alias for the initial SDK-97 preview.
The C++ facade also exposes the optional `preparing(owner)` status report used
to make preparation progress visible in coordinator snapshots.

- `configurable = true` registers a schedule owner. `false` registers a
  participant that joins global pause/resume quorums but has no schedule of
  its own.
- `start` makes the registration active. `stop` keeps it registered and
  visible; `unregister` implies stop and removes
  the live registration.
- Poll before reporting. `poll` and the three convenience phase queries record
  the current epoch; pause/defer/error/running reports for stale epochs fail
  closed.
- On `PREPARE`, stop issuing new work and call `paused` only at a safe
  boundary. `defer` is a retry hint and does not extend the coordinator's
  deadline. Remain paused through `BREAK_ACTIVE`. On `RESUME`, wait until the
  client is world-ready, restart safely, and call `running`.
- Register/start in `onEnable`, and stop/unregister in `onDisable`. The host
  also removes registrations during disable, fault, reload, and unload, so
  cleanup calls are intentionally idempotent.
- `observe` (SDK 144) needs no registration: any loaded, enabled plugin reads
  the command the coordinator has currently published, and
  `isBreakInProgress` is true for every phase but `NONE`. It records nothing,
  so it never joins a pause quorum, and a participant still polls before
  reporting. Use it to stay out of the way of breaks -- for example, not
  hopping worlds while one is in progress -- without being paused by them.

The native `registrations`, `publish`, and `clear` methods are coordinator
operations accepted only from the loaded `break_handler` plugin; gameplay
plugins should use only the common participant surface above.

Load-lifetime services in SDK 97 code should use
`titan::registerService(owner, serviceId, pointer)`. The host attributes these
entries to the owner's exact load generation and removes them automatically.
The ownerless SDK 66-96 overload remains only for binary compatibility and is
cleared at final host shutdown; current in-tree publishers do not use it.

### Account Profiles service (native SDK v97)

`<titan/account_profiles.h>` declares the versioned, POD-only
`titan.account_profiles.v1` service. Resolve it with
`titan::account_profiles::service()`. It is published for the native
`account_profiles` plugin's complete load lifetime, so dependants can use it
when the Profiles UI plugin is disabled. A null result means the dependency
service is unavailable or has an incompatible table size/version.

| Operation | Contract |
| --- | --- |
| `getStatus` | Returns `Missing`, `Locked`, `Ready`, `Busy`, `Corrupt`, `UnknownVersion`, or `IoError`, plus vault revision/count and stable active/staged profile ids. |
| `unlockExisting` | Unlocks an existing vault only. It never creates a missing vault and returns no credential material. The caller owns and must promptly clear its password buffer. |
| `getSnapshot` | Copies immutable rows containing stable profile id, label, Standard/Jagex kind, opaque proxy id/label, and readiness flags. |
| `beginActivation` / `pollActivation` | Starts and observes route selection plus credential staging using a caller-held opaque token. Poll until `CredentialsApplied` or a terminal phase. |
| `submitActivation` | After `CredentialsApplied`, submits the appropriate Standard or Jagex login flow; continue polling for logged-in/world-ready success or a sanitized failure. |
| `cancelActivation` | Cancels a pending, credentials-applied, or submitted operation. |

Each service function's byte return value reports ABI/request validity, not
account-login success. Inspect the returned `Status` or `ActivationSnapshot`
for the domain state and result.

Terminal activation results distinguish success, locked/changed/missing
Profiles data, missing/failed proxy, rejected or expired credentials, 2FA,
required user action, unsupported login, timeout, and cancellation. The
service never returns usernames, passwords, OAuth/session tokens, proxy
endpoints, or proxy credentials. Consumers persist stable profile/proxy ids,
not snapshot indexes or labels.

The lower-level native `titan::state::proxy()` and
`LoginFacade::submitLauncherCredentials()` surfaces exist for infrastructure
integrations. The latter name and ABI are retained, but its host implementation
uses the keyboard Enter route rather than invoking the analyzer-discovered
launcher-processing function. Prefer the Profiles activation service for
account switching so Profiles retains secret ownership and coordinates the
assigned route.

### Type catalogs

All catalog types live in `namespace titan` under `<titan/*.h>`. Plugins
include the specific header needed; the umbrella `<titan/titan.h>`
pulls in the lot. Client-internal code can also reach them via the
top-level shims in `client/game/types/*.h` (which alias into
`namespace titan`) to preserve the familiar unqualified spelling.

| Type | Header | Summary |
| --- | --- | --- |
| `titan::Prayer` + `titan::PrayerInfo` | `<titan/prayer.h>` | 55-prayer enum (standard + Ruinous Powers) with `varbitId(p)` / `name(p)` helpers. Use with `titan::state::prayers().isActive(Prayer)` or `titan::utils::Prayers`. |
| `titan::Skill` + `titan::SkillInfo` | `<titan/skill.h>` | 25-skill enum with `name(s)` helper and `MAX_SKILLS` constant. Use with `titan::state::skills().boosted(Skill)` / `.real(Skill)` / `.experience(Skill)`. |
| `titan::MenuAction::Id` + helpers | `<titan/menu_action.h>` | DoAction opcode enum + `normalize`, `isWidgetCcFamily`, `isCcOpFamily`, `nameFor`. Includes `MenuAction::Entry` for fully-specified `state::client().invokeMenuAction(Entry)` dispatch. Menu entries expose `identifier`. |
| `titan::Varbits::*` | `<titan/varbits.h>` | ~200 named varbit-id `constexpr int` constants + inline `nameOf(id)`. Use with `titan::state::vars().varbit(Varbits::FOO)`. |
| `titan::VarPlayerID::*` | `<titan/var_player.h>` | Varp-id constants for common player state (poison, special-attack toggle, auto-retaliate, ...) plus inline `nameOf(id)`. Note: `RUN_ENERGY` (173) is a deprecated alias -- varp 173 is the run toggle in the native client. Use `titan::state::client().runEnergy()` for the actual 0-10000 energy value (SDK v45+). |
| `titan::VarClientInt::*` | `<titan/var_client_int.h>` | RuneLite-compatible client-side integer variable ids plus inline `nameOf(id)`. Mirrored to JS as `titan.VarClientInt.*`. Added in SDK 61; annotation helper added in SDK 62. |
| `titan::VarClientStr::*` | `<titan/var_client_str.h>` | RuneLite-compatible client-side string variable ids plus inline `nameOf(id)`. Mirrored to JS as `titan.VarClientStr.*`. Added in SDK 61; annotation helper added in SDK 62. |
| `titan::gamevals::*` | `<titan/gamevals.h>` | Native cache-backed gameval catalogs with RuneLite-style class names. Examples: `titan::gamevals::ItemID::ABYSSAL_WHIP`, `titan::gamevals::ObjectID::BANKBOOTH`, `titan::gamevals::NpcID::GOBLIN`, `titan::gamevals::QuestID::QUEST_ANIMALMAGNETISM`. Each class exposes `sourceCatalog`, `entries()`, and `byId(id)`; `QuestID` exposes `byQuestId(id)` and `byRowId(rowId)`. Mirrored to TS as `titan.gamevals.*` and Java as `net.titan.gamevals.*`. Added in SDK 87. |
| `titan::HeadIcon` + `titan::HeadIconInfo` | `<titan/head_icon.h>` | Overhead prayer/curse icon enum with `name`, `shortName`, `isPrayer`, `isCurse`, `color`, `isValid`, `fromRaw` helpers (SDK v35). |
| `titan::LocalPoint` | `<titan/local_point.h>` | Plane-less sub-tile precise-coord point. Euclidean `distanceTo`, `sceneX/Y`, `fromScene`, `isInScene`. |
| `titan::WorldPos` / `titan::WorldPoint` | `<titan/world_point.h>` | Absolute world tile (`x` / `y` / `z` = plane). `WorldPoint` is a typedef alias for the RuneLite-style name. Chebyshev `distanceTo` / `distanceTo2D`, `regionId`, `regionX`, `regionY`, `dx` / `dy` / `dz`, `hasLineOfSight(other)` (SDK v52+), `isInMeleeDistance(...)` (SDK v86+), `fromLocalInstance()` / `toLocalInstance()` (SDK v81+). |
| `titan::WorldArea` | `<titan/world_area.h>` | Axis-aligned rectangle in world-tile space. `contains(WorldPos)`, `contains2D`, Chebyshev `distanceTo(WorldPos)` / `distanceTo(WorldArea)`, `center()`, `hasLineOfSight(...)` (SDK v52+), `isInMeleeDistance(...)` (SDK v86+). |
| `titan::Tile` | `<titan/world_point.h>` | Scene-local tile (`x`, `y`, `plane`). Chebyshev `distanceTo` / `distanceTo2D`, `isInScene`. |
| `titan::EquipmentSlot` + `titan::EquipmentSlotInfo` | `<titan/equipment_slot.h>` | 14-slot enum (HEAD..AMMO; RuneLite-aligned ordinals) with `name(slot)`, `slotWidgetPackedId(slot)` (returns `0` for ARMS/HAIR/JAW), `isValid(ordinal)`, `fromOrdinal(ordinal)` helpers. Use with `titan::utils::Equipment::*`. Added in SDK 40. |

---

## Utils (`titan::utils::*`)

Header-only inline wrappers under `namespace titan::utils` that compose
existing facades into ergonomic one-call APIs for common UI / inventory
flows. **No new ABI surface.** Mirror of the client-internal
`Dialogue::*` / `Combat::*` / `Equipment::*` / `Inventory::*` helpers
under `client/actions/`.

JS plugins reach the same primitives via `titan.utils.dialogue.*` /
`titan.utils.combat.*` / `titan.utils.prayers.*` /
`titan.utils.equipment.*` / `titan.utils.inventory.*`
-- see [shared/titan-plugin-sdk.d.ts](../titan-plugin-sdk.d.ts). Each TS
method is a thin forward to the matching C++ helper.

### `titan::utils::Inventory` (SDK 41+)

Header-only inline helpers in `<titan/utils/inventory.h>`. Composed over
`titan::queries::inventory()` (item data) and
`titan::state::widgets().find((149<<16)|0)` (the inventory tab parent
widget, used for the visibility check).

| Function | Summary |
| --- | --- |
| `Inventory::isOpen()` | True when the inventory parent widget is visible (i.e. the inventory UI is currently displayed). |
| `Inventory::size()` | Number of occupied inventory slots (`queries::inventory().count()`). |
| `Inventory::emptySlots()` | `28 - size()`, clamped to `[0, 28]`. |
| `Inventory::isFull()` | True when `size() >= 28`. |
| `Inventory::isEmpty()` | True when `size() == 0`. |
| `Inventory::getAll()` | Snapshot of every occupied slot (`InventoryItem` value list); placeholder id `6512` is excluded. |
| `Inventory::find(id\|name)` | First matching item by id (exact) or name (case-insensitive substring). `std::nullopt` on miss. |
| `Inventory::getSlot(slot)` | Item at the given slot index, or `std::nullopt` when empty / out of range. |
| `Inventory::getByIds({ids})` / `getByNames({names})` | Filter by id list / name needles. |
| `Inventory::contains(id)` / `contains(id, qty)` / `contains(name)` | Has-item predicates; `(id, minQuantity)` requires the combined quantity across matching slots to reach `minQuantity` (useful for stackables). |
| `Inventory::count(id)` / `count({ids})` | Combined quantity across matching slots. |
| `Inventory::containsAny / containsAll({ids\|names})` | Aggregate predicates over id or name lists. |
| `Inventory::drop(id\|name)` | Locate the first matching slot and dispatch its `Drop` menu action. Returns false when no match or no `Drop` option. |

### `titan::utils::Dialogue`

Header-only inline helpers in `<titan/utils/dialogue.h>`. Composed over
`titan::state::widgets()`.

| Function | Summary |
| --- | --- |
| `Dialogue::continueMake()` | Click the "Make" button (270, 14) when the make-X interface is open. Returns true when queued. |
| `Dialogue::continueDialogue()` | Press Space on the active "click here to continue" prompt (level-up, NPC dialog, minigame, item box, tutorial-island, ...). |
| `Dialogue::inDialogue()` | True when a continue prompt or multi-option dialog is visible. |
| `Dialogue::isQuestCompletionOpen()` / `closeQuestCompletion()` | Open-state predicate + close-button click for the quest-completion scroll. |
| `Dialogue::getContinueWidgetPackedId()` | Packed id of the current continue widget, or 0. Walks gameval-backed continue widgets and applies the "Click here to continue" text gate to the tutorial-island prompts and to Objectbox's prompt. That prompt is dynamic child 2 of `Objectbox::UNIVERSE`, which is returned for it; it is not clickable as a whole widget, so use `continueDialogue()`. |
| `Dialogue::hasOption({needles})` (SDK v38+) | True when the dialog-options widget (219, 1) is visible and at least one option text contains any needle (case-insensitive). Slot 0, the dialog title, is not an option. |
| `Dialogue::selectOption({needles})` (SDK v38+) | Press the digit key of the first option whose text matches a needle: option N is dynamic-child slot N, key N. Slot 0, the dialog title, is never picked, and within one needle an exact match beats an earlier substring match. |
| `Dialogue::handleDialogue({needles})` (SDK v38+) | `selectOption({needles})` first; falls back to `continueDialogue()`. Mirrors RuneLite `handleDialogue(String...)`. |

### `titan::utils::Combat`

Header-only inline helpers in `<titan/utils/combat.h>`. Composed over
`titan::state::widgets()` / `titan::state::client()` / `titan::state::vars()`.

| Function | Summary |
| --- | --- |
| `Combat::enableSpecialAttack(skipMovement=false)` | Click the special-attack orb through the normal synthetic click path. `skipMovement` is retained for compatibility and does not suppress clicks. |
| `Combat::getSpecialAttackPercentage()` | Spec energy percentage (0..100). Reads `VarPlayerID::SPECIAL_ATTACK / 10`. |
| `Combat::isSpecialAttackEnabled()` | True when the spec toggle is armed (`VarPlayerID::SPECIAL_ATTACK_ENABLED == 1`). |
| `Combat::isAutoRetaliateEnabled()` | True when auto-retaliate is on. Note: underlying varp 172 is inverted (0 == on). |
| `Combat::setAutoRetaliate(enabled)` | Idempotent toggle. Returns true (no-op) when already in the requested state. |

### `titan::utils::Prayers` (SDK 102+)

Header-only inline helpers in `<titan/utils/prayers.h>`. Each prayer maps
explicitly to its generated `InterfaceID::Prayerbook::PRAYERn` component.
Actions require the mapped widget to exist but deliberately do not validate
the selected prayerbook, unlock state, widget name, or visibility.
Quick-prayer orb toggling and quick-prayer configuration are not included.
Java mirrors the surface as `net.titan.api.utils.Prayers`; JavaScript and
TypeScript expose it as `titan.utils.prayers`.

| Function | Summary |
| --- | --- |
| `Prayers::isActive(prayer)` | Read the requested prayer's active varbit. Invalid values return false. |
| `Prayers::toggle(prayer)` | Always dispatch `CC_OP` identifier 1 / param0 -1 to the mapped widget. Returns false when the prayer is invalid, the widget is absent, or dispatch is rejected. |
| `Prayers::setActive(prayer, active)` | Idempotent state setter; returns true without clicking when the requested prayer varbit already matches. |
| `Prayers::enable(prayer)` / `disable(prayer)` | Convenience wrappers for `setActive(prayer, true/false)`. |

### `titan::utils::Magic` (SDK 49+)

Header-only spellbook metadata helpers in `<titan/utils/magic.h>`. The
spell catalogs mirror TP_RL names and levels; widget ids mirror RuneLite
`InterfaceID.MagicSpellbook`. Bare spell selection/casts use the widget
interaction backend for the spell widget's `WIDGET_TARGET` action.

| Function / type | Summary |
| --- | --- |
| `SpellBook`, `Standard`, `Ancient`, `Lunar`, `Necromancy` | Typed spellbook and spell enums. |
| `SpellInfo`, `info(spell)` | Name, level, widget, book, members flag, and menu-entry id metadata. |
| `currentSpellBook()`, `isAutoCasting()` | Live varbit/varp state reads. |
| `lastHomeTeleportUsage()`, `isHomeTeleportOnCooldown()` | Home-teleport timing from varp 892. |
| `canCast(spell)` | Current-book and Magic-level check, plus TP_RL's active Standard Ardy/Trollheim gates. |
| `select(spell)`, `cast(spell)` | Dispatch widget interact `WIDGET_TARGET` with `identifier=0`, `param0=-1`, `param1=<spell packed widget id>`. `select` delegates to `cast`. |
| `cast(spell, actionIndex)`, `cast(spell, actionIndex, opcode)` | Dispatch widget interact with `identifier=1+actionIndex`; the two-arg overload uses `CC_OP`. |
| `castOn` | Selects the spell, then schedules the target action on the next client tick for NPC, player, inventory item, tile object, ground item, and widget targets. Entity/world target entries resolve a target click point through the native clickbox/projected-footprint path before dispatch; inventory/widget targets use widget interact. Target action text is `Cast`; target text is blank for now. |

JS/TS mirrors live at `titan.utils.magic` with spell catalog objects and
the same bare, indexed, and targeted spell dispatch behavior.

### `titan::utils::Mouse` (SDK 94+)

Header-only inline helpers in `<titan/utils/mouse.h>`.

| Function | Summary |
| --- | --- |
| `Mouse::resolveActionClickPoint(entry)` | Resolve the screen point Titan would use for a menu action, including entity clickboxes, scene-object footprints, ground items, and supplied target metadata. Returns empty when the target is off-screen or unresolvable. |

Java mirrors live at `net.titan.api.utils.Mouse.resolveActionClickPoint(...)`.
JS/TS mirrors live at `titan.utils.mouse.resolveActionClickPoint(action)`.

### `titan::utils::Equipment` (SDK 40+)

Header-only inline helpers in `<titan/utils/equipment.h>`. Composed over
`titan::state::itemContainer()` / `titan::state::itemDef()` /
`titan::state::widgets()`.

| Function | Summary |
| --- | --- |
| `Equipment::getAll()` | Snapshot every occupied equipment slot. Empty when EQUIPMENT (id 94) isn't populated. |
| `Equipment::find(slot\|id\|name)` | First equipped item matching the lookup key (`EquipmentSlot`, item id, or case-insensitive name substring). |
| `Equipment::getByIds({ids})` / `getByNames({names})` | Filter the equipped set by id list or name needles. |
| `Equipment::contains(id)` / `contains(id, qty)` / `contains(name)` | Is-equipped predicates; the `(id, minQuantity)` overload requires the slot to hold at least `minQuantity` charges (useful for ammo). |
| `Equipment::count(id)` / `count({ids})` | Combined quantity across matching slots. |
| `Equipment::containsAny / containsAll({ids\|names})` | Aggregate predicates over id or name lists. |
| `Equipment::unequip(slot\|id\|name)` | Fire `Remove` against the worn-items slot widget for the requested item. Returns true when accepted / queued; observe `onItemContainerChanged` for confirmation. No-op when the slot is empty or has no clickable widget (ARMS / HAIR / JAW). |

SDK v64 exposes the full loaded-widget walk through
`titan::queries::widgets()`. Text searches can use
`titan::queries::widgets().textContains(...)`; callers that know the
parent can narrow directly with `.packedId(parent).children()`. Query
snapshots retain dynamic slot paths, so a matching result can be chained
straight into `.interact(...)` or `.setText(...)`.
Use `.slot(index)` to traverse directly to an exact native dynamic-child slot;
`.child(componentId)` filters the low 16 bits of a packed widget id.

### `titan::utils::Bank` (SDK 44+)

Header-only inline helpers in `<titan/utils/bank.h>`. Composed over
`titan::state::widgets()` / `titan::state::vars()` /
`titan::state::itemContainer()` / `titan::queries::objects()` /
`titan::keyboard::typeString()`.

| C++ | TS (`titan.utils.bank`) | Summary |
| --- | --- | --- |
| `Bank::isOpen()` | `.isOpen` | Bank interface visible |
| `Bank::isGeOpen()` | `.isGeOpen` | GE inventory overlay visible |
| `Bank::isSearchOpen()` | `.isSearchOpen` | Chatbox modal input line visible (Withdraw-X / bank search prompt) |
| `Bank::isNotedMode()` | `.isNotedMode` | Noted withdrawal mode active |
| `Bank::getBankTab()` | `.bankTab` | Current bank tab index |
| `Bank::isMainTabOpen()` | `.isMainTabOpen` | Tab 0 (all items) selected |
| `Bank::contains(id\|name)` | `.contains(id\|name, minQty?)` | Item in bank |
| `Bank::count(id\|name)` | `.count(id\|name)` | Total quantity in bank |
| `Bank::find(id\|name)` | `.find(id\|name)` | First matching slot |
| `Bank::close()` | `.close()` | Close bank interface |
| `Bank::setNotedMode(bool)` | `.setNotedMode(bool)` | Toggle noted mode |
| `Bank::depositAll()` | `.depositAll()` | Deposit entire inventory |
| `Bank::depositEquipment()` | `.depositEquipment()` | Deposit worn equipment |
| `Bank::depositAllOfSlot(slot)` | `.depositAllOfSlot(slot)` | Deposit all of item at inventory slot |
| `Bank::depositOneOfSlot(slot)` | `.depositOneOfSlot(slot)` | Deposit one of item at inventory slot |
| `Bank::depositAllOfItem(itemId)` | `.depositAllOfItem(itemId)` | Deposit all of item by item ID |
| `Bank::depositOneOfItem(itemId)` | `.depositOneOfItem(itemId)` | Deposit one of item by item ID |
| `Bank::depositAllExcept(ids)` | `.depositAllExcept(ids)` | Deposit everything except listed ids |
| `Bank::withdrawItem(id)` | `.withdrawItem(id)` | Withdraw one of item |
| `Bank::withdrawAllItem(id)` | `.withdrawAllItem(id)` | Withdraw all of item |
| `Bank::withdrawItemAmount(id, n)` | `.withdrawItemAmount(id, n)` | Withdraw specific amount |
| `Bank::open()` | `.open()` | Open nearest bank/chest/NPC |
| `Bank::isNearBank(dist)` | `.isNearBank(dist?)` | Bank object within distance |
| `Bank::isPinVisible()` | `.isPinVisible` | PIN prompt visible |
| `Bank::pinRequestedDigitIndex()` | `.pinRequestedDigitIndex` | PIN digit 0-3 or -1 |
| `Bank::typePin(pin)` | -- | Type one PIN digit (C++ only) |
| `Bank::LoadoutRunner` | -- | Stateful loadout automation (C++ only) |

### `titan::utils::DepositBox` (SDK 133+)

Header-only inline helpers in `<titan/utils/deposit_box.h>` for the shared
bank deposit box interface (`InterfaceID::BANK_DEPOSITBOX`). Composed over
`titan::state::widgets()` / `titan::state::vars()`. Every action is the
interface's own component operation (CC_OP), which is what a real click
sends on both menu conventions; `close()` targets the close X on dynamic
child 11 of `BankDepositbox::FRAME`. Raw `WidgetClose` (26) is the native
root-owned closure on callback clients and does not close this interface.

| C++ | TS (`titan.utils.depositBox`) | Java (`utils.DepositBox`) | Summary |
| --- | --- | --- | --- |
| `DepositBox::isOpen()` | `.isOpen` | `isOpen()` | Deposit box interface visible |
| `DepositBox::isDepositAllSelected()` | `.isDepositAllSelected` | `isDepositAllSelected()` | Deposit quantity mode is "All" |
| `DepositBox::close()` | `.close()` | `close()` | Close via the frame's close X |
| `DepositBox::depositInventory()` | `.depositInventory()` | `depositInventory()` | Deposit entire inventory |
| `DepositBox::depositWorn()` | `.depositWorn()` | `depositWorn()` | Deposit worn equipment |
| `DepositBox::depositLootingBag()` | `.depositLootingBag()` | `depositLootingBag()` | Deposit looting bag contents |
| `DepositBox::selectDepositAll()` | `.selectDepositAll()` | `selectDepositAll()` | Select the "All" quantity (idempotent) |

### `titan::keyboard` (SDK 44+)

Inline facade in `<titan/keyboard.h>`. Routes through `IBackend` keyboard
virtuals.

| Function | Summary |
| --- | --- |
| `keyboard::sendString(utf8)` | Inject a string into the game's keyboard producer |
| `keyboard::sendKey(key, mods)` | Press+release a named key with modifiers |
| `keyboard::typeString(utf8, min, max)` | Type with randomized inter-character delays |
| `keyboard::cancelTypeString()` | Cancel an in-progress type operation |
| `keyboard::isTyping()` | True while a type operation is active |

---

## Events (`PluginApi` callbacks)

Override these virtuals on your `titan::Plugin` subclass. All are optional.
The first column is the event wrapper struct (in `<titan/events.h>`) passed
to the virtual; the second column is the callback signature on the base
class.

| Event | Virtual |
| --- | --- |
| -- | `void onEnable()` |
| -- | `void onDisable()` |
| -- | `void onGameTick(int32_t tick)` |
| -- | `void onClientTick()` |
| -- | `void onMainLoop()` -- outer client loop in every state, including title/login screens. Added in SDK 118. |
| `MenuClickEvent` | `void onMenuOptionClicked(MenuClickEvent&)` (`consume()` blocks; `replaceWith(...)` or replacement setters change the DoAction that reaches the game). Wraps `MenuOptionClickedEvent` / RuneLite-style menu fields. |
| `ScriptEvent` | `void onScriptFired(const ScriptEvent&)` |
| `VarbitChangedEvent` | `void onVarbitChanged(const VarbitChangedEvent&)` |
| `GameStateChangedEvent` | `void onGameStateChanged(const GameStateChangedEvent&)` -- native `Client.GameState` transitions from `SetGameState`. Values use native `LoginGameState` numbers; `Loading` (25) is mapped in SDK v126+. Added in SDK 91. |
| `ChatMessageEvent` | `void onChatMessage(const ChatMessageEvent&)` |
| `SoundPlayedEvent` | `void onSoundPlayed(SoundPlayedEvent&)` (`consume()` marks the event handled for handler ordering; use `state::audio()` for playback suppression). Fires when the client plays a synth sound effect or a MIDI jingle (`event.kind()`). Added in SDK 69. |
| `HitsplatAppliedEvent` | `void onHitsplatApplied(const HitsplatAppliedEvent&)` -- applied hitsplats with `actor()` resolving to a `Player`, `Npc`, or empty actor when unresolved. Added in SDK 74; native signature corrected in SDK 76. |
| `ActorSpotAnimEvent` | `void onActorSpotAnim(const ActorSpotAnimEvent&)` -- actor-attached spot animations with `actor()` resolving to a `Player`, `Npc`, or empty actor when unresolved. Clear/removal ids are filtered. Added in SDK 76. |
| `AnimationChangedEvent` | `void onAnimationChanged(const AnimationChangedEvent&)` -- accepted actor animation field changes with `actor()` resolving to a `Player`, `Npc`, or empty actor when unresolved. Same-animation resets and rejected native requests are filtered. Added in SDK 78. |
| `ItemContainerChangedEvent` | `void onItemContainerChanged(const ItemContainerChangedEvent&)` -- tick-level diff of mapped containers. Added in SDK 26. |
| `MouseButtonEvent` | `void onMousePressed(MouseButtonEvent&)` / `void onMouseReleased(MouseButtonEvent&)` -- real (non-synthetic) mouse button press/release with client-area `x()/y()`, `button()`, and `KeyboardMods` `modifiers()`. `consume()` on a PRESS suppresses the native click and its paired release; `consume()` on a RELEASE is handler-ordering only (never suppresses, so the game can't be stranded down-without-up). Suppression covers the game's WndProc (V1) pipeline only -- the low-level (V2) pipeline still observes real hardware input. Fires on the INPUT thread -- copy fields out, act from `onClientTick`. Added in SDK 115. |
| -- | `void onNpcSpawned(const Npc&)` / `onNpcDespawned(const Npc&)` |
| -- | `void onPlayerSpawned(const Player&)` / `onPlayerDespawned(const Player&)` |
| -- | `void onTileObjectSpawned(const TileObject&)` / `onTileObjectDespawned(const TileObject&)` |
| -- | `void onProjectileSpawned(const Projectile&)` / `onProjectileDespawned(const Projectile&)` / `onProjectileMoved(const Projectile&)` |
| -- | `void onGraphicsObjectSpawned(const GraphicsObject&)` / `onGraphicsObjectDespawned(const GraphicsObject&)` / `onGraphicsObjectMoved(const GraphicsObject&)` |
| -- | `void onSettingChanged(const char* settingKey)` |
| `CrossTabChange` | `void onCrossTabChanged(const CrossTabChange&)` -- a key in this plugin's Cross-Tab Store namespace changed, the instance just bound (one `Replay` per key), or one of its own writes has an outcome. Runs on the game thread whether or not the plugin is enabled; never carries the value. Added in SDK 141. |
| -- | Side panels: register in the constructor with `panel(id, title, build).onAction(fn).icon("awesome:gear" \| "lucide:house" \| "phosphor:gear:bold" \| glyph).iconColor(0xAARRGGBB).image(path)`. The `build` lambda runs render-thread on the controller and does NOT dispatch titan:: queries -- see threading note. SDK 65+, icon specs/colour SDK 80+. |

`onMainLoop` does not advance the gameplay caches. Static definition lookups
remain available before login; live queries must be gated on world readiness:

```cpp
void onMainLoop() override {
    auto itemDef = titan::state::cache().item(4151); // safe in every state
    if (!titan::state::login().isWorldReady()) return;
    auto local = titan::state::client().localPlayer();
}
```

```ts
onMainLoop() {
    const itemDef = titan.state.cache.item(4151); // safe in every state
    if (!titan.state.login.isWorldReady) return;
    const local = titan.state.client.localPlayer;
}
```

---

## Registration

| Symbol | Purpose |
| --- | --- |
| `titan::Plugin` | Base class; inherit and override lifecycle + event virtuals |
| `TITAN_PLUGIN_META(id, name, description, author, version, defaultEnabled)` | Declare all plugin metadata in one macro. Supersedes `TITAN_PLUGIN` (SDK 23). |
| `Plugin::panel(id, title, build)` | Register a side panel (constructor-time). Chain `.onAction(fn)`, `.icon(spec\|glyph)`, `.iconColor(argb)`, `.image(path\|bytes,len)`. A plugin may register up to 8 panels; the controller shows one nav button per panel. Panel content can use semantic helpers such as `section`, `status`, `primaryButton`, `inputPassword`, and `beginCollapsible`/`endCollapsible`. SDK 65+ (helpers expanded in SDK 72; named/tinted icons in SDK 80). |
| `TITAN_PLUGIN_DEPS(TypeA, TypeB, ...)` | Declare load-order dependencies by plugin TYPE (RuneLite-style). Each type's static `kPluginId` is added to the dependency graph; the host loads them first and rejects cycles / missing deps. SDK 66+. |
| `TITAN_PLUGIN_DEP_IDS("id", ...)` | Declare load-order dependencies by id STRING. Escape hatch for cross-language deps where the C++ type is unavailable; prefer `TITAN_PLUGIN_DEPS`. SDK 66+. |
| `TITAN_REGISTER_PLUGIN(ClassName, "id")` | Single-plugin DLL. Emits `TitanPlugin_QueryNative` plus static `TitanPluginIdStr` and `TitanPluginIdList` metadata. |
| `TITAN_REGISTER_PLUGINS(ClassA, ClassB, ...)` | Multi-plugin DLL. `TitanPlugin_QueryNative` returns a module descriptor with count, per-index requirements, and an instance factory. Emits `TitanPluginIdList` for static metadata readers. Use one registration macro per DLL and `titan_add_plugin(... SLUGS a b c ...)` for staging. |
| `TITAN_REQUIRE_NATIVE_INTERFACES(...)` | Declare required `{interfaceId, majorVersion, minStructSize}` contracts. Other domains remain optional. The C++ factory always requires the instance lifetime interface. |
| `Plugin::onUnload()` | Final unload hook, independent of UI enabled state. Signal and join load-lifetime workers. Release operation-scoped service references before teardown; admissions are already stopped, so do not submit new work. |
| `titan::BoolSetting` / `IntSetting` / `IntInputSetting` / `ColorSetting` / `ComboSetting` / `StringSetting` / `ProtectedStringSetting` / `ButtonSetting` / `MatrixSetting` | Constructor-registered plugin settings (`IntInputSetting` is an `IntSetting` typed into a number box with -/+ buttons, for exact values such as quantities; a controller that predates it shows "unsupported control". `ButtonSetting` is a value-less action button whose `onClick` runs on the game thread, SDK 98; `MatrixSetting` is a checkbox grid whose rows name the columns they have, SDK 135). `set()` / `reset()` / `setCell()` / `toggleCell()` persist the new value to the user's config (SDK 140); `setHidden()` changes visibility only. |
| `titan::Section` | Section grouping within the settings panel. `SectionOptions::parent` (SDK 143) nests a section inside another of the same plugin: a collapsing header within the parent's, after the parent's own settings. Mirrored as `SectionOptions.parent` in JS/TS and `@ConfigSection(parent = ...)` in Java. |
| `titan::Overlay` | Self-registering render overlay (lambda or subclass form) |
| `titan::OverlayPanel` | Self-registering, anchor-based, Alt-draggable HUD panel (subclass of `titan::Overlay`). Recommended path: `Plugin::overlayPanel(name, anchor, [priority,] lambda)`. SDK 46+. |

---

## Writing your own settings (SDK 140)

A plugin changing one of its own settings used to be invisible to the
controller: the value lived only in the plugin's member, nothing was
saved, and the next tab init replayed the persisted value over it. As of
SDK 140 a programmatic write is saved exactly as a side-panel edit is.

```cpp
class TripCounter final : public titan::Plugin {
    TITAN_PLUGIN_META("trip_counter", "Trip Counter", "", "", "1.0", false)

    titan::IntSetting trips{this, "trips", "Trips done", 0, 0, 1000};
    titan::BoolSetting stopWhenDone{this, "stop", "Stop when done", true};
    titan::IntSetting target{this, "target", "Target trips", 20, 1, 1000};

    void onGameTick(int32_t) override {
        if (!bankedThisTick()) return;
        trips.set(trips.value() + 1);        // saved; survives a restart
        if (stopWhenDone && trips.value() >= target.value()) {
            titan::plugins().self().disable();
        }
    }
};
```

Notes that matter in practice:

- **Every typed setter counts.** `set()` is the funnel for `reset()`,
  `ColorSetting::set`, and `MatrixSetting::set` / `setCell` /
  `toggleCell`, so all of them persist.
- **Call it as often as you like.** The host bumps a counter and marks the
  snapshot dirty; the controller writes the file once per page, so a
  per-tick write coalesces into one save.
- **`setHidden()` is not a value change.** It repaints the panel and
  writes nothing, so a setting hidden behind a mode switch keeps whatever
  the user last chose.
- **Writing the value it already holds does nothing.** No save, and no
  `onSettingChanged`, so re-asserting a value every tick is free.
- **The value is sanitized at the source.** `IntSetting::set` clamps to
  `[min, max]`, `ColorSetting::set` masks, `MatrixSetting` drops
  unavailable cells -- and what gets saved is the sanitized value, not
  what you asked for.
- **Values pushed down by the controller do not count.** Those arrive
  through the setting's `apply()`, which never reports. That is what stops
  an out-of-range int being clamped and saved back, or a secret whose
  DPAPI unwrap failed being saved as an empty string.
- **Buttons are never persisted**, here or from the UI -- they hold no
  value.

The same behaviour is available in the other two SDKs:

| | C++ | JS/TS | Java (`@Inject ConfigManager`) |
|---|---|---|---|
| read a value | `setting.value()` | `setting.value` | `config.item()` on the proxy |
| write a value | `setting.set(v)` | `setting.value = v` | `set` / `setString` / `setEnum` |
| restore default | `setting.reset()` | `setting.reset()` | `reset(Cfg.class, "key")` |
| read visibility | `setting.isHidden()` | `setting.isHidden` | `isHidden(Cfg.class, "key")` |
| set visibility | `setting.setHidden(b)` | `setting.isHidden = b` | `setHidden(Cfg.class, "key", b)` |
| grid: one cell | `setCell(r, c, on)` | `set(r, c, on)` | `setCell(Cfg.class, "key", r, c, on)` |
| grid: toggle | `toggleCell(r, c)` | `toggle(r, c)` | `toggleCell(Cfg.class, "key", r, c)` |
| grid: whole grid | `mask()` / `grid()` | `value` / `toGrid()` | `setGrid(...)` / `toGrid()` |

Java's reference-typed writes have distinct names (`setString`, `setEnum`,
`setGrid`) rather than `set` overloads, because a bare `null` argument
would match all three and fail to compile at the call site.

---

## Overlay panels (SDK 46)

`titan::OverlayPanel` is a subclass of `titan::Overlay` that adds
RuneLite-style structured HUD panels: anchored placement, Alt-drag
repositioning, sticky theming (rounded corners, borders, padding,
per-component colours), and per-machine layout persistence to
`%USERPROFILE%\.titanclient\overlay_layout.json`.

### Anchors (`titan::Anchor`)

Minimal semantic set -- only values whose layout semantics can't be
replicated by free positioning are exposed. Corner / canvas anchors
from the original RuneLite `OverlayPosition` enum are intentionally
omitted; users free-position into those areas via Alt-drag (panels
become `Dynamic` once moved).

| Value | Meaning |
| --- | --- |
| `Dynamic` | Panel positions itself. Default anchor; placed at the top-left margin until the user drags it. |
| `TopCenter` | Stacks horizontally centered along the top edge of the game viewport widget (`161.15`). Falls back to the window-relative center when the viewport widget isn't loaded. |
| `LeftCenter` | Stacks vertically centered along the left edge of the game viewport widget. |
| `RightCenter` | Stacks vertically centered along the right edge of the game viewport widget. |
| `AboveChatboxRight` | Anchored above the chatbox, right side. Used for things like XP trackers. |
| `Tooltip` | Follows the mouse cursor; only the most-recent tooltip renders, drag disabled. |

### Style (`titan::OverlayPanelStyle`)

Sticky -- set once via `setStyle(s)` (or any of the convenience
setters) and the host applies it every frame until replaced.

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `background` | `uint32_t` (ARGB) | `0xC8141821` | Semi-transparent slate. |
| `borderColor` | `uint32_t` (ARGB) | `0xFF3B5566` | Alpha=0 disables border. |
| `borderThickness` | `float` (px) | `1.0` | 0 disables border. |
| `cornerRadius` | `float` (px) | `4.0` | 0 = sharp corners. |
| `padHorizontal` / `padVertical` | `int32_t` | `8` / `6` | Inset for content. |
| `lineGap` | `int32_t` | `2` | Vertical gap between rows. |
| `titleColor` | `uint32_t` (ARGB) | `0xFF9DEBFF` | Default `title()` colour. |
| `lineLeftColor` / `lineRightColor` | `uint32_t` (ARGB) | `0xFFEAF2F8` / `0xFFEAF2F8` | Default `line()` text colours. |
| `barFillColor` / `barBgColor` | `uint32_t` (ARGB) | `0xFF60E060` / `0xFF333333` | Default `progressBar()` colours. |

### `titan::OverlayPanel` builders

| Method | Purpose |
| --- | --- |
| `title(text, color?)` | Append a bold title row (~22 px). |
| `line(left, right, leftColor?, rightColor?)` | Two-column label/value row. Right text right-aligned. |
| `line(text, color?)` | Single-value variant. |
| `progressBar(value, min, max, fillColor?, bgColor?)` | Progress bar row (~12 px). |
| `setPreferredWidth(px)` | Width hint, clamped to `[80, 600]`. |
| `setStyle(style)` | Replace the sticky style. |
| `style()` | Read the current style. |
| `setBackgroundColor` / `setBorderColor` / `setBorderThickness` / `setCornerRadius` / `setPadding(h, v)` / `setLineGap` / `setTitleColor` / `setLineColors(l, r)` / `setProgressBarColors(f, b)` | Convenience setters that mutate one field of the sticky style. |
| `setOpacity(alpha01)` | Multiply every alpha channel by a 0..1 value. |

### Plugin factory

The recommended way to declare a panel is via `Plugin::overlayPanel(...)`,
which returns a reference for chained styling:

```cpp
overlayPanel("main", titan::Anchor::Dynamic,
             [this](titan::OverlayPanel& p) {
    p.title("Chompy Bird Hunter");
    p.line("Status:", worker_.status());
}).setCornerRadius(8.0f);
```

Subclassing `titan::OverlayPanel` directly is supported for stateful
panels but rarely needed.

### TypeScript / JavaScript

Mirrored as `Plugin.overlayPanel({ name, anchor?, priority?, style?, render })`
and `titan.OverlayAnchor` enum. The `render` callback receives an
`OverlayPanelInstance` with the same builder + style methods.

### ABI surface (private)

Eight new `HostApi` function pointers + `AnchorAbi` namespace +
`OverlayPanelStyleAbi` struct. See the v46 entry in
[`detail/abi.h`](detail/abi.h) and [`CHANGELOG.md`](CHANGELOG.md).

---

## Threading guarantees

Documented once here; plugin authors can rely on this between SDK minor
versions.

| Callback / method | Runs on | Safe to call `titan::*` queries? | Must dispatch mutations? |
| --- | --- | --- | --- |
| `onGameTick`, `onClientTick` | Game thread | Yes | No |
| `onMainLoop` | Game thread | Static definition-cache reads always; live queries only when `state::login().isWorldReady()` | No |
| `onNpcSpawned/Despawned`, `onPlayerSpawned/Despawned`, `onTileObject*` | Game thread | Yes | No |
| `onProjectileSpawned/Despawned/Moved` | Game thread | Yes | No |
| `onGraphicsObjectSpawned/Despawned/Moved` | Game thread | Yes | No |
| `onMenuOptionClicked`, `onChatMessage`, `onSoundPlayed`, `onHitsplatApplied`, `onActorSpotAnim`, `onAnimationChanged`, `onGameStateChanged`, `onVarbitChanged`, `onScriptFired` | Game thread (inside the detour or main-loop marshal) | Yes for queries; avoid heavy work | Dispatch heavy work via `runOnClientTick` |
| `onMousePressed`, `onMouseReleased` | **Input (message-pump) thread** -- may NOT be the game thread | **No** -- do not read game state or call `titan::*` queries/actions; copy the event fields out and return quickly | Hand off to a game-thread callback (e.g. store a pending value and act in `onClientTick`) |
| `onRender` overlay callback | Render thread (SwapBuffers hook) | Yes for queries; mutation APIs must be dispatched | Dispatch mutations via `runOnClientTick` |
| `titan::screenshot()` `submit` / `poll` / `copyPng` / `release` | Any plugin callback thread | N/A -- a mutex-guarded host table; the capture itself rides the render thread and encoding a worker, so none of the work lands on the caller | No |
| side panel `build` / `onAction` callbacks | Controller process | No -- HostApi does NOT reach the game thread from here. Treat panel code as pure UI + intent queueing. | N/A |
| `onEnable`, `onDisable` | Game thread (client.dll side) | Yes | No |
| `onSettingChanged` | Controller or game thread | Queries fine in-game; no-op from the controller | N/A |
| `onCrossTabChanged` | Game thread, from the MainLoop drain (title and login screens too), enabled or not, with the host's dispatch lock held | Static definition-cache reads always; live queries only when `state::login().isWorldReady()` | Keep it cheap: record the change and wake a worker |
| `titan::crossTab(plugin)` calls | Any thread, including from inside the plugin's own locks | N/A -- takes only the store's own lock, never the host's | No |

Re-entry model: the chat + varbit + sound-effect + hitsplat + spotanim + animation hooks are self-serialising
via a thread-local depth guard, so a plugin injecting `titan::addChatMessage`
from inside `onChatMessage` does NOT recursively re-fire its own
`onChatMessage`; likewise a sound triggered from inside `onSoundPlayed`
plays through without re-dispatching the event.

---

## Compiler compatibility matrix

| Target | Compiler | C++ | Runtime library | Notes |
| --- | --- | --- | --- | --- |
| Plugin DLL | MSVC 2022+, Windows x64 | C++20 | `MultiThreaded` / `MultiThreadedDebug` (`/MT` / `/MTd`) | Supported SDK helper profile; `/W4` recommended. Binary calls use naturally aligned C-style records/function pointers. Allocations are freed by their owning module; STL objects and C++ exceptions do not cross the boundary. |
| JS plugin | TypeScript -> ES2020 | N/A (runs in QuickJS) | -- | Use the bundled `titan-plugin-sdk.d.ts` for typing. |

---

## Version compatibility

Native ABI v1 and the SDK floor decide whether a native DLL loads.
SDK 146 grew `WidgetState`, so `kMinSupportedSdkVersion` is 146: rebuild every
native DLL built against SDK 145 or older, and publish the client, controller
and plugins together. The loader discovers
`TitanPlugin_QueryNative(uint32_t abiVersion)`, which returns a module-owned
`ModuleDescriptorV1`. It checks required interfaces before calling the
per-index factory. The factory returns a plugin-owned `PluginDescriptorV1`;
its destructor releases both the plugin and descriptor inside the owning DLL.
No SDK-sized output structure is written into another module's allocation.

| Situation | Outcome |
| --- | --- |
| A native plugin built against SDK 146+ and a 146+ host implement ABI v1 and its required capabilities | Loads. A plugin built against a newer SDK than the host also loads; functions the host lacks import as unavailable. |
| A module's `sdkRelease` is below the host's `kMinSupportedSdkVersion` | Refused before the plugin is constructed, with a rebuild message. |
| An SDK-built plugin finds the host's `HostCoreV1::sdkRelease` below its own `kMinSupportedSdkVersion`, or a host too old to publish one | The plugin's factory refuses creation, asking for a TitanClient update. |
| An optional domain or appended function is absent | Imports as unavailable; facade checks retain their documented failure/default behaviour. |
| A required interface id, major, or byte prefix is absent | Creation is refused before constructing the plugin instance; DLL initialization and bootstrap discovery have already run. |
| A bootstrap ABI major is unsupported | Query fails cleanly. |
| A DLL exports only legacy `TitanCreatePlugin` | Rebuild against the Native ABI v1 SDK. |

`kSdkVersion` identifies the source release. `kMinSupportedSdkVersion` is the
native load floor: it rises only in the change that alters a payload record
layout, never for appended optional functions. Modules publish their release
in `ModuleDescriptorV1::sdkRelease` and hosts in `HostCoreV1::sdkRelease`;
`PluginDescriptorV1::sdkRelease` is diagnostic only.
`NativeAbi::kAbiVersion` identifies the bootstrap contract.
Individual capabilities have stable ids, major versions, and published byte
sizes. `HostApi` and `PluginApi` are local dispatch views populated from those
capabilities; their local layout is not the DLL contract.

The host domains are Core, Game, Actions, Render, and Navigation, plus the
instance Lifetime interface. Plugin callbacks are grouped into Core, UI, and
Events. A required capability specifies a minimum complete prefix. Optional
functions can be appended without moving an existing field. Consumers check
table size before reading a member, including when the producer allocated a
physically shorter table. Advanced plugins can declare extra requirements
with `TITAN_REQUIRE_NATIVE_INTERFACES`; include the required `LifetimeV1`
contract when supplying an explicit list.

Payloads follow a stricter rule: **a record's layout, nested array extents,
enum values, field types and meanings change only together with
`kMinSupportedSdkVersion`**, so the host refuses every DLL built against the
old layout. A `structSize` field alone does not make arrays extensible because
their element stride is fixed.
Cache-definition records use `kNativePayloadVersion == 1`; source SDK
releases do not change accepted snapshot tags. Native UI records live in
`detail/native_records.h`, independently of the controller IPC records.

`detail/native_payload_v1.h` pins payload layouts, array types and numeric
values; `detail/native_abi.h` pins capability offsets. The compatibility
suite also loads a real DLL compiled from a frozen standalone v1 header,
checking short tables, settings arrays, instance state and destruction.
Released fixture DLLs should be retained and tested without recompilation;
they exercise the ABI mechanics directly, and the SDK floor is covered by the
native plugin instance tests.

### Optional HTML interfaces (v1)

| Interface | Stable ID | Major | Complete required prefix (Windows x64) |
| --- | --- | --- | --- |
| `HostHtmlUiV1` | `0x1007` | 1 | 16 bytes |
| `PluginHtmlPanelsV1` | `0x2004` | 1 | 88 bytes |
| `PluginHtmlOverlaysV1` | `0x2005` | 1 | 88 bytes |

Each plugin HTML table contains `InterfaceHeader` and the complete
`HtmlUiCallbacks` block. All ten callbacks must be present. Missing tables,
physically short allocations, partial final pointers, null required callbacks,
and unknown majors import atomically as absent. A future tail is ignored.
The host capability returns support bits `kHtmlUiSidePanels` (1) and
`kHtmlUiOverlays` (2), with `kHtmlUiRuntimeAvailable` (`1u << 31`) independently
describing renderer availability. The new interfaces are not default required
capabilities; old hosts and old ABI v1 plugins continue to load.

The new immutable records in `detail/html_ui_abi.h` are `DescriptorV1` (456
bytes), `ResourceV1` (324 bytes), and `SnapshotV1` (48 bytes). These records,
their field meanings, and their array strides must not grow after publication.
The existing 168-byte native `PanelDescriptor` remains unchanged.

`getDescriptors` returns the total count and copies at most the supplied
capacity. `getResource` copies immutable metadata; `copyResource` and `copyIcon`
copy bounded byte ranges. `copyState` and `copyMessage` return required byte
counts excluding NUL, and leave null/short output buffers untouched. A changed
state revision returns zero so the caller can retry its snapshot. `reset(0)`
deactivates; a new nonzero activation discards transients and replays state;
repeating the current activation is idempotent. `acknowledge` only accepts an
issued sequence for the current activation. `dispatchMessage` validates the
activation and envelope, but the host must first marshal it to MainLoop under
the lifecycle gate and validate the complete controller/client identity.

### Native ownership and service lifetime

Each instance has its own host context. Callback entry installs that context
on the executing thread and restores the previous context on return. Native
scheduled callbacks capture the submitting owner; rejected or cancelled work
still runs cleanup exactly once. For calls outside a dispatched callback,
use the explicit-owner scheduler/service overloads and keep the owner alive.

`titan::service<T>(id)` and `titan::service<T>(owner, id)` return a move-only
`ServiceRef<T>`, not a bare borrowed pointer. A successful reference pins the
provider instance and module for that operation. Access it with `operator->`
or `get()`, and release it promptly by leaving scope or calling `reset()`.
Never cache the raw result of `get()` after releasing the reference. Service
contracts still need their own versioned POD tables; the registry guarantees
lifetime, not compatibility of an arbitrary C++ class or service payload.
An optional `HostCoreV1::getHostService` whitelist serves only permanent
host-resident tables such as the performance probe. Those may be cached for
the host lifetime; this path never exposes the plugin service registry.

Final unload refuses new work and service acquisitions, cancels queued work,
and drains finite callbacks. After disable, `Plugin::onUnload()` signals and
joins load-lifetime workers. Service references must have been released before
this hook: they participate in the earlier finite-call drain. Destruction and
DLL unload happen only once all remaining work and service leases are gone.
Do not wait for a newly scheduled callback from `onUnload()`; admissions are
already stopped. Use `titan::startPluginWorker(owner, fn)` from `<titan/worker.h>` for native
workers. Its move-only `PluginWorker` carries instance context and keeps the
DLL pinned through `join()`, including cleanup of callable captures. Signal
cancellation and call `join()` from `onUnload()`; there is no detach operation.
Workers tied to enabled state should also be stopped/joined in `onDisable()`. Ordinary UI disable is
separate from final unload, so a service provider may remain available while
its UI is disabled.

### Plugin identity stability

A plugin's id (`TITAN_PLUGIN_META`'s first argument) is the key used by the
controller to persist settings and enabled state. Changing a plugin's id
between releases orphans the user's saved state -- treat it as a permanent
identifier, not a display name.

---

## Quickstart (native plugin)

Start with the bundled
[native example](https://github.com/Soxs/titan-public-sdk/tree/main/examples/native-plugin)
in `titan-public-sdk`. Its
[README](https://github.com/Soxs/titan-public-sdk/blob/main/examples/native-plugin/README.md)
covers configuring, renaming, and Run/Reload/Watch in CMake or an IDE. The bundled
example finds its parent SDK automatically; a standalone copy can select an SDK
path explicitly. No private source repository is required. The equivalent
manual CMake integration is:

`my_plugin/CMakeLists.txt`:

```cmake
cmake_minimum_required(VERSION 3.24)
project(my_plugin LANGUAGES CXX)

# Environment variables (or `-D` cache overrides):
#   TITAN_PLUGIN_SDK_ROOT -> your titan-public-sdk checkout
#   TITAN_CLIENT_ROOT     -> optional directory containing controller.exe
set(TITAN_PLUGIN_SDK_ROOT "$ENV{TITAN_PLUGIN_SDK_ROOT}" CACHE PATH "")
set(TITAN_CLIENT_ROOT     "$ENV{TITAN_CLIENT_ROOT}"     CACHE PATH "")

# add_subdirectory on the SDK's own root CMakeLists.txt pulls in the
# titan_sdk INTERFACE library AND the titan_add_plugin /
# titan_add_dev_session helpers.
add_subdirectory("${TITAN_PLUGIN_SDK_ROOT}" titan_sdk)

# Output stages to build/.titan/dev/my_plugin/<config>/load/gen-N/ with the PDB
# co-located, so debugger breakpoints resolve out of the box.
titan_add_plugin(
    TARGET  my_plugin
    SLUG    my_plugin
    SOURCES my_plugin.cpp
)

# Adds titan_stage_, titan_run_, titan_reload_, and titan_watch_ targets.
# Run builds/stages and starts or reuses this live DEV tab. Reload replaces
# only its native module without restarting the game; Watch repeats after
# source changes. No installed runtime is needed to configure/build.
titan_add_dev_session(
    PLUGIN      my_plugin
    LOADER_MODE DebuggableLoadLibrary   # alternatives: Restart, MemoryBlob
)
```

`my_plugin/my_plugin.cpp`:

```cpp
#include <titan/plugin.h>
#include <titan/setting.h>
#include <titan/client.h>          // titan::state::client(), titan::log, ...
#include <titan/query.h>           // titan::queries::npcs(), ...
#include <titan/render.h>
#include <titan/utils/inventory.h> // titan::utils::Inventory::*

class MyPlugin : public titan::Plugin {
    TITAN_PLUGIN_META(
        "my_plugin",
        "My Plugin",
        "Shows how to wire up a native Titan plugin with the SDK.",
        "Your Name",
        "1.0.0",
        /*defaultEnabled=*/false)

public:
    titan::BoolSetting loud{this, "loud", "Verbose logging", false};

    MyPlugin() {
        onRender(titan::Layer::AboveScene, [this] { drawWorld(); });
    }

    void onGameTick(int32_t tick) override {
        if (loud && tick % 10 == 0) {
            titan::logf("tick=%d invFull=%d",
                        tick,
                        titan::utils::Inventory::isFull() ? 1 : 0);
        }
    }

private:
    void drawWorld() {
        auto local = titan::state::client().localPlayer();
        if (!local) return;
        titan::queries::npcs()
            .nameContains("Chicken")
            .forEach([](const titan::Npc& n) {
                titan::overlay().entityBox(n, 0xFF00FF00);
            });
    }
};
TITAN_REGISTER_PLUGIN(MyPlugin)
```

## Quickstart (JS plugin)

```ts
/// <reference path="../titan-plugin-sdk-v41/include/titan-plugin-sdk.d.ts" />

class MyPlugin extends titan.Plugin {
    id = "my_plugin";
    name = "My Plugin";
    description = "JS version of the sample, on the v41 shape.";
    author = "Your Name";
    version = "1.0.0";

    loud = this.boolSetting({ key: "loud", name: "Verbose logging", default: false });

    onGameTick(tick: number) {
        if (this.loud && tick % 10 === 0) {
            const full = titan.utils.inventory.isFull() ? 1 : 0;
            titan.logf("tick=%d invFull=%d", tick, full);
        }
    }

    onRender() {
        const local = titan.state.client.localPlayer;
        if (!local) return;
        titan.queries
            .npcs()
            .nameContains("Chicken")
            .forEach((n) => titan.overlay.entityBox(n, 0xff00ff00));
    }
}
titan.register(new MyPlugin());
```

Compile with `tsc` or `esbuild`; drop the `.js` output in
`~/.titanclient/plugins/`. JS plugins hot-reload on save.

---

## Reference plugins

The public SDK includes a complete
[native starter](https://github.com/Soxs/titan-public-sdk/tree/main/examples/native-plugin)
with settings, a tick callback, and build/reload targets. The additional examples
below are maintained in the private client source tree for client contributors;
access to them is not required to use this SDK.

| Pattern | Reference plugin | Shows |
| --- | --- | --- |
| Developer tools panel + internal windows | `plugins/dev_tools/` | `panel(...)`, `.onAction(...)`, `HostApi::setInternalToolVisible`, overlay settings |
| Overlay with settings + sections | `plugins/tile_overlay/` | `BoolSetting`, `IntSetting`, `StringSetting`, `Section`, `onRender`, `titan::overlay()`, `titan::queries::objects()` |
| Entity queries + world-space drawing | `plugins/entity_overlay/` | `titan::queries::players()`, `titan::queries::npcs()`, `titan::overlay().entityBox`, `titan::overlay().screenLine` |
| Panel-heavy plugin with actions | `plugins/quest_helper/` | `panel(...)` with interactive controls, `.onAction(...)`, settings persistence |
| Varbit events + idle timer | `plugins/never_logout/` | `onGameTick`, `titan::state::idle()`, HUD overlay |
| Login / account flow | `plugins/account_profiles/` | Load-lifetime `titan.account_profiles.v1` POD service, stable profile ids, sanitized proxy routing, Standard/Jagex activation, encrypted settings storage |
| Coordinated breaks | `plugins/break_handler/` | Native Break Handler coordinator, Profiles service consumer, monotonic scheduler, cancellable worker, panel/config persistence |
| Combat automation | `plugins/auto_fighter/` | `titan::queries::npcs().nameContains`, `nearestTo`, `interact`, instance-based Break Handler safe-pause/resume participation |

---

## Error handling

- Plugins must not throw exceptions across the C ABI. The host wraps each
  callback in a try/catch that logs the exception and disables the plugin
  on fatal errors.
- `titan::log` / `titan::logf` route to the client log (visible in the
  controller's log tab).
- Functions that might fail return `uint8_t` (1 = ok, 0 = failure) in the C
  ABI, `bool` / `std::optional<T>` in the C++ fluent facades.
- Plugin load failures surface in the controller's plugin list with a reason
  such as an unsupported bootstrap ABI or a missing required capability. Source
  SDK release numbers alone do not reject a Native ABI v1 DLL.

---

## Non-public surface (explicit)

These implementation details are not supported plugin-facing APIs and may
change. The frozen native ABI contracts documented above are a separate surface:

- `TitanPluginSdk::HostApi` and `TitanPluginSdk::PluginApi` struct details
  (local dispatch views, not the frozen DLL boundary).
- Facade implementation helpers under `<titan/detail/>`; this does not remove
  the compatibility guarantees of the native ABI tables and payload records.
- `titan::Plugin::host()` -- kept only for pre-SDK-23 compat; prefer the
  fluent facades.
- Everything in `client/core/`, `client/game/`, `client/hooks/`,
  `client/actions/`, `client/overlay/`, `client/debug/`, `client/plugins/`,
  `client/titan_backend/`.
- `InternalBackend`, `ExternalBackend` (the backend layer is an internal
  abstraction; plugin code talks through the facades).
- Raw offset / memory / hook APIs (`Offsets::`, `safeRead<T>`,
  `HookManager::`, `PacketHook::`). Strict sandbox.

If your plugin needs something not on this page, open an issue against the
registry or propose the API extension: an SDK bump adds the symbol here,
adds a line to the abi.h changelog, and becomes part of the stable surface.


### Actor overhead text (SDK 127; Java artifact 0.1.49)

`Player`, `Npc`/`NPC`, and shared `Actor` expose `getOverheadText()` and
`getOverheadTextCyclesRemaining()`. C++ returns `optional<string>` and
`optional<int32_t>`; Java returns nullable `String`/`Integer`; JS/TypeScript
returns `string | null` / `number | null`. A successful empty string is distinct
from an invalid or unavailable read. Text uses UTF-8 on the native ABI and is
length-aware throughout; embedded NULs and long utterances are retained. A
positive lifetime alone does not imply nonempty text. Call on the game thread.

C++ `Plugin::onOverheadTextChanged(const OverheadTextChangedEvent&)` and
JS/TypeScript `onOverheadTextChanged(event)` receive each accepted utterance.
Java event-bus subscribers use `OverheadTextChanged.getActor()` and
`getOverheadText()`. Actor type, identity, and WorldView are captured before
dispatch; event text is the owned snapshot of the completed native write.
Identical consecutive text, self-assignment, and accepted empty text each
produce events. Public rejection and expiry/clearing do not produce events.

C++ `state::client().getOverheadTextCapabilities()`, Java
`Client.getOverheadTextCapabilities()`, and JS/TypeScript
`titan.overheadTextCapabilities()` return a bitmask: `1` for validated direct
access, `2` only when all five speech hooks are installed. Direct access can
remain available without events. Query after reload: capabilities are cleared
and revalidated against the loaded executable.

Native ABI views are borrowed until callback return. Copy exactly
`overheadTextLength` bytes; do not use `strlen`. C++ event `overheadText()` /
`getOverheadText()` returns an owned copy in the plugin's CRT. The host owns
queued text and preserves acceptance order across deferred/nested dispatch.
Never free host pointers or pass string/vector ownership across DLLs.

```cpp
void onOverheadTextChanged(const titan::OverheadTextChangedEvent& event) override {
    auto actor = event.actor();
    auto text = event.overheadText(); // Owned, including embedded NULs.
}
```

```typescript
onOverheadTextChanged(event: titan.OverheadTextChangedEvent) {
    const snapshot = event.overheadText;
    const cycles = event.actor.getOverheadTextCyclesRemaining();
}
```

```java
@Subscribe
public void onOverheadTextChanged(OverheadTextChanged event) {
    Actor actor = event.getActor();
    String snapshot = event.getOverheadText();
}
```
