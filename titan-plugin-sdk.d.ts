/**
 * Titan Client Plugin SDK - TypeScript Definitions
 *
 * Write plugins in TypeScript, compile to JS with tsc or esbuild, and drop
 * the .js file into ~/.titanclient/plugins/ or plugins/ next to the client.
 *
 * # Three shapes of the SDK (SDK 41+)
 *
 *   `titan.queries.*`   live snapshots; chainable filters; call-then-iterate.
 *                       Examples: `titan.queries.npcs().nameContains("...").first()`
 *                                 `titan.queries.inventory().id(995).totalQuantity`
 *
 *   `titan.state.*`     subsystem state and actions; no chaining.
 *                       Examples: `titan.state.client.tick`
 *                                 `titan.state.widgets.find(packedId)`
 *                                 `titan.state.world.hop(308)`
 *
 *   `titan.utils.*`     static composition helpers; no setup; just call.
 *                       Examples: `titan.utils.inventory.isFull`
 *                                 `titan.utils.equipment.unequip("Bow")`
 *                                 `titan.utils.dialogue.continueDialogue()`
 *
 * Top-level (`titan.x`) is reserved for free helpers (`log`, `logf`,
 * `addChatMessage`, `runOnClientTick`, `runOnRender`), registration
 * (`Plugin`, `register`, settings), and enums (`MenuAction`, `Skill`,
 * `Prayer`, `Varbits`, `InventoryID`, `EquipmentSlot`, ...).
 *
 * Two ways to write a plugin:
 *
 * 1) Class-based (recommended):
 * ```ts
 * class MyPlugin extends titan.Plugin {
 *     id = "my_plugin";
 *     name = "My Plugin";
 *     range = this.intSetting({ key: "range", name: "Range",
 *                               default: 5, min: 1, max: 20 });
 *     world = this.overlay({ layer: titan.OverlayLayer.ABOVE_SCENE, render: () => {
 *         titan.queries.npcs().nameContains("Chicken").forEach(n => {
 *             titan.overlay.entityBox(n, 0xFF00FF00);
 *         });
 *     }});
 *     onGameTick(tick: number) {
 *         const local = titan.state.client.localPlayer;
 *         if (!local) return;
 *         const target = titan.queries.npcs().nameContains("Chicken").nearestTo(local);
 *         target?.interact("Attack");
 *     }
 * }
 * titan.register(new MyPlugin());
 * ```
 *
 * 2) Object-registration (legacy, still supported):
 * ```ts
 * titan.registerPlugin({ id: "my_plugin", name: "My Plugin", ... });
 * ```
 */

// ---------------------------------------------------------------------------
// Positional types
// ---------------------------------------------------------------------------

interface Tile {
    x: number;
    y: number;
    plane: number;
}
/** RuneLite-style world-space tile coordinate (x, y, plane = z). */
interface WorldPoint {
    x: number;
    y: number;
    z: number;
}
/**
 * Sub-tile precision scene-local coordinate. Mirrors `titan::LocalPoint`
 * in [shared/titan/local_point.h](shared/titan/local_point.h). Stored
 * fields `x` / `y` are in 1/128-tile units (the game's internal scene
 * coord system); `sceneX` / `sceneY` are the tile-granularity
 * indices (`x >> 7` / `y >> 7`). Added in SDK 39.
 *
 * Locatable entity wrappers expose this as `localPoint`. JS plugins may
 * also construct one as a plain object literal.
 */
interface LocalPoint {
    /** Sub-tile units (0..sceneSizeX*128). */
    x: number;
    /** Sub-tile units (0..sceneSizeY*128). */
    y: number;
    /** Scene tile index (x >> 7). Optional; computed by callers. */
    sceneX?: number;
    /** Scene tile index (y >> 7). Optional; computed by callers. */
    sceneY?: number;
}
interface ScreenPoint {
    x: number;
    y: number;
}

interface MenuActionSpec {
    opcode: number;
    /** Menu-entry identifier: entity id or CC_OP sub-action index. */
    identifier: number;
    param0: number;
    param1: number;
    worldViewId?: number;
    /** Omit both coords to randomize the synthetic click on the active game screen. */
    clickX?: number;
    clickY?: number;
    actionText?: string;
    targetText?: string;
    /** Ignored by MenuOptionClicked.replaceWith(); used by invokeMenuAction(). */
    skipClick?: boolean;
}

/** Magic spell metadata descriptor. Added in SDK 49. */
interface MagicSpellInfo {
    readonly name: string;
    readonly level: number;
    readonly widget: number;
    readonly book: 0 | 1 | 2 | 3;
    readonly members: boolean;
    readonly menuEntryId: number;
}
type MagicSpell = MagicSpellInfo;
interface MagicWidgetTarget {
    readonly packedId: number;
    readonly childIndex?: number;
    readonly itemId?: number;
}
type MagicTarget = Npc | Player | GroundItem | TileObject | titan.WidgetState | MagicWidgetTarget;


// ---------------------------------------------------------------------------
// Entity wrappers — fluent methods on live game entities.
// ---------------------------------------------------------------------------

interface ActorSpotAnim {
    readonly slot: number;
    readonly id: number;
    readonly height: number;
    readonly expireCycle: number;
}

interface ActorBase {
    readonly hashIndex: number;
    readonly tileX: number;
    readonly tileY: number;
    readonly plane: number;
    readonly worldX: number;
    readonly worldY: number;
    readonly preciseX: number;
    readonly preciseY: number;
    readonly orientation: number;
    readonly animation: number;
    /** Current movement pose id. Offset bundles still call this MovementState. SDK 73+. */
    readonly movementPose: number;
    /** Idle/rest pose id. Offset bundles still call this IdleState. SDK 73+. */
    readonly idlePose: number;
    readonly interactingIndex: number;
    readonly interactingType: number;
    /** Interaction lifecycle phase (0 = active, non-zero = stale, 0xFF = unavailable). SDK v44+. */
    readonly interactingPhase: number;
    readonly entityPtr: number;

    /** Logical/server tile. For actors this is PathQueue[0] when available. */
    readonly tile: Tile;
    /** Logical/server world point. For actors this is PathQueue[0] plus scene base when available. */
    readonly worldPoint: WorldPoint;
    /** Render/interpolated local point from PreciseX/Y. */
    readonly localPoint: LocalPoint;
    /** Valid actor path queue entries as world points. Index 0 is the logical/server tile. SDK 59+. */
    readonly pathQueue: WorldPoint[];
    /** Active actor-attached spot animations. Empty when unavailable or none are active. SDK 76+. */
    readonly currentSpotAnims: ActorSpotAnim[];

    readonly isPlayer: boolean;
    readonly isNpc: boolean;
    /** No pending movement (`movementPose === idlePose`). A stationary actor can still be animating. SDK 73+. */
    readonly isStationary: boolean;

    /** Cast to Player, or null if not a player. */
    toPlayer(): Player | null;
    /** Cast to Npc, or null if not an NPC. */
    toNpc(): Npc | null;

    /** Chebyshev tile distance. */
    distanceTo(other: Tile | ActorBase): number;

    /** RuneLite-style line of sight from this actor's footprint to another locatable or world point. SDK 52+. */
    hasLineOfSight(other: LineOfSightTarget): boolean;

    /** True when this actor is actively interacting with a target. SDK 54+. */
    isInteracting(): boolean;

    /** Resolve the entity this actor is interacting with, or null. */
    interacting(): Actor | null;
}

interface Player extends ActorBase {
    readonly combatLevel: number;
    readonly isHidden: boolean;
    /** `animation !== -1`. */
    readonly isAnimating: boolean;
    /** Fully idle: `isStationary && !isAnimating`. */
    readonly isIdle: boolean;
    readonly name: string;
    /** Primary overhead icon index, or `-1` when none. SDK 35+. */
    readonly overheadIcon: number;
    /** Skull icon ordinal, or `-1` when not skulled. SDK 35+. */
    readonly skullIcon: number;
    /** True when currently displaying any overhead icon. SDK 35+. */
    isOverheadActive(icon?: number): boolean;
    /** True when `skullIcon >= 0`. SDK 35+. */
    isSkulled(): boolean;
    /** Current headbar fill [0, healthScale], or -1 when no bar. SDK 48+. */
    readonly healthRatio: number;
    /** Max bar width, or -1 when no bar. SDK 48+. */
    readonly healthScale: number;
    /** True when any health bar is active. SDK 48+. */
    readonly hasHealthBar: boolean;
    /** Health as percent [0, 1] or -1 when no bar. SDK 48+. */
    healthPercent(): number;
    /** True when health bar is active and ratio is 0. SDK 48+. */
    isDead(): boolean;
}

interface Npc extends ActorBase {
    readonly id: number;
    readonly overrideTransform: number;
    readonly sizeX: number;
    readonly sizeY: number;
    /** `animation !== -1`. */
    readonly isAnimating: boolean;
    readonly name: string;
    readonly actions: string[];
    /** Primary overhead icon index for this NPC, or `-1` when none. SDK 35+. */
    readonly overheadIcon: number;
    /** True when a per-instance runtime override is currently set. SDK 35+. */
    readonly hasHeadIconOverride: boolean;

    /** Footprint area anchored at this NPC's south-west world point. */
    toWorldArea(): titan.WorldArea;
    hasAction(action: string): boolean;
    /** Dispatch a named action via this NPC's hash index. */
    interact(action: string): boolean;
    /** True when this NPC is currently displaying any overhead icon. SDK 35+. */
    isOverheadActive(icon?: number): boolean;
    /** Current headbar fill [0, healthScale], or -1 when no bar. SDK 48+. */
    readonly healthRatio: number;
    /** Max bar width, or -1 when no bar. SDK 48+. */
    readonly healthScale: number;
    /** True when any health bar is active. SDK 48+. */
    readonly hasHealthBar: boolean;
    /** Health as percent [0, 1] or -1 when no bar. SDK 48+. */
    healthPercent(): number;
    /** True when health bar is active and ratio is 0. SDK 48+. */
    isDead(): boolean;
}

type Actor = Player | Npc;

interface TileObject {
    readonly tileX: number;
    readonly tileY: number;
    readonly plane: number;
    readonly id: number;
    readonly sizeX: number;
    readonly sizeY: number;
    readonly type: string;
    readonly name: string;
    /**
     * Scene layer the loc was picked up from (matches the layer bits
     * in the engine's picking typecode):
     *   0 = Wall, 1 = Decor, 2 = Scenery (standing loc), 3 = GroundDecor.
     * `-1` when the host couldn't classify. Populated by SDK v34+.
     */
    readonly layer: number;
    /** Raw 1-byte TypeCode2 value, or -1 when unavailable. */
    readonly sceneTypecode: number;
    /** Derived scene object type/shape (sceneTypecode & 0x1f), or -1. */
    readonly sceneObjectType: number;
    /** Alias for sceneObjectType. */
    readonly shape: number;
    /** Derived orientation ((sceneTypecode >> 6) & 3), or -1. */
    readonly orientation: number;
    /** Active dynamic scenery animation id, or -1 when static/unavailable. SDK 71+. */
    readonly animation: number;
    readonly actions: string[];
    readonly tile: Tile;
    readonly worldPoint: WorldPoint;
    readonly localPoint: LocalPoint;

    hasAction(action: string): boolean;
    /** RuneLite-style line of sight from this object's footprint to another locatable or world point. SDK 52+. */
    hasLineOfSight(other: LineOfSightTarget): boolean;
    /** RuneLite-style dynamic scenery animation id lookup. SDK 71+. */
    getAnimation(): number;
    /** Dispatch against this exact object instance, preserving its scene tile. */
    interact(action: string): boolean;
}

interface GroundItem {
    readonly tileX: number;
    readonly tileY: number;
    readonly plane: number;
    readonly id: number;
    readonly quantity: number;
    /** See titan.GroundItemOwnership. Unknown/unreadable ownership is `0xFFFFFFFF`. */
    readonly ownershipType: number;
    readonly name: string;
    readonly tile: Tile;
    readonly worldPoint: WorldPoint;
    readonly localPoint: LocalPoint;

    /** True when this ground item is lootable for the current account mode. */
    canLoot(): boolean;

    /** RuneLite-style line of sight from this item tile to another locatable or world point. SDK 52+. */
    hasLineOfSight(other: LineOfSightTarget): boolean;

    /** Dispatch a ground-item action (e.g. "Take", "Examine"). */
    interact(action: string): boolean;
}

interface Item {
    readonly slot: number;
    readonly id: number;
    readonly quantity: number;
    readonly name: string;

    /**
     * Dispatch an inventory action (e.g. "Eat", "Bury", "Drop").
     * Returns true when the action was accepted / queued; inventory or
     * equipment state changes are confirmed later via `onItemContainerChanged`.
     */
    interact(action: string): boolean;

    /**
     * Use this item on another target. Overloaded by the target's
     * runtime shape: inventory items produce the two-packet
     * `WIDGET_TARGET` -> `WIDGET_TARGET_ON_WIDGET` flow (the knife-on-logs
     * case), NPCs select the item then invoke an NPC menu entry,
     * and `TileObject`s select the item then invoke a loc menu entry.
     * Returns `true` when the first selection packet was accepted.
     */
    useOn(target: Item | Npc | TileObject): boolean;
}

interface Projectile {
    readonly plane: number;
    readonly startX: number;
    readonly startZ: number;
    readonly targetX: number;
    readonly targetZ: number;
    /** Decoded source actor hash index, or -1 when no actor source is encoded. */
    readonly sourceEntity: number;
    /** Decoded target actor hash index, or -1 when no actor target is encoded. */
    readonly targetEntity: number;
    /** Raw packed signed game source value. */
    readonly rawSourceEntity: number;
    /** Raw packed signed game target value. */
    readonly rawTargetEntity: number;
    /** EntityType.PLAYER / NPC / NONE for sourceEntity. */
    readonly sourceEntityType: number;
    /** EntityType.PLAYER / NPC / NONE for targetEntity. */
    readonly targetEntityType: number;
    readonly spotAnimId: number;
    readonly startTick: number;
    readonly endTick: number;
    readonly sceneX: number;
    readonly sceneY: number;
    readonly sceneZ: number;
    readonly tileX: number;
    readonly tileY: number;
    readonly worldX: number;
    readonly worldY: number;
    readonly tile: Tile;
    readonly worldPoint: WorldPoint;
    readonly localPoint: LocalPoint;
    readonly yaw: number;
    readonly pitch: number;
    readonly hasMoved: boolean;

    sourceActor(): ActorBase | null;
    targetActor(): ActorBase | null;

    /** RuneLite-style line of sight from this projectile tile to another locatable or world point. SDK 52+. */
    hasLineOfSight(other: LineOfSightTarget): boolean;
}

interface Sequence {
    readonly ptr: bigint;
    readonly id: number;
    readonly flags: number;
    readonly frameCount: number;
    readonly numFrames: number;
    readonly frameIds: bigint;
    readonly frameIDs: bigint;
    readonly frameLengths: bigint;
    readonly totalDuration: number;
    readonly frameStep: number;
    readonly repeatLimit: number;
}

/**
 * Active map-tile spot animation (RuneLite's `GraphicsObject`). Materialised
 * from `WorldView::GraphicsObjectList` and dispatched through
 * `onGraphicsObject{Spawned,Despawned,Moved}`. SDK 57+.
 */
interface GraphicsObject {
    readonly spotAnimId: number;
    readonly startCycle: number;
    readonly plane: number;
    readonly height: number;
    readonly preciseX: number;
    readonly preciseZ: number;
    readonly sceneX: number;
    readonly sceneZ: number;
    readonly tileX: number;
    readonly tileY: number;
    readonly worldX: number;
    readonly worldY: number;
    readonly seqPtr: bigint;
    readonly seqTypePtr: bigint;
    readonly animationId: number;
    readonly frameCycle: number;
    readonly currentFrame: number;
    readonly loopCount: number;
    readonly totalCycle: number;
    readonly animation: Sequence | null;
    readonly tile: Tile;
    readonly worldPoint: WorldPoint;
    readonly localPoint: LocalPoint;

    /** RuneLite-style line of sight from this graphics object's tile to another locatable or world point. */
    hasLineOfSight(other: LineOfSightTarget): boolean;
}

type LineOfSightTarget = WorldPoint | ActorBase | TileObject | GroundItem | Projectile | GraphicsObject | titan.WorldArea;

// ---------------------------------------------------------------------------
// Queries — fluent filtering over collections.
// ---------------------------------------------------------------------------

/**
 * Base query over any collection of entities. Location-independent filters
 * and terminals only — queries over entities that have a tile extend
 * LocatableQuery to unlock `within` / `nearestTo`.
 */
interface Query<T> {
    where(pred: (e: T) => boolean): this;
    when(cond: boolean, fn: (q: this) => void): this;
    /** Sort items using a user-supplied comparator. */
    sortBy(cmp: (a: T, b: T) => number): this;

    count(): number;
    any(): boolean;
    empty(): boolean;
    first(): T | null;
    forEach(fn: (e: T) => void): void;
    toArray(): T[];
}

/** Name filters shared only by entities that expose a name. */
interface NamedQuery<T> extends Query<T> {
    nameContains(needle: string): this;
    nameEquals(name: string): this;
    /** Keep only entries whose name contains any needle in the array
     *  (case-insensitive substring). */
    namesAnyOf(needles: string[]): this;
}

/**
 * Query over locatable entities (things that live on a tile). Adds
 * position-based filtering and nearest-to lookup on top of Query.
 *
 * Both `within` and `nearestTo` accept the same set of origin shapes:
 * - `Tile` / `ActorBase` -- scene-local tile coords (no conversion).
 * - `WorldPoint` -- absolute world coords; converted to scene-local
 *   via the live `baseX` / `baseY` from `titan.state.client.snapshot`.
 *   Falls back to an empty result (`within`) / `null` (`nearestTo`)
 *   when the client snapshot is unavailable.
 * - `LocalPoint` -- sub-tile scene-local coords; round-down via `>> 7`.
 */
interface LocatableQuery<T> extends Query<T> {
    within(radius: number, origin: Tile | ActorBase | WorldPoint | LocalPoint): this;
    nearestTo(origin: Tile | ActorBase | WorldPoint | LocalPoint): T | null;
    /** Nearest entity to the local player, or null. */
    nearest(): T | null;
    /** Keep only entities on the exact scene tile (or x, y pair). */
    onTile(tile: Tile): this;
    onTile(x: number, y: number): this;
    /** Keep only entities at the exact absolute world coordinate. */
    atWorldPoint(wp: WorldPoint): this;
    /** Sort ascending by Chebyshev tile distance from the origin. */
    sortedByDistanceTo(origin: Tile | ActorBase | WorldPoint | LocalPoint): this;
}

interface NamedLocatableQuery<T> extends LocatableQuery<T>, NamedQuery<T> {}

interface NpcQuery extends NamedLocatableQuery<Npc> {
    id(npcId: number): this;
    ids(...ids: number[]): this;
    hasAction(action: string): this;
    /** Keep only NPCs not actively targeted by any other player. */
    notTargetedByOtherPlayers(): this;
    /** Keep only NPCs actively interacting with a specific actor. */
    interactingWith(actor: ActorBase): this;
    /** Keep only NPCs actively interacting with the local player. */
    interactingWithLocal(): this;
    /** Keep only NPCs with no active interaction target. */
    notInteracting(): this;
    /** Keep only NPCs currently playing an animation. */
    isAnimating(): this;
    /** Keep only NPCs not currently animating. */
    notAnimating(): this;
    /** Keep only NPCs playing the exact animation ID. */
    animation(animId: number): this;
    /** Keep only NPCs with no pending movement. SDK 73+. */
    isStationary(): this;
    /** Keep only NPCs with any (or exact) active overhead icon. */
    overheadActive(icon?: number): this;
    /** Keep only NPCs whose overrideTransform matches (morphing NPCs). */
    overrideTransform(transformId: number): this;
    /** Keep only NPCs whose tile footprint equals `s` on both axes. */
    sizeEquals(s: number): this;
    /** Keep only dead NPCs (health bar at 0%). SDK 48+. */
    isDead(): this;
    /** Keep only alive NPCs (no health bar, or ratio > 0). SDK 48+. */
    isAlive(): this;
    /** Keep only NPCs with an active health bar. SDK 48+. */
    withHealthBar(): this;
    /** Keep only NPCs without an active health bar. SDK 48+. */
    noHealthBar(): this;
    /** Keep only NPCs whose health percent is below threshold. SDK 48+. */
    healthPercentBelow(threshold: number): this;
    /** Keep only NPCs whose health percent is above threshold. SDK 48+. */
    healthPercentAbove(threshold: number): this;
}

interface PlayerQuery extends NamedLocatableQuery<Player> {
    /** Keep only players actively interacting with a specific actor. */
    interactingWith(actor: ActorBase): this;
    /** Keep only players actively interacting with the local player. */
    interactingWithLocal(): this;
    /** Keep only players with no active interaction target. */
    notInteracting(): this;
    /** Keep only players currently playing an animation. */
    isAnimating(): this;
    /** Keep only players not currently animating. */
    notAnimating(): this;
    /** Keep only players playing the exact animation ID. */
    animation(animId: number): this;
    /** Keep only players with no pending movement. SDK 73+. */
    isStationary(): this;
    /** Keep only fully idle players (stationary and not animating). */
    isIdle(): this;
    /** Keep only skulled players. */
    isSkulled(): this;
    /** Keep only players with any (or exact) active overhead icon. */
    overheadActive(icon?: number): this;
    /** Keep only players with combat level >= min. */
    combatLevelAbove(min: number): this;
    /** Keep only players with combat level <= max. */
    combatLevelBelow(max: number): this;
    /** Keep only players with combat level in [lo, hi] inclusive. */
    combatLevelBetween(lo: number, hi: number): this;
    excludingSelf(): this;
    /** Keep only dead players (health bar at 0%). SDK 48+. */
    isDead(): this;
    /** Keep only alive players (no health bar, or ratio > 0). SDK 48+. */
    isAlive(): this;
    /** Keep only players with an active health bar. SDK 48+. */
    withHealthBar(): this;
    /** Keep only players without an active health bar. SDK 48+. */
    noHealthBar(): this;
    /** Keep only players whose health percent is below threshold. SDK 48+. */
    healthPercentBelow(threshold: number): this;
    /** Keep only players whose health percent is above threshold. SDK 48+. */
    healthPercentAbove(threshold: number): this;
}

interface ObjectQuery extends NamedLocatableQuery<TileObject> {
    id(locId: number): this;
    ids(...ids: number[]): this;
    hasAction(action: string): this;
    ofType(typeName: string): this;
    /** Keep only objects on the given scene layer (0=Wall, 1=Decor, 2=Scenery, 3=GroundDecor). */
    layer(layerId: number): this;
}

interface GroundItemQuery extends NamedLocatableQuery<GroundItem> {
    id(itemId: number): this;
    ids(...ids: number[]): this;
    minQuantity(n: number): this;
    maxQuantity(n: number): this;
    /** Keep only ground items lootable for the current account mode. */
    canLoot(): this;
}

/// Inventory items have no tile — they extend the plain Query only.
interface InventoryQuery extends NamedQuery<Item> {
    id(itemId: number): this;
    ids(...ids: number[]): this;
    /** Keep only items at the exact inventory slot index. */
    slot(idx: number): this;
    /** Keep only items whose slot is in the supplied set. */
    slotsAnyOf(slots: number[]): this;
    /** Keep only items whose slot lies in `[lo, hi]` (inclusive). */
    slotsBetween(lo: number, hi: number): this;
    /** Keep only items with per-slot quantity >= n. */
    minQuantity(n: number): this;
    /** Keep only items with per-slot quantity <= n. */
    maxQuantity(n: number): this;
    /** Remove items whose id matches any in the array. */
    excludeIds(ids: number[]): this;
    /** Remove items whose name matches any needle (CI substring). */
    excludeNames(names: string[]): this;
    readonly totalQuantity: number;
    readonly exists: boolean;
}

interface ProjectileQuery extends LocatableQuery<Projectile> {
    spotAnim(animId: number): this;
    /** Keep only projectiles targeting the given decoded actor hash index. */
    targetingEntity(entityIndex: number): this;
    /** Keep only projectiles from the given decoded actor hash index. */
    fromEntity(entityIndex: number): this;
    /** Keep only projectiles targeting the exact player/NPC actor. */
    targetingActor(actor: ActorBase): this;
    /** Keep only projectiles from the exact player/NPC actor. */
    fromActor(actor: ActorBase): this;
    /** Keep only projectiles that started on or after the given tick. */
    startedAfterTick(tick: number): this;
    /** Keep only projectiles that end on or before the given tick. */
    endsBeforeTick(tick: number): this;
    /** Keep only projectiles active during the given tick. */
    activeDuring(tick: number): this;
}

/** Filterable collection of active map-tile spot animations. SDK 57+. */
interface GraphicsObjectQuery extends LocatableQuery<GraphicsObject> {
    /** Keep only graphics objects with the given spot-anim definition id. */
    spotAnim(animId: number): this;
    /** Keep only graphics objects on the given floor plane. */
    onPlane(plane: number): this;
    /** Keep only graphics objects that started on or after the given tick. */
    startedAfterTick(tick: number): this;
    /** Keep only graphics objects that started on or before the given tick. */
    startedBeforeTick(tick: number): this;
}

// ---------------------------------------------------------------------------
// Sections & settings
// ---------------------------------------------------------------------------

interface SectionOptions {
    /** Display label. Defaults to the key. */
    name?: string;
    /** Tooltip shown on the collapsing header. */
    description?: string;
    /** Render order — lower values appear first. */
    position?: number;
    /** Start collapsed. Defaults to false. */
    closedByDefault?: boolean;
}

interface Section {
    readonly key: string;
    readonly name: string;
    readonly description: string;
    readonly position: number;
    readonly isClosedByDefault: boolean;
}

interface SettingMetaBase {
    /** Owning section (optional). */
    section?: Section;
    /** Render order within the section. */
    position?: number;
    /** Start hidden. Can be flipped at runtime via setting.isHidden = true. */
    hidden?: boolean;
    /** Tooltip shown on the control. */
    tooltip?: string;
}

interface BoolSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    default: boolean;
}
interface IntSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    default: number;
    min: number;
    max: number;
}

  /** Loaded widget snapshots with retained slot-aware dynamic paths. SDK 64+. */
  interface WidgetQuery extends Query<titan.WidgetState> {
      /** Flat-table matches win over colliding dynamic fallback ids. */
      packedId(id: number): this;
    group(id: number): this;
    /** Filter by packed widget component id. */
    child(id: number): this;
    /** Replace matches with their direct dynamic child at this native slot. */
    slot(index: number): this;
    textContains(text: string): this;
    textEquals(text: string): this;
    isVisible(): this;
    isHidden(): this;
    type(id: number): this;
    contentType(id: number): this;
    itemId(id: number): this;
    /** Replace matches with their non-null direct dynamic children. */
    children(): this;
    /** True when the host clipped a bounded live traversal. */
    readonly truncated: boolean;
}
interface ColorSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    /** RGB color stored as 0xRRGGBB. */
    default: number;
}
interface ComboChoice {
    value: number;
    label: string;
}
interface ComboSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    default: number;
    choices: ComboChoice[];
}
interface StringSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    default: string;
}

interface ProtectedStringSettingInit extends SettingMetaBase {
    key: string;
    name: string;
    default?: string;
}

interface Setting<T> {
    readonly key: string;
    readonly name: string;
    value: T;
    readonly defaultValue: T;
    isHidden: boolean;
    readonly position: number;
    /** Restore the value to defaultValue. */
    reset(): void;
}

// ---------------------------------------------------------------------------
// Events (typed wrappers)
// ---------------------------------------------------------------------------

interface MenuOptionClicked {
    readonly opcode: number;
    /** Menu-entry identifier: entity id or CC_OP sub-action index. */
    readonly identifier: number;
    readonly param0: number;
    readonly param1: number;
    readonly worldViewId: number;
    readonly clickX: number;
    readonly clickY: number;
    readonly actionText: string;
    readonly targetText: string;
    readonly replaced: boolean;
    consumed: boolean;
    /** Shorthand for `consumed = true`. */
    consume(): void;
    /** Replace the DoAction that reaches the game while preserving the original click frame. */
    replaceWith(action: MenuActionSpec): void;
    /** Clear any pending replacement; the original action will run unless consumed. */
    clearReplacement(): void;
}

interface ScriptEvent {
    readonly scriptId: number;
    /** Snapshot of the integer argument stack as a fresh array
     *  (allocates; prefer `arg(i)` for indexed access). */
    args(): number[];
    /** Snapshot of the integer result stack as a fresh array
     *  (allocates; prefer `result(i)` for indexed access). */
    results(): number[];
    arg(i: number, def?: number): number;
    result(i: number, def?: number): number;
}

/** Delivered to `onVarbitChanged` when a varbit's resolved value actually
 * changes (no-op SetVarbit writes are filtered by the host). Added in SDK 21. */
interface VarbitChangedEvent {
    /** Varbit type id (index into the VarBitType cache). */
    readonly varbitId: number;
    /** Resolved value of the varbit before the write. */
    readonly oldValue: number;
    /** Resolved value of the varbit after the write. */
    readonly newValue: number;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
}

/** Delivered to `onChatMessage` for every native chat line added to the
 * chatbox: server-delivered text, local system messages, and plugin-injected
 * lines via `titan.addChatMessage`. Added in SDK 22. */
interface ChatMessageEvent {
    /** Chat type (0=PUBLIC, 2=SERVER, 3=CLAN, 4=TRADE, 99=BROADCAST, ...). */
    readonly type: number;
    /** Sender display name. Empty on system / server messages. */
    readonly name: string;
    /** Rendered chat text (may contain `<col=...>` tags). */
    readonly message: string;
    /** Sender prefix (clan name for clan chat, empty on most types). */
    readonly sender: string;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
}

/** Discriminates the source of a `SoundPlayedEvent`. */
declare const enum SoundKind {
    /** Queued JagFX/wave sound effect (combat, spells, NPCs, area sounds). */
    Synth = 0,
    /** MIDI jingle (level-ups, quests, music stings). */
    Jingle = 1,
}

/** Delivered to `onSoundPlayed` when the native client plays a sound. Covers
 * queued synth sound effects (captured at the queue drain) and MIDI jingles
 * (captured at `PlayJingle`); check `kind`. Set `consumed = true` to mark the
 * event handled and stop later sound handlers in the same dispatch. Current
 * playback suppression is global-only; use
 * `titan.state.audio.playbackDisabled = true` to mute sounds. Added in SDK 69. */
interface SoundPlayedEvent {
    /** Source of the sound (synth sound effect or MIDI jingle). */
    readonly kind: SoundKind;
    /** Synth JagFX id or jingle id. */
    readonly soundId: number;
    /** Synth loop count; -1 for jingles. */
    readonly loops: number;
    /** Jingle duration in ms; -1 for synths. */
    readonly durationMs: number;
    /** Synth packed position/range; -1 for jingles. */
    readonly packedPos: number;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
    /** Set to true to mark this sound event consumed for handler ordering. */
    consumed: boolean;
}

/** Delivered to `onHitsplatApplied` when the native client applies a visible
 * hitsplat to a player or NPC. Added in SDK 74; native signature corrected in
 * SDK 76. */
interface HitsplatAppliedEvent {
    /** Resolved actor object, or null if the actor no longer resolves. */
    readonly actor: Actor | null;
    /** Entity type constant: titan.ENTITY_TYPE_PLAYER, NPC, or NONE. */
    readonly actorType: number;
    /** Lowercase actor kind: "player", "npc", or "none". */
    readonly kind: "player" | "npc" | "none";
    /** Player hash index for players, NPC id for NPCs, or -1 when unresolved. */
    readonly indexOrId: number;
    /** Resolved actor name, or empty when unresolved. */
    readonly actorName: string;
    /** Native hitsplat type id. */
    readonly type: number;
    /** Damage/value payload. */
    readonly value: number;
    /** Alias for value. */
    readonly damage: number;
    /** Native limit field from the real hitsplat adder. */
    readonly limit: number;
    /** Native delay field. */
    readonly delay: number;
    /** Native cycle field. */
    readonly cycle: number;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
}

/** Delivered to `onActorSpotAnim` when the native client applies an actor-attached
 * spot animation. Clear/removal ids are filtered before dispatch. Added in
 * SDK 76. */
interface ActorSpotAnimEvent {
    /** Resolved actor object, or null if the actor no longer resolves. */
    readonly actor: Actor | null;
    /** Entity type constant: titan.ENTITY_TYPE_PLAYER, NPC, or NONE. */
    readonly actorType: number;
    /** Lowercase actor kind: "player", "npc", or "none". */
    readonly kind: "player" | "npc" | "none";
    /** Player hash index for players, NPC id for NPCs, or -1 when unresolved. */
    readonly indexOrId: number;
    /** Resolved actor name, or empty when unresolved. */
    readonly actorName: string;
    /** Native actor spotanim slot. */
    readonly slot: number;
    /** SpotAnim definition id. */
    readonly id: number;
    /** Native spotanim height field. */
    readonly height: number;
    /** Native delay field. */
    readonly delay: number;
    /** Native cycle field. */
    readonly cycle: number;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
}

/** Delivered to `onAnimationChanged` when the native client accepts an actor
 * animation field change. Same-animation resets and rejected native requests
 * are filtered before dispatch. Added in SDK 78. */
interface AnimationChangedEvent {
    /** Resolved actor object, or null if the actor no longer resolves. */
    readonly actor: Actor | null;
    /** Entity type constant: titan.ENTITY_TYPE_PLAYER, NPC, or NONE. */
    readonly actorType: number;
    /** Lowercase actor kind: "player", "npc", or "none". */
    readonly kind: "player" | "npc" | "none";
    /** Player hash index for players, NPC id for NPCs, or -1 when unresolved. */
    readonly indexOrId: number;
    /** Resolved actor name, or empty when unresolved. */
    readonly actorName: string;
    /** Previous raw Actor.Animation id. */
    readonly oldAnimation: number;
    /** New raw Actor.Animation id accepted by the native setter. */
    readonly newAnimation: number;
    /** Current game tick captured at dispatch, or 0 if unavailable. */
    readonly gameTick: number;
}

/** Single occupied slot in an item container snapshot. Added in SDK 26. */
interface ItemContainerSlot {
    readonly slot: number;
    readonly id: number;
    readonly quantity: number;
}

/** Single bank slot with item info. Used by `titan.utils.bank` helpers. */
interface BankItemSlot {
    readonly slot: number;
    readonly itemId: number;
    readonly quantity: number;
}

/** Snapshot of a RuneLite-style item container (INVENTORY=93,
 * EQUIPMENT=94, BANK=95). Empty slots are filtered out. Added in SDK 26. */
interface ItemContainerSnapshot {
    readonly containerId: number;
    readonly capacity: number;
    /** Snapshot of every occupied slot as a fresh array (allocates). */
    items(): ItemContainerSlot[];
}

/** Event wrapper for tick-level diff of an item container. Fires whenever
 * a mapped container's slot contents differ from the previous tick's
 * snapshot. Added in SDK 26. */
interface ItemContainerChangedEvent extends ItemContainerSnapshot {
    readonly gameTick: number;
}

/** Runtime ItemDef snapshot (RuneLite's Client.getItemDefinition parity).
 * When `runtimeResolved` is true the fields came from the native
 * ITEM_DEF_LOOKUP (includes varbit/varp transforms and preserves runtime
 * inventory-action slots, including empty gaps); when false they came from
 * the cache file's raw 5-slot inventory-action array. Added in SDK 26. */
interface ItemComposition {
    readonly id: number;
    readonly name: string;
    readonly stackable: boolean;
    /** -1 when the item has no note variant. */
    readonly linkedNoteId: number;
    /** Inventory-action slots with positional gaps preserved. */
    readonly inventoryActions: string[];
    readonly runtimeResolved: boolean;
}

// ---------------------------------------------------------------------------
// Overlay
// ---------------------------------------------------------------------------

/**
 * Render-pass selector for overlays. Two values matching the host's two
 * draw passes. RuneLite's `UNDER_WIDGETS` / `ABOVE_MAP` aren't mirrored —
 * the host has no equivalent passes. Available at runtime as
 * `titan.OverlayLayer`.
 */
type OverlayLayer = "AboveScene" | "AboveWidgets";

interface OverlayInit {
    layer?: OverlayLayer;
    render: () => void;
}

// ---------------------------------------------------------------------------
// OverlayPanel (SDK 46) -- structured HUD panels with anchor-based layout,
// Alt-drag repositioning, and full theming. Mirrors RuneLite's OverlayPanel.
// ---------------------------------------------------------------------------

/**
 * Anchor positions for OverlayPanel. Only the values with semantics
 * that free positioning can't replicate are exposed -- corner / canvas
 * anchors are intentionally omitted; users free-position into corners
 * via Alt-drag (panels become Dynamic once moved). Available at runtime
 * as `titan.OverlayAnchor`.
 */
type OverlayAnchor =
    | "Dynamic"
    | "TopCenter"
    | "LeftCenter"
    | "RightCenter"
    | "AboveChatboxRight"
    | "Tooltip";

/**
 * Sticky theming for an OverlayPanel. All fields optional -- omitted
 * fields fall back to library defaults.
 */
interface OverlayPanelStyle {
    /** ARGB background colour (0xAARRGGBB). */
    background?: number;
    /** ARGB border colour. Alpha=0 disables the border. */
    borderColor?: number;
    /** Border thickness in pixels. 0 disables the border. */
    borderThickness?: number;
    /** Corner radius in pixels (0 = sharp corners). */
    cornerRadius?: number;
    /** Horizontal padding inside the panel rect. */
    padHorizontal?: number;
    /** Vertical padding inside the panel rect. */
    padVertical?: number;
    /** Vertical gap between component rows. */
    lineGap?: number;
    /** Default ARGB colour for `title()` calls. */
    titleColor?: number;
    /** Default ARGB colour for `line()` left text. */
    lineLeftColor?: number;
    /** Default ARGB colour for `line()` right text. */
    lineRightColor?: number;
    /** Default ARGB fill colour for `progressBar()`. */
    barFillColor?: number;
    /** Default ARGB background colour for `progressBar()`. */
    barBgColor?: number;
}

/**
 * The panel object passed to `render` in `Plugin.overlayPanel`. Provides
 * builder methods that emit components into the host, plus styling
 * setters that update the panel's sticky style.
 */
interface OverlayPanelInstance {
    /** Append a title row. */
    title(text: string, color?: number): OverlayPanelInstance;
    /** Append a label/value line (right text right-aligned). */
    line(left: string, right?: string, leftColor?: number, rightColor?: number): OverlayPanelInstance;
    /** Append a progress bar with [min..max] range. */
    progressBar(value: number, min: number, max: number,
                fillColor?: number, bgColor?: number): OverlayPanelInstance;
    /** Hard-clamps to [80, 600]. */
    setPreferredWidth(px: number): OverlayPanelInstance;
    /** Replace the panel's sticky style. */
    setStyle(style: OverlayPanelStyle): OverlayPanelInstance;
    setBackgroundColor(argb: number): OverlayPanelInstance;
    setBorderColor(argb: number): OverlayPanelInstance;
    setBorderThickness(px: number): OverlayPanelInstance;
    setCornerRadius(px: number): OverlayPanelInstance;
    setPadding(horizontal: number, vertical: number): OverlayPanelInstance;
    setLineGap(px: number): OverlayPanelInstance;
    setTitleColor(argb: number): OverlayPanelInstance;
    setLineColors(leftArgb: number, rightArgb: number): OverlayPanelInstance;
    setProgressBarColors(fillArgb: number, bgArgb: number): OverlayPanelInstance;
}

interface OverlayPanelInit {
    /** Stable identifier within this plugin (used as the layout key). */
    name: string;
    /** Anchor position. Defaults to "Dynamic". */
    anchor?: OverlayAnchor;
    /** Stack ordering within an anchor group. Lower priority renders first. */
    priority?: number;
    /** Optional starting style. Equivalent to calling `panel.setStyle(...)`. */
    style?: OverlayPanelStyle;
    /** Width hint in pixels. Defaults to 220. */
    preferredWidth?: number;
    /**
     * Per-frame render callback. Receives the panel object so the
     * callback can call `p.title(...)` / `p.line(...)` / etc.
     */
    render: (panel: OverlayPanelInstance) => void;
}

// ---------------------------------------------------------------------------
// Plugin base class (class-based authoring, recommended)
// ---------------------------------------------------------------------------

interface PluginHandle {
    readonly id: string;
    readonly name: string;
    /** True when the host still knows this plugin id. */
    readonly isValid: boolean;
    readonly isEnabled: boolean;
    readonly hasPanel: boolean;
    enable(): boolean;
    disable(): boolean;
    toggle(): boolean;
    /** Direct enable/disable setter. Returns true when accepted by the host. */
    setEnabled(v: boolean): boolean;
}

/**
 * Extend titan.Plugin, declare setting / section / overlay members via the
 * helper methods (this.boolSetting, this.section, this.overlay, ...) and
 * override the lifecycle methods you care about.
 */
declare class TitanPlugin {
    id: string;
    name: string;
    /**
     * Side panels exposed by this plugin. Each entry becomes its own nav
     * button in the controller's right-hand rail. A plugin may declare
     * several. Replaces the SDK <= 64 singular `hasPanel` / `panelTitle` /
     * `buildPanel` / `onPanelAction` model.
     */
    panels?: PanelDef[];
    readonly isEnabled: boolean;
    /** One-line description shown as a tooltip in the plugin list. */
    description?: string;
    /** Plugin author name. */
    author?: string;
    /** Short version string (e.g. "1.0.0"). */
    version?: string;
    /** Default enabled state for first install; runtime starts disabled until the controller applies saved/default state. */
    enabled?: boolean;
    /** Legacy single-overlay layer. Prefer `overlay({ layer, render })` for multiple overlays. */
    renderLayer?: OverlayLayer;
    /** Legacy single-overlay callback. Prefer `overlay({ layer, render })` for new plugins. */
    renderOverlay?(): void;

    // Setting helpers — each returns a Setting<T> that auto-registers.
    boolSetting(init: BoolSettingInit): Setting<boolean>;
    intSetting(init: IntSettingInit): Setting<number>;
    colorSetting(init: ColorSettingInit): Setting<number>;
    comboSetting(init: ComboSettingInit): Setting<number>;
    stringSetting(init: StringSettingInit): Setting<string>;
    protectedStringSetting(init: ProtectedStringSettingInit): Setting<string>;

    // Section helper — auto-registers.
    section(key: string, name: string, opts?: SectionOptions): Section;

    // Overlay helper — auto-registers. Multiple allowed per plugin.
    overlay(init: OverlayInit): OverlayInit;

    /**
     * OverlayPanel helper -- auto-registers a structured HUD panel with
     * anchor-based layout and Alt-drag repositioning. Multiple allowed
     * per plugin. Returns the panel instance so styling setters can be
     * chained onto the registration call. Added in SDK 46.
     */
    overlayPanel(init: OverlayPanelInit): OverlayPanelInstance;

    // Lifecycle hooks — override as needed.
    onEnable?(): void;
    onDisable?(): void;
    onClientTick?(): void;
    onGameTick?(tick: number): void;
    onSettingChanged?(key: string): void;

    onMenuOptionClicked?(event: MenuOptionClicked): void;
    onScriptFired?(event: ScriptEvent): void;
    /** Fired when a varbit's resolved value actually changes. Added in SDK 21. */
    onVarbitChanged?(event: VarbitChangedEvent): void;
    /** Fired for every chat line added to the chatbox (server + local + injected). Added in SDK 22. */
    onChatMessage?(event: ChatMessageEvent): void;
    /** Fired when the native client plays a sound (synth effect or jingle; see
     * `event.kind`). Set `event.consumed = true` to stop later sound handlers;
     * use `titan.state.audio.playbackDisabled` for playback suppression. Added
     * in SDK 69. */
    onSoundPlayed?(event: SoundPlayedEvent): void;
    /** Fired when a visible hitsplat is applied to a resolved actor. Added in SDK 74. */
    onHitsplatApplied?(event: HitsplatAppliedEvent): void;
    /** Fired when an actor-attached spot animation is applied. Added in SDK 76. */
    onActorSpotAnim?(event: ActorSpotAnimEvent): void;
    /** Fired when an actor animation field actually changes. Added in SDK 78. */
    onAnimationChanged?(event: AnimationChangedEvent): void;
    /** Fired when a mapped item container's slot contents differ from the
     * previous tick. Detection is tick-level diff. Added in SDK 26. */
    onItemContainerChanged?(event: ItemContainerChangedEvent): void;

    onNpcSpawned?(npc: Npc): void;
    onNpcDespawned?(npc: Npc): void;
    onPlayerSpawned?(player: Player): void;
    onPlayerDespawned?(player: Player): void;
    onTileObjectSpawned?(obj: TileObject): void;
    onTileObjectDespawned?(obj: TileObject): void;
    onProjectileSpawned?(proj: Projectile): void;
    onProjectileDespawned?(proj: Projectile): void;
    onProjectileMoved?(proj: Projectile): void;
    onGraphicsObjectSpawned?(g: GraphicsObject): void;
    onGraphicsObjectDespawned?(g: GraphicsObject): void;
    onGraphicsObjectMoved?(g: GraphicsObject): void;
}

// ---------------------------------------------------------------------------
// Panel builder
// ---------------------------------------------------------------------------

/**
 * One side panel definition. Supply via `panels` on a TitanPlugin or
 * PluginDefinition. A plugin may declare multiple panels; each gets its own
 * nav button in the controller side rail.
 */
interface PanelDef {
    /** Stable per-plugin id used to route panel content and actions. */
    id: string;
    /** Display title shown on the nav button tooltip / header. */
    title: string;
    /**
     * Optional Font Awesome glyph (UTF-8 string, e.g. "\uf013") for the nav
     * button. Ignored when `image` is set.
     */
    icon?: string;
    /**
     * Optional custom image icon as a base64-encoded PNG (RuneLite-style).
     * A `data:image/png;base64,` prefix is allowed. Takes precedence over
     * `icon`.
     */
    image?: string;
    /** Build the panel's contents (called whenever the panel is visible). */
    build(panel: Panel): void;
    /** Handle a control interaction inside this panel. */
    onAction?(actionId: number, value: SettingValue): void;
}

declare enum PanelTone {
    neutral = 0,
    accent = 1,
    success = 2,
    warning = 3,
    danger = 4,
    info = 5,
}

declare enum PanelButtonStyle {
    normal = 0,
    primary = 1,
    secondary = 2,
    danger = 3,
    ghost = 4,
}

declare enum PanelInputFlags {
    none = 0,
    password = 1,
    multiline = 2,
}

interface Panel {
    text(s: string): Panel;
    wrapped(s: string): Panel;
    disabled(s: string): Panel;
    bullet(s: string): Panel;
    colored(s: string, color: number): Panel;
    status(s: string, tone?: PanelTone): Panel;
    label(label: string, value: string): Panel;

    separator(): Panel;
    separatorText(s: string): Panel;
    section(s: string): Panel;
    spacing(): Panel;
    sameLine(offset?: number, spacing?: number): Panel;
    newLine(): Panel;
    indent(width?: number): Panel;
    unindent(width?: number): Panel;
    dummy(w: number, h: number): Panel;

    button(label: string, actionId: number): Panel;
    button(label: string, actionId: number, style: PanelButtonStyle, width?: number, height?: number): Panel;
    smallButton(label: string, actionId: number): Panel;
    smallButton(label: string, actionId: number, style: PanelButtonStyle): Panel;
    primaryButton(label: string, actionId: number, width?: number, height?: number): Panel;
    secondaryButton(label: string, actionId: number, width?: number, height?: number): Panel;
    dangerButton(label: string, actionId: number, width?: number, height?: number): Panel;
    selectable(label: string, actionId: number, selected?: boolean): Panel;
    checkbox(label: string, actionId: number, value: boolean): Panel;
    sliderInt(label: string, actionId: number, value: number, min: number, max: number): Panel;
    sliderFloat(label: string, actionId: number, value: number, min: number, max: number): Panel;
    inputText(label: string, actionId: number, value: string): Panel;
    inputText(label: string, actionId: number, value: string, flags: PanelInputFlags): Panel;
    inputText(label: string, actionId: number, submitActionId: number, value: string): Panel;
    inputText(label: string, actionId: number, submitActionId: number, value: string, flags: PanelInputFlags): Panel;
    inputPassword(label: string, actionId: number, value: string): Panel;
    inputPassword(label: string, actionId: number, submitActionId: number, value: string): Panel;

    progress(fraction: number, overlay?: string): Panel;
    collapsing(label: string): Panel;
    beginCollapsible(label: string, defaultOpen?: boolean): Panel;
    endCollapsible(): Panel;
    treeNode(label: string): Panel;
    treePop(): Panel;
    beginTable(id: string, columns: number, flags?: number): Panel;
    endTable(): Panel;
    tableNextRow(): Panel;
    tableNextColumn(): Panel;
    tableSetupColumn(label: string, flags?: number, width?: number): Panel;
    tableHeadersRow(): Panel;
    tooltip(t: string): Panel;
    push(type: number, text: string): PanelElement;
}

interface SettingValue {
    type: "boolean" | "integer" | "string";
    boolValue?: boolean;
    intValue?: number;
    stringValue?: string;
}

// ---------------------------------------------------------------------------
// Legacy object-registration types (still supported)
// ---------------------------------------------------------------------------

interface PluginSettingBase {
    key: string;
    name: string;
    section?: string;
    tooltip?: string;
    position?: number;
    hidden?: boolean;
}

interface BooleanSetting extends PluginSettingBase {
    type: "checkbox";
    value: boolean;
    default?: boolean;
}

interface SliderSetting extends PluginSettingBase {
    type: "slider";
    value: number;
    min: number;
    max: number;
    default?: number;
}

interface ColorSetting extends PluginSettingBase {
    type: "color";
    /** RGB color stored as 0xRRGGBB. */
    value: number;
    default?: number;
}

interface ComboOption {
    value: number;
    label: string;
}

interface ComboSetting extends PluginSettingBase {
    type: "combo";
    value: number;
    options: ComboOption[];
    default?: number;
}

interface TextSetting extends PluginSettingBase {
    type: "text";
    value: string;
    default?: string;
}

type PluginSetting = BooleanSetting | SliderSetting | ColorSetting | ComboSetting | TextSetting;

interface PluginSection {
    key: string;
    name: string;
    description?: string;
    position?: number;
    closedByDefault?: boolean;
}

interface PluginDefinition {
    id: string;
    name: string;
    /** Default enabled state for first install; runtime starts disabled until the controller applies saved/default state. */
    enabled?: boolean;
    /**
     * Side panels exposed by this plugin (each becomes its own nav button).
     * Replaces the SDK <= 64 singular `hasPanel` / `panelTitle` /
     * `getPanelElements` / `onPanelAction` fields.
     */
    panels?: PanelDef[];
    /** One-line description shown as a tooltip in the plugin list. */
    description?: string;
    /** Plugin author name. */
    author?: string;
    /** Short version string (e.g. "1.0.0"). */
    version?: string;
    renderLayer?: OverlayLayer;
    settings?: PluginSetting[];
    sections?: PluginSection[];

    onClientTick?(): void;
    onGameTick?(tick: number): void;
    renderOverlay?(): void;

    onProjectileSpawned?(proj: Projectile): void;
    onProjectileDespawned?(proj: Projectile): void;
    onProjectileMoved?(proj: Projectile): void;
    onGraphicsObjectSpawned?(g: GraphicsObject): void;
    onGraphicsObjectDespawned?(g: GraphicsObject): void;
    onGraphicsObjectMoved?(g: GraphicsObject): void;
    onNpcSpawned?(npc: Npc): void;
    onNpcDespawned?(npc: Npc): void;
    onPlayerSpawned?(player: Player): void;
    onPlayerDespawned?(player: Player): void;
    onTileObjectSpawned?(obj: TileObject): void;
    onTileObjectDespawned?(obj: TileObject): void;
    onMenuOptionClicked?(event: MenuOptionClicked): void;
    onScriptFired?(event: ScriptEvent): void;
    /** Fired when a varbit's resolved value actually changes. Added in SDK 21. */
    onVarbitChanged?(event: VarbitChangedEvent): void;
    /** Fired for every chat line added to the chatbox. Added in SDK 22. */
    onChatMessage?(event: ChatMessageEvent): void;
    /** Fired when the native client plays a sound (synth effect or jingle; see
     * `event.kind`). Set `event.consumed = true` to stop later sound handlers;
     * use `titan.state.audio.playbackDisabled` for playback suppression. Added
     * in SDK 69. */
    onSoundPlayed?(event: SoundPlayedEvent): void;
    /** Fired when a visible hitsplat is applied to a resolved actor. Added in SDK 74. */
    onHitsplatApplied?(event: HitsplatAppliedEvent): void;
    /** Fired when an actor-attached spot animation is applied. Added in SDK 76. */
    onActorSpotAnim?(event: ActorSpotAnimEvent): void;
    /** Fired when an actor animation field actually changes. Added in SDK 78. */
    onAnimationChanged?(event: AnimationChangedEvent): void;
    /** Fired when a mapped item container's slot contents differ from the
     * previous tick's snapshot. Added in SDK 26. */
    onItemContainerChanged?(event: ItemContainerChangedEvent): void;

    settingValues?: Record<string, any>;
}

interface PanelElement {
    type: number;
    text?: string;
    textSecondary?: string;
    color?: number;
    actionId?: number;
    intVal?: number;
    intVal2?: number;
    intVal3?: number;
    floatVal?: number;
    floatVal2?: number;
    floatVal3?: number;
    widthVal?: number;
    heightVal?: number;
    boolVal?: boolean;
}

// ---------------------------------------------------------------------------
// titan global namespace — fluent facades + legacy free functions.
// ---------------------------------------------------------------------------

declare namespace titan {
    // Entity-type constants (for legacy callers).
    const ENTITY_TYPE_NPC: number;
    const ENTITY_TYPE_PLAYER: number;
    const ENTITY_TYPE_NONE: number;

    /**
     * Render-pass selector for `Plugin.overlay({ layer: ... })`. Two
     * values matching the host's draw passes (`ABOVE_SCENE` /
     * `ABOVE_WIDGETS`). RuneLite's `UNDER_WIDGETS` / `ABOVE_MAP` are
     * intentionally not mirrored — the host has no equivalent passes.
     */
    const OverlayLayer: {
        readonly ABOVE_SCENE: "AboveScene";
        readonly ABOVE_WIDGETS: "AboveWidgets";
    };

    const PanelTone: {
        readonly neutral: PanelTone.neutral;
        readonly accent: PanelTone.accent;
        readonly success: PanelTone.success;
        readonly warning: PanelTone.warning;
        readonly danger: PanelTone.danger;
        readonly info: PanelTone.info;
    };

    const PanelButtonStyle: {
        readonly normal: PanelButtonStyle.normal;
        readonly primary: PanelButtonStyle.primary;
        readonly secondary: PanelButtonStyle.secondary;
        readonly danger: PanelButtonStyle.danger;
        readonly ghost: PanelButtonStyle.ghost;
    };

    const PanelInputFlags: {
        readonly none: PanelInputFlags.none;
        readonly password: PanelInputFlags.password;
        readonly multiline: PanelInputFlags.multiline;
    };

    /**
     * Anchor positions for `Plugin.overlayPanel({ anchor: ... })`.
     * Minimal semantic set -- corner anchors are intentionally omitted
     * because users free-position into those areas via Alt-drag.
     * Added in SDK 46.
     */
    const OverlayAnchor: {
        readonly Dynamic:           "Dynamic";
        readonly TopCenter:         "TopCenter";
        readonly LeftCenter:        "LeftCenter";
        readonly RightCenter:       "RightCenter";
        readonly AboveChatboxRight: "AboveChatboxRight";
        readonly Tooltip:           "Tooltip";
    };

    /**
     * Axis-aligned rectangle in absolute world-tile space. Mirrors
     * `titan::WorldArea` in
     * [shared/titan/world_area.h](shared/titan/world_area.h). Pure JS
     * class — methods are arithmetic over `x` / `y` / `width` /
     * `height` / `plane`, so no host call is needed. Construct with
     * `new titan.WorldArea(x, y, w, h, plane)`. Added in SDK 39.
     */
    class WorldArea {
        readonly x: number;
        readonly y: number;
        readonly width: number;
        readonly height: number;
        readonly plane: number;

        constructor(x: number, y: number, width: number, height: number, plane: number);

        /** Plane-aware containment test. */
        contains(p: WorldPoint): boolean;
        /** Plane-ignoring containment test. */
        contains2D(p: WorldPoint): boolean;
        /** South-west-corner-anchored centre tile of this area. */
        center(): WorldPoint;
        /**
         * Chebyshev distance from the closest edge of this area to `p`.
         * Returns `Number.MAX_SAFE_INTEGER` when planes differ.
         */
        distanceTo(p: WorldPoint): number;
        /** RuneLite-style line of sight from this area to another area, locatable, or world point. SDK 52+. */
        hasLineOfSight(other: LineOfSightTarget): boolean;
    }

    // --- Geometry helpers (SDK 39) ---
    // Mirror the inline methods on `titan::WorldPos` in C++. Operate
    // over plain `WorldPoint` interface values returned from entity
    // wrappers. Plane-aware where relevant.

    /** RuneLite-style 6-bit-by-6-bit region id: `((x >> 6) << 8) | (y >> 6)`. */
    function regionId(p: WorldPoint): number;
    /** X coordinate within the 64x64 region (`x & 63`). */
    function regionX(p: WorldPoint): number;
    /** Y coordinate within the 64x64 region (`y & 63`). */
    function regionY(p: WorldPoint): number;
    /**
     * Chebyshev distance between two world points or scene tiles.
     * Returns `Number.MAX_SAFE_INTEGER` when planes differ for
     * `WorldPoint` arguments.
     */
    function distance(a: WorldPoint | Tile, b: WorldPoint | Tile): number;

    // Class-based registration.
    const Plugin: typeof TitanPlugin;
    function register(plugin: TitanPlugin): void;

    // Legacy object-based registration (still supported).
    function registerPlugin(definition: PluginDefinition): void;

    // Logging.
    function log(message: string): void;
    /**
     * printf-style one-line log. Supports `%s %d %i %f %x %%`. Missing
     * arguments are left as the raw specifier.
     * @example titan.logf("hp=%d tick=%d", hp, tick);
     */
    function logf(fmt: string, ...args: unknown[]): void;

    /**
     * Query factories — each returns a freshly materialised, chainable
     * query over live game state. Each domain exposes relevant filters, such
     * as `.nameContains(...)`, `.within(...)`, or widget `.textContains(...)`.
     * Consume with terminals such as `.first()`, `.forEach(...)`,
     * `.toArray()`, `.count()`, and `.any()`.
     */
    namespace queries {
        function npcs(): NpcQuery;
        function players(): PlayerQuery;
        function objects(radius?: number): ObjectQuery;
        function groundItems(radius?: number): GroundItemQuery;
        function inventory(): InventoryQuery;
        function projectiles(): ProjectileQuery;
        /** Active map-tile spot animations (GraphicsObject). SDK 57+. */
        function graphicsObjects(): GraphicsObjectQuery;
        /** Loaded widgets plus recursively reachable dynamic descendants. SDK 64+. */
        function widgets(groupId?: number): WidgetQuery;
    }

    /**
     * Subsystem state and actions. Each member is a const object that
     * reads (and sometimes writes) a single subsystem. Plugin authors
     * dot into these — no chaining, no factories.
     */
    namespace state {
        const client: {
            readonly tick: number;
            readonly plane: number;
            readonly playerCount: number;
            /** Scene base X in absolute world tile coordinates. */
            readonly baseX: number;
            /** Scene base Y in absolute world tile coordinates. */
            readonly baseY: number;
            /** Loaded scene width in tiles. SDK 52+. */
            readonly sceneSizeX: number;
            /** Loaded scene height in tiles. SDK 52+. */
            readonly sceneSizeY: number;
            readonly loggedIn: boolean;
            /** Run energy (0-10000). Divide by 100 for the orb percentage. */
            readonly runEnergy: number;
            /** Player weight in kg (signed; negative with weight-reducing gear). */
            readonly weight: number;
            /** Raw account type varbit (`titan.Varbits.ACCOUNT_TYPE`). */
            readonly accountType: number;
            /** True for ironman account modes, including group variants. */
            readonly isIronman: boolean;
            /** Alias for `isIronman`. */
            readonly isIronMan: boolean;
            /** True for GIM / HCGIM / UGIM account modes. */
            readonly isGroupIronman: boolean;
            /** Alias for `isGroupIronman`. */
            readonly isGroupIronMan: boolean;
            readonly localPlayer: Player | null;
            /**
             * Dispatch a fully-specified menu-action entry. Mirrors
             * `titan::ClientFacade::invokeMenuAction(...)` in C++.
             * Omitted click coordinates are randomized on the active
             * game screen.
             * Set `skipClick = true` to suppress the synthetic click phase
             * in both packet and WndProc modes. The DoAction still fires
             * directly, but the player does not move toward the click target.
             */
            invokeMenuAction(action: MenuActionSpec): boolean;
        };

        const camera: {
            readonly yaw: number;
            readonly pitch: number;
            readonly zoom: number;
            readonly isValid: boolean;
            readonly viewportW: number;
            readonly viewportH: number;
            /** World-space camera X position (sub-tile units). Added in SDK 39. */
            readonly posX: number;
            /** World-space camera Y position (sub-tile units). Added in SDK 39. */
            readonly posY: number;
            /** World-space camera Z (height) position. Added in SDK 39. */
            readonly posZ: number;
        };

        /** Entity hiding (render-function overrides). */
        const hider: {
            players: boolean;
            npcs: boolean;
            self: boolean;
            scene: boolean;
        };

        /** Global sound playback suppression. When true, the sound hooks
         * still fire `onSoundPlayed` but skip the native playback call (synth
         * entries are dropped from the queue; jingles are not played).
         * Added in SDK 69. */
        const audio: {
            playbackDisabled: boolean;
        };
    }

    // Cache definition lookups.
    interface ItemDef {
        id: number;
        name: string;
        isMembers: boolean;
        stackable: boolean;
        noted: boolean;
        noteId: number;
        linkedId: number;
        inventoryActions: string[];
        groundActions: string[];
    }
    interface NpcDef {
        id: number;
        name: string;
        combatLevel: number;
        size: number;
        actions: string[];
        transformVarbit: number;
        transformVarp: number;
        transformDefault: number;
    }
    interface ObjDef {
        id: number;
        name: string;
        actions: string[];
        sizeX: number;
        sizeY: number;
        blocksMovement: boolean;
        transformVarbit: number;
        transformVarp: number;
        transformDefault: number;
    }
    interface VarbitDef {
        id: number;
        varpIndex: number;
        lowBit: number;
        highBit: number;
        /**
         * Where the `{varpIndex, lowBit, highBit}` triple came from.
         * `"live"`: the game's in-memory VarBitType hash cache (always
         * fresh; only populated for varbits loaded this session).
         * `"native"`: decoded via the native GET_VARBIT round-trip
         * (reserved for future flows; not produced today).
         * `"disk"`: read from the JS5 disk cache as a fallback when the
         * live entry isn't loaded; may be stale if the local
         * `main_file_cache.dat2` hasn't been refreshed.
         * Added in SDK 58.
         */
        source: 'live' | 'native' | 'disk';
    }
    namespace state {
        const cache: {
            item(id: number): ItemDef | null;
            npc(id: number): NpcDef | null;
            obj(id: number): ObjDef | null;
            varbit(id: number): VarbitDef | null;
        };

        /**
         * Raw var reads. Skill / prayer queries live on
         * `titan.state.skills` / `titan.state.prayers` (SDK 39+).
         *
         * `varbit(id)` is self-healing (SDK 58+): if the game hasn't
         * loaded the VarBitType into its in-memory cache yet (typical
         * for out-of-area minigame varbits), the host falls back to the
         * native GET_VARBIT call (which JS5-loads the type on demand)
         * and finally to a disk-cache extract. Returns `-1` only when
         * every source is unavailable. The first out-of-area read for a
         * given varbit may incur a one-time JS5 load.
         */
        const vars: {
            varbit(id: number): number;
            varp(id: number): number;
            /** Read a client-side integer variable, or null when unavailable. */
            varClientInt(id: number): number | null;
            /** Queue a client-side integer write for the next client tick. */
            setVarClientInt(id: number, value: number): boolean;
            /** Read a client-side string variable, or null when unavailable. */
            varClientString(id: number): string | null;
            /** Queue a client-side string write for the next client tick. */
            setVarClientString(id: number, value: string): boolean;
            /** Read an optional 64-bit client-side variable without precision loss. */
            varClientLong(id: number): bigint | null;
            /** Queue an optional 64-bit client-side write for the next client tick. */
            setVarClientLong(id: number, value: bigint): boolean;
        };

        /**
         * Skill levels and experience. Mirrors `titan::state::skills()`
         * in C++. Pass `titan.Skill.*` ordinals (added in SDK 39) or
         * raw ints.
         */
        const skills: {
            /** Current (boosted) level after temporary modifiers. */
            boosted(skill: number): number;
            /** Base level before any boost / drain. */
            real(skill: number): number;
            /** Total experience for the skill. */
            experience(skill: number): number;
        };

        /**
         * Active-prayer queries. Mirrors `titan::state::prayers()` in
         * C++. Pass `titan.Prayer.*` ordinals (added in SDK 39) or raw
         * ints.
         */
        const prayers: {
            isActive(prayer: number): boolean;
        };
    }

    // CS2 script runner + quest state.
    interface Cs2Result {
        success: boolean;
        ints: number[];
    }
    namespace state {
        const script: {
            run(scriptId: number, intArgs?: number[]): Cs2Result | null;
            runAndGetInt(scriptId: number, intArgs?: number[]): number | null;
            questState(questId: number): number;
        };
    }

    /**
     * Static composition helpers. Each utility namespace is a flat
     * collection of pure functions / accessors built on top of
     * `titan.state.*` and `titan.queries.*`. No chaining, no setup —
     * just call.
     */
    namespace utils {
        /**
         * Inventory state, query, and action helpers. Composes
         * `titan.queries.inventory()` and `titan.state.widgets.find(...)`.
         * Added in SDK 41.
         */
        const inventory: {
            /** True when the inventory tab widget is currently visible. */
            readonly isOpen: boolean;
            /** True when every inventory slot is occupied. */
            readonly isFull: boolean;
            /** True when no inventory slot is occupied. */
            readonly isEmpty: boolean;
            /** Number of occupied slots. */
            readonly size: number;
            /** Number of empty slots (`28 - size`). */
            readonly emptySlots: number;
            /** Standard inventory capacity (28). */
            readonly capacity: number;
            /** Snapshot every occupied slot. */
            getAll(): Item[];
            /** First slot whose item id matches @p query (number) or
             *  whose display name contains @p query (case-insensitive
             *  substring when string). Returns null when no match. */
            find(query: number | string): Item | null;
            /** Item occupying @p slot (0..27), or null when empty. */
            getSlot(slot: number): Item | null;
            /** Every slot whose item id appears in @p ids. Order
             *  matches slot index, not @p ids. */
            getByIds(ids: number[]): Item[];
            /** Every slot whose display name contains any needle in
             *  @p names (case-insensitive substring). */
            getByNames(names: string[]): Item[];
            /** True when @p query occupies a slot. When @p query is a
             *  number and @p minQty is provided, requires combined
             *  quantity ≥ @p minQty (useful for stackables). */
            contains(query: number | string, minQty?: number): boolean;
            /** Combined quantity of slots whose id matches the
             *  argument; pass an array to sum across multiple ids. */
            count(idOrIds: number | number[]): number;
            /** True when ANY entry in @p arr (mixed ids / names) is
             *  in the inventory. */
            containsAny(arr: Array<number | string>): boolean;
            /** True when EVERY entry in @p arr (mixed ids / names) is
             *  in the inventory. */
            containsAll(arr: Array<number | string>): boolean;
            /** Drop the first slot matching @p query (item id when
             *  number, name substring when string). */
            drop(query: number | string): boolean;
        };

        /**
         * Dialogue / continue-prompt / quest-scroll helpers. Mirrors
         * `titan::utils::Dialogue::*` from
         * [shared/titan/utils/dialogue.h](shared/titan/utils/dialogue.h).
         * Added in SDK 39.
         */
        const dialogue: {
        /**
         * Click the active "click here to continue" widget (level-up,
         * NPC dialogue continue, minigame dialog, tutorial-island
         * prompt, ...). @returns true when a continue widget was found
         * and the click was queued.
         */
        continueDialogue(): boolean;
        /** True when a continue prompt or a multi-option dialog is visible. */
        readonly inDialogue: boolean;
        /** Click the "Make" button when the make-X interface is open. */
        continueMake(): boolean;
        /** True when the quest-completion scroll is visible. */
        readonly isQuestCompletionOpen: boolean;
        /** Click the close button on the quest-completion scroll. */
        closeQuestCompletion(): boolean;
        /**
         * Packed widget id of the active continue prompt, or 0 when
         * no continue widget is up.
         */
        readonly continueWidgetPackedId: number;
        /**
         * @returns true when the multi-option dialog is visible and at
         *          least one option contains any of `needles` (case-
         *          insensitive substring).
         */
        hasOption(needles: string[]): boolean;
        /**
         * Click the first dialog option whose text matches any of
         * `needles` (case-insensitive substring). Tries needles in
         * order; the first matching option wins.
         * @returns true when a matching option was clicked.
         */
        selectOption(needles: string[]): boolean;
        /**
         * Convenience: click a matching dialog option if one is up,
         * otherwise advance a continue widget. Mirrors RuneLite's
         * `handleDialogue(String...)` helper.
         */
        handleDialogue(needles: string[]): boolean;
        };

        /**
         * Combat-orb helpers: special-attack toggle + auto-retaliate.
         * Mirrors `titan::utils::Combat::*` from
         * [shared/titan/utils/combat.h](shared/titan/utils/combat.h).
         * Added in SDK 39.
         */
        const combat: {
            /**
             * Click the special-attack orb through the normal synthetic click path.
             * `skipMovement` is retained for compatibility and does not suppress clicks.
             */
            enableSpecialAttack(skipMovement?: boolean): boolean;
            /** Special-attack energy as a percentage (0..100). */
            readonly specialAttackPercentage: number;
            /** True when the special-attack toggle is currently armed. */
            readonly isSpecialAttackEnabled: boolean;
            /** True when auto-retaliate is currently enabled. */
            readonly isAutoRetaliateEnabled: boolean;
            /** Toggle auto-retaliate. No-op (returns true) when already in the requested state. */
            setAutoRetaliate(enabled: boolean): boolean;
        };

        /**
         * Magic spell metadata and intentionally stubbed action shape.
         * Added in SDK 49; select/cast/castOn currently return false.
         */
        const magic: {
            readonly SpellBook: { readonly Standard: 0; readonly Ancient: 1; readonly Lunar: 2; readonly Necromancy: 3; };
            readonly Standard: {
                readonly HOME_TELEPORT: MagicSpell;
                readonly VARROCK_TELEPORT: MagicSpell;
                readonly LUMBRIDGE_TELEPORT: MagicSpell;
                readonly FALADOR_TELEPORT: MagicSpell;
                readonly TELEPORT_TO_HOUSE: MagicSpell;
                readonly CAMELOT_TELEPORT: MagicSpell;
                readonly ARDOUGNE_TELEPORT: MagicSpell;
                readonly WATCHTOWER_TELEPORT: MagicSpell;
                readonly TROLLHEIM_TELEPORT: MagicSpell;
                readonly TELEPORT_TO_APE_ATOLL: MagicSpell;
                readonly TELEPORT_TO_KOUREND: MagicSpell;
                readonly TELEOTHER_LUMBRIDGE: MagicSpell;
                readonly TELEOTHER_FALADOR: MagicSpell;
                readonly TELEPORT_TO_BOUNTY_TARGET: MagicSpell;
                readonly TELEOTHER_CAMELOT: MagicSpell;
                readonly WIND_STRIKE: MagicSpell;
                readonly WATER_STRIKE: MagicSpell;
                readonly EARTH_STRIKE: MagicSpell;
                readonly FIRE_STRIKE: MagicSpell;
                readonly WIND_BOLT: MagicSpell;
                readonly WATER_BOLT: MagicSpell;
                readonly EARTH_BOLT: MagicSpell;
                readonly FIRE_BOLT: MagicSpell;
                readonly WIND_BLAST: MagicSpell;
                readonly WATER_BLAST: MagicSpell;
                readonly EARTH_BLAST: MagicSpell;
                readonly FIRE_BLAST: MagicSpell;
                readonly WIND_WAVE: MagicSpell;
                readonly WATER_WAVE: MagicSpell;
                readonly EARTH_WAVE: MagicSpell;
                readonly FIRE_WAVE: MagicSpell;
                readonly WIND_SURGE: MagicSpell;
                readonly WATER_SURGE: MagicSpell;
                readonly EARTH_SURGE: MagicSpell;
                readonly FIRE_SURGE: MagicSpell;
                readonly SARADOMIN_STRIKE: MagicSpell;
                readonly CLAWS_OF_GUTHIX: MagicSpell;
                readonly FLAMES_OF_ZAMORAK: MagicSpell;
                readonly CRUMBLE_UNDEAD: MagicSpell;
                readonly IBAN_BLAST: MagicSpell;
                readonly MAGIC_DART: MagicSpell;
                readonly CONFUSE: MagicSpell;
                readonly WEAKEN: MagicSpell;
                readonly CURSE: MagicSpell;
                readonly BIND: MagicSpell;
                readonly SNARE: MagicSpell;
                readonly VULNERABILITY: MagicSpell;
                readonly ENFEEBLE: MagicSpell;
                readonly ENTANGLE: MagicSpell;
                readonly STUN: MagicSpell;
                readonly TELE_BLOCK: MagicSpell;
                readonly CHARGE: MagicSpell;
                readonly BONES_TO_BANANAS: MagicSpell;
                readonly LOW_LEVEL_ALCHEMY: MagicSpell;
                readonly SUPERHEAT_ITEM: MagicSpell;
                readonly HIGH_LEVEL_ALCHEMY: MagicSpell;
                readonly BONES_TO_PEACHES: MagicSpell;
                readonly LVL_1_ENCHANT: MagicSpell;
                readonly LVL_2_ENCHANT: MagicSpell;
                readonly LVL_3_ENCHANT: MagicSpell;
                readonly CHARGE_WATER_ORB: MagicSpell;
                readonly LVL_4_ENCHANT: MagicSpell;
                readonly CHARGE_EARTH_ORB: MagicSpell;
                readonly CHARGE_FIRE_ORB: MagicSpell;
                readonly CHARGE_AIR_ORB: MagicSpell;
                readonly LVL_5_ENCHANT: MagicSpell;
                readonly LVL_6_ENCHANT: MagicSpell;
                readonly LVL_7_ENCHANT: MagicSpell;
                readonly TELEKINETIC_GRAB: MagicSpell;
            };
            readonly Ancient: {
                readonly EDGEVILLE_HOME_TELEPORT: MagicSpell;
                readonly PADDEWWA_TELEPORT: MagicSpell;
                readonly SENNTISTEN_TELEPORT: MagicSpell;
                readonly KHARYRLL_TELEPORT: MagicSpell;
                readonly LASSAR_TELEPORT: MagicSpell;
                readonly DAREEYAK_TELEPORT: MagicSpell;
                readonly CARRALLANGER_TELEPORT: MagicSpell;
                readonly BOUNTY_TARGET_TELEPORT: MagicSpell;
                readonly ANNAKARL_TELEPORT: MagicSpell;
                readonly GHORROCK_TELEPORT: MagicSpell;
                readonly SMOKE_RUSH: MagicSpell;
                readonly SHADOW_RUSH: MagicSpell;
                readonly BLOOD_RUSH: MagicSpell;
                readonly ICE_RUSH: MagicSpell;
                readonly SMOKE_BURST: MagicSpell;
                readonly SHADOW_BURST: MagicSpell;
                readonly BLOOD_BURST: MagicSpell;
                readonly ICE_BURST: MagicSpell;
                readonly SMOKE_BLITZ: MagicSpell;
                readonly SHADOW_BLITZ: MagicSpell;
                readonly BLOOD_BLITZ: MagicSpell;
                readonly ICE_BLITZ: MagicSpell;
                readonly SMOKE_BARRAGE: MagicSpell;
                readonly SHADOW_BARRAGE: MagicSpell;
                readonly BLOOD_BARRAGE: MagicSpell;
                readonly ICE_BARRAGE: MagicSpell;
            };
            readonly Lunar: {
                readonly LUNAR_HOME_TELEPORT: MagicSpell;
                readonly MOONCLAN_TELEPORT: MagicSpell;
                readonly TELE_GROUP_MOONCLAN: MagicSpell;
                readonly OURANIA_TELEPORT: MagicSpell;
                readonly WATERBIRTH_TELEPORT: MagicSpell;
                readonly TELE_GROUP_WATERBIRTH: MagicSpell;
                readonly BARBARIAN_TELEPORT: MagicSpell;
                readonly TELE_GROUP_BARBARIAN: MagicSpell;
                readonly KHAZARD_TELEPORT: MagicSpell;
                readonly TELE_GROUP_KHAZARD: MagicSpell;
                readonly FISHING_GUILD_TELEPORT: MagicSpell;
                readonly TELE_GROUP_FISHING_GUILD: MagicSpell;
                readonly CATHERBY_TELEPORT: MagicSpell;
                readonly TELE_GROUP_CATHERBY: MagicSpell;
                readonly ICE_PLATEAU_TELEPORT: MagicSpell;
                readonly TELE_GROUP_ICE_PLATEAU: MagicSpell;
                readonly MONSTER_EXAMINE: MagicSpell;
                readonly CURE_OTHER: MagicSpell;
                readonly CURE_ME: MagicSpell;
                readonly CURE_GROUP: MagicSpell;
                readonly STAT_SPY: MagicSpell;
                readonly DREAM: MagicSpell;
                readonly STAT_RESTORE_POT_SHARE: MagicSpell;
                readonly BOOST_POTION_SHARE: MagicSpell;
                readonly ENERGY_TRANSFER: MagicSpell;
                readonly HEAL_OTHER: MagicSpell;
                readonly VENGEANCE_OTHER: MagicSpell;
                readonly VENGEANCE: MagicSpell;
                readonly HEAL_GROUP: MagicSpell;
                readonly BAKE_PIE: MagicSpell;
                readonly GEOMANCY: MagicSpell;
                readonly CURE_PLANT: MagicSpell;
                readonly NPC_CONTACT: MagicSpell;
                readonly HUMIDIFY: MagicSpell;
                readonly HUNTER_KIT: MagicSpell;
                readonly SPIN_FLAX: MagicSpell;
                readonly SUPERGLASS_MAKE: MagicSpell;
                readonly TAN_LEATHER: MagicSpell;
                readonly STRING_JEWELLERY: MagicSpell;
                readonly MAGIC_IMBUE: MagicSpell;
                readonly FERTILE_SOIL: MagicSpell;
                readonly PLANK_MAKE: MagicSpell;
                readonly RECHARGE_DRAGONSTONE: MagicSpell;
                readonly SPELLBOOK_SWAP: MagicSpell;
            };
            readonly Necromancy: {
                readonly ARCEUUS_HOME_TELEPORT: MagicSpell;
                readonly ARCEUUS_LIBRARY_TELEPORT: MagicSpell;
                readonly DRAYNOR_MANOR_TELEPORT: MagicSpell;
                readonly BATTLEFRONT_TELEPORT: MagicSpell;
                readonly MIND_ALTAR_TELEPORT: MagicSpell;
                readonly RESPAWN_TELEPORT: MagicSpell;
                readonly SALVE_GRAVEYARD_TELEPORT: MagicSpell;
                readonly FENKENSTRAINS_CASTLE_TELEPORT: MagicSpell;
                readonly WEST_ARDOUGNE_TELEPORT: MagicSpell;
                readonly HARMONY_ISLAND_TELEPORT: MagicSpell;
                readonly CEMETERY_TELEPORT: MagicSpell;
                readonly BARROWS_TELEPORT: MagicSpell;
                readonly APE_ATOLL_TELEPORT: MagicSpell;
                readonly GHOSTLY_GRASP: MagicSpell;
                readonly SKELETAL_GRASP: MagicSpell;
                readonly UNDEAD_GRASP: MagicSpell;
                readonly INFERIOR_DEMONBANE: MagicSpell;
                readonly SUPERIOR_DEMONBANE: MagicSpell;
                readonly DARK_DEMONBANE: MagicSpell;
                readonly LESSER_CORRUPTION: MagicSpell;
                readonly GREATER_CORRUPTION: MagicSpell;
                readonly RESURRECT_LESSER_GHOST: MagicSpell;
                readonly RESURRECT_LESSER_SKELETON: MagicSpell;
                readonly RESURRECT_LESSER_ZOMBIE: MagicSpell;
                readonly RESURRECT_SUPERIOR_GHOST: MagicSpell;
                readonly RESURRECT_SUPERIOR_SKELETON: MagicSpell;
                readonly RESURRECT_SUPERIOR_ZOMBIE: MagicSpell;
                readonly RESURRECT_GREATER_GHOST: MagicSpell;
                readonly RESURRECT_GREATER_SKELETON: MagicSpell;
                readonly RESURRECT_GREATER_ZOMBIE: MagicSpell;
                readonly DARK_LURE: MagicSpell;
                readonly MARK_OF_DARKNESS: MagicSpell;
                readonly WARD_OF_ARCEUUS: MagicSpell;
                readonly BASIC_REANIMATION: MagicSpell;
                readonly ADEPT_REANIMATION: MagicSpell;
                readonly EXPERT_REANIMATION: MagicSpell;
                readonly MASTER_REANIMATION: MagicSpell;
                readonly DEMONIC_OFFERING: MagicSpell;
                readonly SINISTER_OFFERING: MagicSpell;
                readonly SHADOW_VEIL: MagicSpell;
                readonly VILE_VIGOUR: MagicSpell;
                readonly DEGRIME: MagicSpell;
                readonly RESURRECT_CROPS: MagicSpell;
                readonly DEATH_CHARGE: MagicSpell;
            };
            info(spell: MagicSpell): MagicSpellInfo;
            readonly currentSpellBook: 0 | 1 | 2 | 3;
            readonly isAutoCasting: boolean;
            lastHomeTeleportUsage(): Date;
            readonly isHomeTeleportOnCooldown: boolean;
            canCast(spell: MagicSpell): boolean;
            select(spell: MagicSpell): boolean;
            cast(spell: MagicSpell, skipMovement?: boolean): boolean;
            cast(spell: MagicSpell, actionIndex: number, skipMovement?: boolean): boolean;
            castOn(spell: MagicSpell, target: MagicTarget, skipMovement?: boolean): boolean;
        };
    }

    /**
     * Equipment slot ordinals matching RuneLite's
     * `EquipmentInventorySlot`. Values are stable engine semantics --
     * the slot index inside the EQUIPMENT `ItemContainer`. Added in
     * SDK 40.
     */
    const EquipmentSlot: {
        readonly HEAD: 0;
        readonly CAPE: 1;
        readonly AMULET: 2;
        readonly WEAPON: 3;
        readonly BODY: 4;
        readonly SHIELD: 5;
        readonly ARMS: 6;
        readonly LEGS: 7;
        readonly HAIR: 8;
        readonly GLOVES: 9;
        readonly BOOTS: 10;
        readonly JAW: 11;
        readonly RING: 12;
        readonly AMMO: 13;
    };

    /**
     * One occupied equipment slot. `slot` is a `titan.EquipmentSlot`
     * ordinal; `name` is the in-game display label (varbit / varp
     * transforms applied when the runtime ItemDef path is available).
     * Added in SDK 40.
     */
    interface EquippedItem {
        readonly slot: number;     // titan.EquipmentSlot ordinal
        readonly id: number;
        readonly quantity: number;
        readonly name: string;
        /** True when this entry is a real, equipped item (id > 0). */
        readonly isValid: boolean;
    }

    namespace utils {
        /**
         * Equipment query + unequip helpers. Mirrors
         * `titan::utils::Equipment::*` from
         * [shared/titan/utils/equipment.h](shared/titan/utils/equipment.h).
         * Reads compose `titan.state.itemContainer(EQUIPMENT)`;
         * `unequip*` fires a synthetic CC_OP click against the worn-
         * items slot widget -- same shape as the RuneLite-side
         * `LegacyMenuEntry("Remove", "", identifier=1, opcode=CC_OP,
         * p0=-1, p1=<wornitems slot>)`.
         *
         * Numeric arguments to `find` / `contains` / `unequip` are
         * treated as **item ids**; pass a `titan.EquipmentSlot` value
         * through `getSlot` / `unequipSlot` when targeting a specific
         * slot. This disambiguates ids that happen to land in
         * `[0, 13]`.
         *
         * Added in SDK 40.
         */
        const equipment: {
            /** Snapshot every occupied equipment slot. Empty when the
             *  EQUIPMENT container isn't populated on this revision. */
            getAll(): EquippedItem[];
            /** First equipped item matching @p query (item id when
             *  number, case-insensitive name substring when string).
             *  Returns null when no match. */
            find(query: number | string): EquippedItem | null;
            /** The item occupying the given `titan.EquipmentSlot`
             *  ordinal, or null when empty. */
            getSlot(slot: number): EquippedItem | null;
            /** Every equipped item whose id appears in @p ids. */
            getByIds(ids: number[]): EquippedItem[];
            /** Every equipped item whose display name contains any
             *  needle in @p names (case-insensitive substring). */
            getByNames(names: string[]): EquippedItem[];
            /** True when @p query is currently equipped. When @p query
             *  is a number and @p minQuantity is provided, requires the
             *  slot to hold at least that many charges (useful for
             *  ammo). */
            contains(query: number | string, minQuantity?: number): boolean;
            /** Combined quantity of every equipped slot whose id matches
             *  the argument. Pass an array to sum across multiple ids. */
            count(idOrIds: number | number[]): number;
            /** True when ANY entry in the array (mixed ids / names) is
             *  equipped. */
            containsAny(arr: Array<number | string>): boolean;
            /** True when EVERY entry in the array (mixed ids / names) is
             *  equipped. */
            containsAll(arr: Array<number | string>): boolean;
            /** Unequip the item identified by @p query. Numbers are
             *  item ids; strings are case-insensitive name substrings.
             *  Returns true when the remove action was accepted / queued;
             *  confirm the state change via `onItemContainerChanged`. */
            unequip(query: number | string): boolean;
            /** Unequip the item occupying the given `titan.EquipmentSlot`
             *  ordinal. No-op (returns false) when the slot is empty or
             *  has no clickable widget (ARMS / HAIR / JAW). A true return
             *  means the remove action was accepted / queued. */
            unequipSlot(slot: number): boolean;
        };

        /**
         * Bank state, query, and action helpers. Mirrors
         * `titan::utils::Bank::*` from
         * [shared/titan/utils/bank.h](shared/titan/utils/bank.h).
         * Added in SDK 44.
         */
        const bank: {
            /** True when the bank interface is open. */
            readonly isOpen: boolean;
            /** True when the GE inventory overlay is open. */
            readonly isGeOpen: boolean;
            /** True when a search/amount dialog is open (Withdraw-X). */
            readonly isSearchOpen: boolean;
            /** True when bank is in noted withdrawal mode. */
            readonly isNotedMode: boolean;
            /** Currently selected bank tab index. */
            readonly bankTab: number;
            /** True when main (all items) tab is selected. */
            readonly isMainTabOpen: boolean;
            /** True when the bank PIN prompt is visible. */
            readonly isPinVisible: boolean;
            /** Index (0-3) of the currently requested PIN digit, or -1. */
            readonly pinRequestedDigitIndex: number;

            /** Close the bank interface. */
            close(): boolean;
            /** Set noted/unnoted withdrawal mode. */
            setNotedMode(noted: boolean): boolean;
            /** Check if bank contains item by id or name, with optional minimum quantity. */
            contains(idOrName: number | string, minQty?: number): boolean;
            /** Count items in bank by id or name. */
            count(idOrName: number | string): number;
            /** Find first matching bank slot by id or name. */
            find(idOrName: number | string): BankItemSlot | null;
            /** Deposit entire inventory. */
            depositAll(): boolean;
            /** Deposit all equipped items. */
            depositEquipment(): boolean;
            /** Deposit all of item at a specific inventory slot. */
            depositAllOfSlot(slot: number): boolean;
            /** Deposit one of item at a specific inventory slot. */
            depositOneOfSlot(slot: number): boolean;
            /** Deposit all of an item by item ID. */
            depositAllOfItem(itemId: number): boolean;
            /** Deposit one of an item by item ID. */
            depositOneOfItem(itemId: number): boolean;
            /** Deposit all items except those with the given ids. */
            depositAllExcept(keepIds: number[]): boolean;
            /** Withdraw one of item by id. */
            withdrawItem(itemId: number): boolean;
            /** Withdraw all of item by id. */
            withdrawAllItem(itemId: number): boolean;
            /** Withdraw specific amount of item. */
            withdrawItemAmount(itemId: number, amount: number): boolean;
            /** Open nearest bank. */
            open(): boolean;
            /** Check if a bank is nearby. */
            isNearBank(distance?: number): boolean;
        };
    }

    // Widgets.
    interface WidgetState {
        /** Packed widget id `(groupId << 16) | childId`. Populated by the host. */
        packedId: number;
        /** Dynamic parent packed id for `children(...)` snapshots; 0 otherwise. SDK v63+. */
        dynamicParentPackedId: number;
        /** Native dynamic-child slot for `children(...)` snapshots; -1 otherwise. SDK v63+. */
        dynamicChildSlot: number;
        /** Root flat widget used to resolve this snapshot's retained path. SDK v64+. */
        rootPackedId: number;
        /** Native dynamic-child slots beneath `rootPackedId`. SDK v64+. */
        dynamicPath: number[];
        screenX: number;
        screenY: number;
        width: number;
        height: number;
        relativeX: number;
        relativeY: number;
        scrollX: number;
        scrollY: number;
        type: number;
        contentType: number;
        opacity: number;
        itemId: number;
        itemQuantity: number;
        parentId: number;
        hidden: boolean;
        selfHidden: boolean;
        visible: boolean;
        text: string;
        /**
         * Replace this widget's live display text. SDK v51+.
         * Dynamic snapshots automatically route through their retained
         * root-plus-slot path. SDK v64+.
         * Returns false when the host is unavailable, the widget is missing,
         * or `text` is longer than 256 UTF-8 bytes. Older offset bundles
         * retain the inline-only limit of 22 bytes.
         */
        setText(text: string): boolean;
        /**
         * Dispatch a widget-family DoAction against this exact widget. With
         * two arguments the snapshot itself is targeted. A third argument
         * selects one exact direct dynamic child beneath it. SDK v64+.
         *
         * @param opcode   MenuAction opcode (57 = CC_OP, 1007 = CC_OP_LOW,
         *                 39..43 = WIDGET_FIRST..FIFTH_OPTION, 25 =
         *                 WIDGET_TARGET, 2 = WIDGET_TARGET_ON_GAME_OBJECT,
         *                 8 = WIDGET_TARGET_ON_NPC, 15 = WIDGET_TARGET_ON_PLAYER,
         *                 58 = WIDGET_TARGET_ON_WIDGET).
         * @param identifier Menu-entry identifier -- the CC_OP sub-action
         *                   index, 0 for non-CC_OP opcodes. This is not a
         *                   widget packed id.
         * @param childSlot Optional direct dynamic-child slot beneath this
         *                  snapshot.
         * @returns true when the action was accepted / queued. Widget-driven
         *          game state changes are confirmed later by events such as
         *          `onItemContainerChanged`.
         */
        interact(opcode: number, identifier: number, childSlot?: number): boolean;
    }
    namespace state {
        const widgets: {
            find(packedId: number): WidgetState | null;
            /**
             * Enumerate dynamic children of the widget at
             * `parentPackedId` (SDK v38+). Returns one `WidgetState`
             * per native slot ordered by slot index. Empty placeholders are
             * preserved so indexes cannot drift. Each returned snapshot
             * remembers its parent and slot for `child.setText(...)` and
             * `child.interact(opcode, identifier)`.
             *
             * Returns an empty array when the parent is missing, has no
             * dynamic children, or the host is pre-SDK-38.
             */
            children(parentPackedId: number): WidgetState[];
            pack(group: number, child: number): number;
            /**
             * Dispatch a widget-family DoAction (CC_OP,
             * WIDGET_*_OPTION, WIDGET_TARGET, WIDGET_TARGET_ON_WIDGET,
             * ...).
             *
             * @param opcode   MenuAction opcode (57 = CC_OP, 1007 = CC_OP_LOW,
             *                 39..43 = WIDGET_FIRST..FIFTH_OPTION, 25 =
             *                 WIDGET_TARGET, 2 = WIDGET_TARGET_ON_GAME_OBJECT,
             *                 8 = WIDGET_TARGET_ON_NPC, 15 = WIDGET_TARGET_ON_PLAYER,
             *                 58 = WIDGET_TARGET_ON_WIDGET).
             * @param identifier Menu-entry identifier -- the CC_OP
             *                   sub-action index, 0 for non-CC_OP opcodes.
             *                   This is not a widget packed id.
             * @param param0   Dynamic-child slot on the target widget,
             *                 or -1 for "whole widget / no slot".
             * @param param1   Packed widget id: `(group << 16) | child`.
             * @returns true when the action was accepted / queued. Widget-
             *          driven game state changes are confirmed later by
             *          events such as `onItemContainerChanged`.
             */
            interact(opcode: number, identifier: number,
                     param0: number, param1: number): boolean;
            /**
             * Replace a live widget's display text. SDK v51+.
             * Returns false when the widget is missing or `text` is longer
             * than 256 UTF-8 bytes. Older offset bundles retain the
             * inline-only limit of 22 bytes.
             */
            setText(packedId: number, text: string): boolean;
            /**
             * Replace an exact dynamic child's display text. SDK v63+.
             * Invalid, missing, or null slots fail closed without touching
             * the parent widget.
             */
            setText(parentPackedId: number, slot: number, text: string): boolean;
            /**
             * First widget whose *primary* display text contains
             * `query` (case-sensitive substring). SDK v39+ /
             * `HostApi::getWidgetByText`. Returns `null` when no match
             * or on pre-v39 hosts.
             */
            findByText(query: string): WidgetState | null;
        };

        /** Idle-timer subsystem. */
        const idle: {
            readonly remaining: number;
            reset(): void;
        };
    }

    // Login / account switch (Super Profiles port).
    enum LoginGameState {
        Unknown = -1,
        LoginScreen = 0,
        LoginAuthenticator = 1,
        LoggingIn = 2,
        LoggedIn = 3,
        HoppingWorld = 4,
    }

    interface LoginSnapshot {
        loginIndex: number;
        gameState: LoginGameState;
        /** 0 = username, 1 = password, -1 = unknown. */
        fieldToggle: number;
        oauthSwitchAvailable: boolean;
        credentialSetAvailable: boolean;
        displayNameAvailable: boolean;
        username: string;
        displayName: string;
    }

    namespace state {
        const login: {
            /** Current snapshot, or null when the analyzer did not detect the login flow. */
            snapshot(): LoginSnapshot | null;
            readonly state: LoginGameState;
            readonly index: number;
            readonly isLoggedIn: boolean;

            setUsername(username: string): void;
            setPassword(password: string): void;
            setAuthenticator(code: string): void;
            setIndex(loginIndex: number): void;
            setDisplayName(displayName: string): void;
            setOAuth2Credentials(accessToken: string, refreshToken: string): void;
            setGameSessionCredentials(sessionId: string, characterId: string): void;

            /** Composite equivalent of RuneLite's ReflectionMethods.setCharacter. */
            setCharacter(displayName: string, characterId: string, sessionId: string): void;
            /** Clear every Jagex token and flip back to the standard login screen. */
            resetCharacter(): void;
        };

        /** Walking facade. */
        const walk: {
            toScene(sceneX: number, sceneY: number): boolean;
            toWorld(worldX: number, worldY: number, plane: number): boolean;
            to(tile: Tile | WorldPoint): boolean;
        };

        /**
         * Per-tile collision map reads + one-tile step blocking
         * (RouteFindSize1). SDK v39+. Mirrors
         * `titan::state::collisions()` / `<titan/collision.h>`.
         */
        const collisions: {
            flag(plane: number, tileX: number, tileY: number): number;
            isBlocked(plane: number, x: number, y: number, dx: number, dy: number): boolean;
            readonly Flag: {
                readonly WALL_SE_CORNER: number;
                readonly WALL_SOUTH: number;
                readonly WALL_SW_CORNER: number;
                readonly WALL_WEST: number;
                readonly WALL_NW_CORNER: number;
                readonly WALL_NORTH: number;
                readonly WALL_NE_CORNER: number;
                readonly WALL_EAST: number;
                readonly BLOCK_FLOOR: number;
                readonly BLOCK_FLOOR_DECORATION: number;
                readonly BLOCK_OBJECT: number;
                readonly BLOCK_FULL: number;
                readonly BLOCK_MOVE: number;
                readonly BLOCKED_WEST: number;
                readonly BLOCKED_EAST: number;
                readonly BLOCKED_SOUTH: number;
                readonly BLOCKED_NORTH: number;
                readonly BLOCKED_SW: number;
                readonly BLOCKED_SE: number;
                readonly BLOCKED_NW: number;
                readonly BLOCKED_NE: number;
            };
        };
    }

    // Plugin manager.
    const plugins: {
        all(): PluginHandle[];
        /** Return a handle for `id`, regardless of whether the host knows it.
         *  Call `.isValid` on the result if you need to distinguish. */
        get(id: string): PluginHandle;
        /** Like `get` but returns `null` for unknown ids. */
        find(id: string): PluginHandle | null;
        readonly self: PluginHandle & (() => PluginHandle);
    };

    // Overlay draw API — call from inside Overlay::render or renderOverlay.
    const overlay: {
        tileQuad(tileX: number, tileY: number, plane: number,
                 fillColor: number, outlineColor: number): void;
        tileRegion(minTileX: number, minTileY: number, maxTileX: number, maxTileY: number,
                   plane: number, fillColor: number, outlineColor: number): void;
        entityBox(entity: Npc | Player, color: number, height?: number): void;
        entityBoxAt(preciseX: number, preciseY: number, plane: number,
                    tileSize: number, height: number, color: number): void;
        /**
         * Draw the accurate world-space AABB clickbox around an entity. Reads
         * the per-entity AABB from the game's scene-graph cache and projects
         * the 8 corners through W2S; silent no-op when the analyzer did not
         * detect the GraphNode / AABB offsets on this revision, or on
         * pre-SDK-33 hosts.
         *
         * @param outline ARGB colour (0xAARRGGBB) for the 12 edges (0 hides edges).
         * @param fill    Optional ARGB colour (0xAARRGGBB) for the 6 faces (0 hides fills).
         */
        entityClickbox(entity: Npc | Player, outline: number, fill?: number): void;
        /**
         * Raw entry for plugins that already hold the entity pointer and
         * its typecode. Passing `typecode = 0` skips typecode-keyed lookup
         * and falls back to the host's legacy world-keyed cache.
         */
        entityClickboxRaw(entityPtr: number, typecode: number,
                          outline: number, fill?: number): void;
        /** Tile-object (wall, decor, standing loc, ground decor) equivalent. */
        tileObjectClickbox(obj: TileObject, outline: number, fill?: number): void;
        tileObjectClickboxRaw(locPtr: number, typecode: number,
                              outline: number, fill?: number): void;
        /**
         * Draw the 2D convex hull of an entity's projected AABB corners --
         * a clean closed silhouette. Same data source as entityClickbox
         * but reduced to the outer outline. Recommended "highlight this
         * entity" primitive. Future SDK versions may upgrade the source
         * to real model vertices.
         */
        entityHull(entity: Npc | Player, outline: number, fill?: number): void;
        entityHullRaw(entityPtr: number, typecode: number,
                      outline: number, fill?: number): void;
        tileObjectHull(obj: TileObject, outline: number, fill?: number): void;
        tileObjectHullRaw(locPtr: number, typecode: number,
                          outline: number, fill?: number): void;
        textAtWorld(worldX: number, worldY: number, worldZ: number,
                    text: string, color: number, centered?: boolean): void;
        screenText(x: number, y: number, text: string, color: number): void;
        screenRect(x: number, y: number, w: number, h: number, color: number): void;
        screenLine(x1: number, y1: number, x2: number, y2: number,
                   color: number, thickness?: number): void;
        worldToScreen(worldX: number, worldY: number, worldZ: number): ScreenPoint | null;
        tileToScreen(tileX: number, tileY: number, plane: number,
                     heightOffset?: number): ScreenPoint | null;
        tileHeight(preciseX: number, preciseY: number, plane: number): number;
    };

    // Frame-phase schedulers.
    function runOnClientTick(cb: () => void): void;
    function runOnRender(cb: () => void): void;

    // Chat injection (SDK 22). `name` and `sender` accept empty strings.
    // No packet is sent -- the line appears only on this client.
    function addChatMessage(type: number, name: string, message: string, sender: string): void;

    /** Fluent chat facade; also see the free function `titan.addChatMessage`. */
    const chat: {
        /** Inject a line into the local chatbox. */
        add(type: number, name: string, message: string, sender?: string): void;
        /** Convenience: post a SERVER-type system line (no sender). */
        system(message: string): void;
        /** Convenience: post a PUBLIC-type line attributed to `name`. */
        say(name: string, message: string): void;
    };

    /**
     * Game-internal keyboard injection (SDK 42+). Synthetic
     * `WM_KEYDOWN` / `WM_CHAR` / `WM_KEYUP` messages are dispatched
     * directly to the game's registered WndProc on the message-pump
     * thread. The injected events take the same path as a hardware
     * keystroke -- UI updates, server packets, listener callbacks,
     * and hotkey bindings all fire normally -- without crossing the
     * kernel LL-hook boundary, so anti-cheat detection of injected
     * input does not apply.
     *
     * Functions return `false` when the underlying offsets are not
     * available on the running revision. Calls are queued onto the
     * Win32 message-pump thread automatically when invoked from any
     * other thread.
     *
     * Example:
     * ```ts
     * titan.keyboard.sendString("hello");
     * titan.keyboard.sendKey(titan.keyboard.Key.Enter);
     * titan.keyboard.sendKey(titan.keyboard.Key.Tab,
     *                        titan.keyboard.Mod.Shift);
     * ```
     *
     * Notes:
     *  - The "always-focused" hook in the client makes injection work
     *    even when the game window is in the background; users who
     *    rely on focus-loss pause behaviour can disable it from the
     *    controller's Settings tab.
     *  - All keys in `Key` (including F-keys, arrows, and page
     *    navigation) deliver real Win32 VK codes, so the game's own
     *    VK->internal translation handles them; no calibration
     *    needed.
     */
    const keyboard: {
        /**
         * Type a string. Each printable character is delivered as
         * `WM_CHAR` to the game's WndProc; `\n`/`\r`/`\b`/`\t` are
         * escalated to a press+release of the matching control
         * key so e.g. submitting a chat message still works.
         */
        sendString(text: string): boolean;
        /**
         * Press AND release a single key, optionally bracketed by
         * a modifier (Shift / Ctrl / Alt). `mods` is a bitmask
         * built from `titan.keyboard.Mod` constants.
         */
        sendKey(key: number, mods?: number): boolean;
        /** Symbolic keys -- pass to `sendKey`. */
        readonly Key: {
            readonly Enter: 0;
            readonly Escape: 1;
            readonly Backspace: 2;
            readonly Delete: 3;
            readonly Tab: 4;
            readonly Space: 5;
            readonly Home: 6;
            readonly End: 7;
            readonly PageUp: 8;
            readonly PageDown: 9;
            readonly Insert: 10;
            readonly ArrowUp: 11;
            readonly ArrowDown: 12;
            readonly ArrowLeft: 13;
            readonly ArrowRight: 14;
            readonly F1: 15;
            readonly F2: 16;
            readonly F3: 17;
            readonly F4: 18;
            readonly F5: 19;
            readonly F6: 20;
            readonly F7: 21;
            readonly F8: 22;
            readonly F9: 23;
            readonly F10: 24;
            readonly F11: 25;
            readonly F12: 26;
            readonly Shift: 27;
            readonly Control: 28;
            readonly Alt: 29;
        };
        /** Modifier bitmask -- pass to `sendKey`'s second arg. */
        readonly Mod: {
            readonly Shift: 1;
            readonly Ctrl: 2;
            readonly Alt: 4;
        };

        /**
         * Type a string with human-like inter-character delays
         * (SDK 43+). Each character is dispatched on a separate
         * pump-thread drain cycle, spread across real time.
         *
         * If a type operation is already in progress, the old one
         * is cancelled (its callback fires with `false`) before
         * the new one starts.
         *
         * @param text The string to type.
         * @param opts Optional timing / callback-phase config.
         * @param onDone Optional completion callback. `true` when
         *   all characters were typed, `false` if cancelled.
         * @returns `true` when the typing operation was started.
         */
        typeString(
            text: string,
            opts?: {
                minDelayMs?: number;
                maxDelayMs?: number;
                /** Which thread the callback runs on. */
                callbackPhase?: "pump" | "clientTick" | "preGameLoop";
            },
            onDone?: (completed: boolean) => void,
        ): boolean;

        /** Cancel any in-progress `typeString`. Its callback fires
         *  with `false`. */
        cancelTypeString(): void;

        /** `true` while a `typeString` operation is in progress. */
        isTyping(): boolean;

        /** Callback-phase constants for `typeString` opts. */
        readonly CallbackPhase: {
            readonly Pump: "pump";
            readonly ClientTick: "clientTick";
            readonly PreGameLoop: "preGameLoop";
        };
    };

    /**
     * Chat message type ordinals. Names and values match RuneLite's
     * `ChatMessageType` enum (see
     * https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/ChatMessageType.java).
     * Pass one of these to `titan.addChatMessage(type, ...)` or compare
     * against `ChatMessageEvent.type` in `onChatMessage`.
     */
    const ChatMessageType: {
        readonly GAMEMESSAGE: 0;
        readonly MODCHAT: 1;
        readonly PUBLICCHAT: 2;
        readonly PRIVATECHAT: 3;
        readonly ENGINE: 4;
        readonly LOGINLOGOUTNOTIFICATION: 5;
        readonly PRIVATECHATOUT: 6;
        readonly MODPRIVATECHAT: 7;
        readonly FRIENDSCHAT: 9;
        readonly FRIENDSCHATNOTIFICATION: 11;
        readonly TRADE_SENT: 12;
        readonly BROADCAST: 14;
        readonly SNAPSHOTFEEDBACK: 26;
        readonly ITEM_EXAMINE: 27;
        readonly NPC_EXAMINE: 28;
        readonly OBJECT_EXAMINE: 29;
        readonly FRIENDNOTIFICATION: 30;
        readonly IGNORENOTIFICATION: 31;
        readonly CLAN_CHAT: 41;
        readonly CLAN_MESSAGE: 43;
        readonly CLAN_GUEST_CHAT: 44;
        readonly CLAN_GUEST_MESSAGE: 46;
        readonly AUTOTYPER: 90;
        readonly MODAUTOTYPER: 91;
        readonly CONSOLE: 99;
        readonly TRADEREQ: 101;
        readonly TRADE: 102;
        readonly CHALREQ_TRADE: 103;
        readonly CHALREQ_FRIENDSCHAT: 104;
        readonly SPAM: 105;
        readonly PLAYERRELATED: 106;
        readonly TENSECTIMEOUT: 107;
        readonly WELCOME: 108;
        readonly CLAN_CREATION_INVITATION: 109;
        readonly CHALREQ_CLANCHAT: 110;
        readonly CLAN_GIM_FORM_GROUP: 111;
        readonly CLAN_GIM_GROUP_WITH: 112;
        readonly DIALOG: 114;
        readonly MESBOX: 115;
        readonly NPC_SAY: 116;
        readonly DIDYOUKNOW: 117;
        readonly LEVELUPMESSAGE: 118;
    };

    /**
     * Well-known item-container ids. Pass one of these (or a raw int) to
     * `titan.state.itemContainer()`. Matches RuneLite's `InventoryID`.
     * Added in SDK 26.
     */
    const InventoryID: {
        readonly INVENTORY: 93;
        readonly EQUIPMENT: 94;
        readonly BANK: 95;
    };

    namespace state {
        /**
         * Read a snapshot of the requested container. Returns null when
         * the container id is unmapped on the current client rev OR the
         * underlying widget isn't populated (bank closed, equipment
         * screen not opened, etc.). Added in SDK 26.
         */
        function itemContainer(id: number): ItemContainerSnapshot | null;

        /**
         * Resolve the RUNTIME ItemDef for the given id -- applies
         * varbit/varp transforms and preserves inventory-action slots
         * when the analyzer detected the native lookup on this
         * revision (check the `runtimeResolved` flag). Returns null
         * only when the id is entirely unknown to both the runtime and
         * the cache. Added in SDK 26.
         */
        function itemDef(id: number): ItemComposition | null;
    }

    /**
     * Snapshot of a single world entry. Mirrors the native `GameWorld`
     * struct fields the analyzer detects at runtime. `string0` /
     * `string1` carry the two eastl::basic_string fields on the entry;
     * which one is "activity" vs "location" varies by revision and the
     * SDK doesn't guess. Added in SDK 28.
     */
    interface World {
        readonly id: number;
        readonly flags: number;     // bit 0 = members, bit 16 = beta
        readonly isMembers: boolean;
        readonly isBeta: boolean;
        readonly string0: string;
        readonly string1: string;
    }

    namespace state {
        namespace world {
            /**
             * Live current-world id from the game singleton. Returns
             * null when the analyzer did not emit
             * `CURRENT_WORLD_OFFSET` on the loaded revision. Added in
             * SDK 28.
             */
            function current(): number | null;

            /**
             * Full snapshot of the native world list. Returns an empty
             * array when the analyzer did not emit the list globals or
             * GameWorld field offsets. Even when this is empty,
             * `hopByListIndex()` may still work. Added in SDK 28.
             */
            function list(): World[];

            /**
             * Dispatch a **title-screen** hop to a specific world id.
             * Calls native `changeWorld` synchronously on the game
             * thread's main loop. Returns true iff the id was found in
             * the live list and the hop function is available. Use
             * this only when the player is NOT logged in -- see
             * `hopIngame` for the logged-in path. Plugins should
             * debounce hops (server boots rapid hoppers). Added in
             * SDK 28.
             */
            function hop(id: number): boolean;

            /**
             * Lower-tier title-screen variant: hop by position in the
             * native m_list. Works when the full list snapshot is
             * unavailable. Added in SDK 28.
             */
            function hopByListIndex(idx: number): boolean;

            /**
             * Dispatch an **in-game** hop via the native 3x CC_OP
             * footer sequence (opens logout tab, opens switcher,
             * selects world, confirms). Progresses asynchronously
             * across several client ticks; the return value is
             * "accepted?" -- true when the request was queued, false
             * when the state machine is already busy, the world id
             * isn't in the live list, or the analyzer data needed to
             * drive the widget clicks is missing. Use this when
             * `state.login.isLoggedIn` returns true. Added in SDK 31.
             */
            function hopIngame(id: number): boolean;
        }
    }

    // ---- Legacy flat free functions ----
    //
    // SDK 41 dropped the flat `titan.getNpcs()` / `titan.containsInventoryItem`
    // / etc. spellings entirely. Use the namespaced equivalents:
    //
    //   - `titan.queries.npcs() / players() / objects() / groundItems() /
    //     inventory() / projectiles()` for entity enumeration.
    //   - `titan.state.client / camera / widgets / skills / prayers / vars /
    //     script / walk / idle / login / hider / audio / cache / collisions /
    //     itemContainer / itemDef / world.*` for subsystem state.
    //   - `titan.utils.inventory / equipment / combat / dialogue` for
    //     composition helpers.
    //   - `titan.overlay.*` for rendering primitives.
    //   - `titan.state.client.invokeMenuAction(...)` for raw menu
    //     dispatch.

    /**
     * RuneLite-aligned `MenuAction` opcode ids (DoAction). Matches
     * `titan::MenuAction::Id` in `shared/titan/menu_action.h`.
     */
    const MenuAction: {
        readonly ITEM_USE_ON_GAME_OBJECT: 1;
        readonly WIDGET_TARGET_ON_GAME_OBJECT: 2;
        readonly ITEM_USE_ON_NPC: 7;
        readonly WIDGET_TARGET_ON_NPC: 8;
        readonly ITEM_USE_ON_PLAYER: 14;
        readonly WIDGET_TARGET_ON_PLAYER: 15;
        readonly ITEM_USE_ON_GROUND_ITEM: 16;
        readonly WIDGET_TARGET_ON_GROUND_ITEM: 17;
        readonly WALK: 23;
        readonly WIDGET_TYPE_1: 24;
        readonly WIDGET_TARGET: 25;
        readonly WIDGET_CLOSE: 26;
        readonly WIDGET_TYPE_4: 28;
        readonly WIDGET_TYPE_5: 29;
        readonly WIDGET_CONTINUE: 30;
        readonly WALK_HERE: 31;
        readonly WIDGET_USE_ON_ITEM: 32;
        readonly ITEM_USE: 38;
        readonly CC_OP: 57;
        readonly WIDGET_TARGET_ON_WIDGET: 58;
        readonly CANCEL: 1006;
        readonly CC_OP_LOW_PRIORITY: 1007;
    };

    /** Raw ClientObj ownership type for ground items. */
    const GroundItemOwnership: {
        readonly NONE: 0;
        readonly SELF_PLAYER: 1;
        readonly OTHER_PLAYER: 2;
        readonly GROUP_IRONMAN: 3;
    };

    /** Well-known CS2 script IDs. */
    const ScriptID: {
        readonly QUEST_STATUS_GET: 4029;
        readonly UPDATE_SCROLLBAR: 72;
        readonly BUILD_CHATBOX: 216;
        readonly CHAT_SEND: 5517;
        readonly CHAT_TEXT_INPUT_REBUILD: 222;
        readonly MESSAGE_LAYER_CLOSE: 299;
        readonly MESSAGE_LAYER_OPEN: 677;
        readonly CAMERA_DO_ZOOM: 42;
        readonly XPDROP_DISABLED: 2091;
        readonly TOPLEVEL_REDRAW: 907;
        readonly BANKMAIN_BUILD: 277;
        readonly BANKMAIN_SEARCH_TOGGLE: 281;
        readonly BANKMAIN_SEARCH_REFRESH: 283;
        readonly GE_OFFERS_SETUP_BUILD: 779;
        readonly GE_ITEM_SEARCH: 752;
        readonly COMBAT_INTERFACE_SETUP: 7593;
        readonly HP_HUD_UPDATE: 2103;
        readonly PRAYER_UPDATEBUTTON: 463;
        readonly PRAYER_REDRAW: 547;
        readonly ORBS_UPDATE_RUNENERGY: 447;
        readonly INVENTORY_DRAWITEM: 6011;
        readonly FRIENDS_UPDATE: 631;
        readonly IGNORE_UPDATE: 630;
        readonly PVP_WIDGET_BUILDER: 388;
        readonly XPDROPS_SETDROPSIZE: 996;
        readonly WIKI_ICON_UPDATE: 3306;
        readonly QUEST_UPDATE_LINECOUNT: 2523;
        readonly WORLDMAP_LOADMAP: 1712;
        readonly COLLECTION_DRAW_LIST: 2731;
    };

    /**
     * Skill ordinals matching the game's internal stat array. Mirrors
     * `titan::Skill` in [shared/titan/skill.h](shared/titan/skill.h).
     * Pass to `titan.state.skills.boosted/real/experience(...)`. Added in SDK 39.
     */
    const Skill: {
        readonly ATTACK: 0;
        readonly DEFENCE: 1;
        readonly STRENGTH: 2;
        readonly HITPOINTS: 3;
        readonly RANGED: 4;
        readonly PRAYER: 5;
        readonly MAGIC: 6;
        readonly COOKING: 7;
        readonly WOODCUTTING: 8;
        readonly FLETCHING: 9;
        readonly FISHING: 10;
        readonly FIREMAKING: 11;
        readonly CRAFTING: 12;
        readonly SMITHING: 13;
        readonly MINING: 14;
        readonly HERBLORE: 15;
        readonly AGILITY: 16;
        readonly THIEVING: 17;
        readonly SLAYER: 18;
        readonly FARMING: 19;
        readonly RUNECRAFT: 20;
        readonly HUNTER: 21;
        readonly CONSTRUCTION: 22;
        readonly SAILING: 23;
    };

    /**
     * Prayer ordinals (standard book + Ruinous Powers). Mirrors
     * `titan::Prayer` in [shared/titan/prayer.h](shared/titan/prayer.h).
     * Pass to `titan.state.prayers.isActive(...)`. Added in SDK 39.
     */
    const Prayer: {
        readonly THICK_SKIN: 0;
        readonly BURST_OF_STRENGTH: 1;
        readonly CLARITY_OF_THOUGHT: 2;
        readonly SHARP_EYE: 3;
        readonly MYSTIC_WILL: 4;
        readonly ROCK_SKIN: 5;
        readonly SUPERHUMAN_STRENGTH: 6;
        readonly IMPROVED_REFLEXES: 7;
        readonly RAPID_RESTORE: 8;
        readonly RAPID_HEAL: 9;
        readonly PROTECT_ITEM: 10;
        readonly HAWK_EYE: 11;
        readonly MYSTIC_LORE: 12;
        readonly STEEL_SKIN: 13;
        readonly ULTIMATE_STRENGTH: 14;
        readonly INCREDIBLE_REFLEXES: 15;
        readonly PROTECT_FROM_MAGIC: 16;
        readonly PROTECT_FROM_MISSILES: 17;
        readonly PROTECT_FROM_MELEE: 18;
        readonly EAGLE_EYE: 19;
        readonly MYSTIC_MIGHT: 20;
        readonly RETRIBUTION: 21;
        readonly REDEMPTION: 22;
        readonly SMITE: 23;
        readonly CHIVALRY: 24;
        readonly DEADEYE: 25;
        readonly MYSTIC_VIGOUR: 26;
        readonly PIETY: 27;
        readonly PRESERVE: 28;
        readonly RIGOUR: 29;
        readonly AUGURY: 30;
        readonly RP_REJUVENATION: 31;
        readonly RP_ANCIENT_STRENGTH: 32;
        readonly RP_ANCIENT_SIGHT: 33;
        readonly RP_ANCIENT_WILL: 34;
        readonly RP_PROTECT_ITEM: 35;
        readonly RP_RUINOUS_GRACE: 36;
        readonly RP_DAMPEN_MAGIC: 37;
        readonly RP_DAMPEN_RANGED: 38;
        readonly RP_DAMPEN_MELEE: 39;
        readonly RP_TRINITAS: 40;
        readonly RP_BERSERKER: 41;
        readonly RP_PURGE: 42;
        readonly RP_METABOLISE: 43;
        readonly RP_REBUKE: 44;
        readonly RP_VINDICATION: 45;
        readonly RP_DECIMATE: 46;
        readonly RP_ANNIHILATE: 47;
        readonly RP_VAPORISE: 48;
        readonly RP_FUMUS_VOW: 49;
        readonly RP_UMBRA_VOW: 50;
        readonly RP_CRUORS_VOW: 51;
        readonly RP_GLACIES_VOW: 52;
        readonly RP_WRATH: 53;
        readonly RP_INTENSIFY: 54;
    };

    /**
     * Overhead prayer/curse icon ordinals. Mirrors `titan::HeadIcon` in
     * [shared/titan/head_icon.h](shared/titan/head_icon.h). Added in SDK 39.
     */
    const HeadIcon: {
        readonly MELEE: 0;
        readonly RANGED: 1;
        readonly MAGIC: 2;
        readonly RETRIBUTION: 3;
        readonly SMITE: 4;
        readonly REDEMPTION: 5;
        readonly RANGE_MAGE: 6;
        readonly RANGE_MELEE: 7;
        readonly MAGE_MELEE: 8;
        readonly RANGE_MAGE_MELEE: 9;
        readonly WRATH: 10;
        readonly SOUL_SPLIT: 11;
        readonly DEFLECT_MELEE: 12;
        readonly DEFLECT_RANGE: 13;
        readonly DEFLECT_MAGE: 14;
    };

    /**
     * VarPlayer (varp) indices for common player state. Mirrors
     * `titan::VarPlayerID::*` in
     * [shared/titan/var_player.h](shared/titan/var_player.h). Pass to
     * `titan.state.vars.varp(...)`. Added in SDK 39.
     */
    const VarPlayerID: {
        readonly ATTACK_STYLE: 43;
        readonly SPECIAL_ATTACK: 301;
        readonly SPECIAL_ATTACK_ENABLED: 300;
        readonly RUN_ENABLED: 173;
        readonly POISON: 102;
        readonly AUTO_RETALIATE: 172;
        readonly DISEASE: 456;
        readonly WEIGHT: 451;
        readonly HP_HUD_1: 3209;
        readonly HP_HUD_2: 3210;
        readonly PRAYER_POINTS: 2382;
        readonly LAST_HOME_TELEPORT: 892;
        /** Return the catalog identifier, or null when unnamed. */
        nameOf(id: number): string | null;
    };

    /**
     * Client-side integer variable ids. Mirrors RuneLite's deprecated
     * `VarClientInt` compatibility catalog and `titan::VarClientInt::*` in
     * [shared/titan/var_client_int.h](shared/titan/var_client_int.h).
     * Added in SDK 61.
     */
    const VarClientInt: {
        readonly TOOLTIP_TIMEOUT: 1;
        readonly TOOLTIP_VISIBLE: 2;
        readonly INPUT_TYPE: 5;
        readonly BANK_SCROLL: 51;
        readonly CAMERA_ZOOM_FIXED_VIEWPORT: 73;
        readonly CAMERA_ZOOM_RESIZABLE_VIEWPORT: 74;
        readonly MEMBERSHIP_STATUS: 103;
        readonly INVENTORY_TAB: 171;
        readonly BLOCK_KEYPRESS: 187;
        readonly WORLD_MAP_SEARCH_FOCUSED: 190;
        /** Return the compatibility-catalog identifier, or null when unnamed. */
        nameOf(id: number): string | null;
    };

    /**
     * Client-side string variable ids. Mirrors RuneLite's deprecated
     * `VarClientStr` compatibility catalog and `titan::VarClientStr::*` in
     * [shared/titan/var_client_str.h](shared/titan/var_client_str.h).
     * Added in SDK 61.
     */
    const VarClientStr: {
        readonly CHATBOX_TYPED_TEXT: 335;
        readonly INPUT_TEXT: 359;
        readonly PRIVATE_MESSAGE_TARGET: 360;
        readonly RECENT_FRIENDS_CHAT: 362;
        readonly NOTIFICATION_TOP_TEXT: 387;
        readonly NOTIFICATION_BOTTOM_TEXT: 388;
        /** Return the compatibility-catalog identifier, or null when unnamed. */
        nameOf(id: number): string | null;
    };

    /**
     * Packed widget ids for the dialogue / combat helpers. Mirrors
     * `titan::InterfaceIds::*` in
     * [shared/titan/interface_ids.h](shared/titan/interface_ids.h).
     * Added in SDK 39.
     */
    const InterfaceIds: {
        readonly MakeButton: 17694734;
        readonly AutoRetaliate: 38862880;
        readonly QuestscrollClose: 10027024;
        readonly QuestscrollContent: 10027009;
        readonly DialogOptions: 14352385;
        readonly SpecOrb: 10485794;
        readonly MagicSpellbook: {
            readonly UNIVERSE: 14286848;
            readonly TOP: 14286849;
            readonly GLOW: 14286850;
            readonly SPELLLAYER: 14286851;
            readonly BACK_BUTTON: 14286852;
            readonly LEAGUE_HOME_TELEPORT: 14286853;
            readonly TELEPORT_HOME_STANDARD: 14286854;
            readonly TELEPORT_MINIGAME_STANDARD: 14286855;
            readonly TELEPORT_MINIGAME_ANCIENT: 14286856;
            readonly TELEPORT_MINIGAME_ARCEUUS: 14286857;
            readonly TELEPORT_MINIGAME_LUNAR: 14286858;
            readonly WIND_STRIKE: 14286859;
            readonly CONFUSE: 14286860;
            readonly XBOWS_ENCHANT: 14286861;
            readonly WATER_STRIKE: 14286862;
            readonly ENCHANT_JEWELLERY: 14286863;
            readonly ENCHANT_1: 14286864;
            readonly EARTH_STRIKE: 14286865;
            readonly WEAKEN: 14286866;
            readonly FIRE_STRIKE: 14286867;
            readonly BONES_BANANAS: 14286868;
            readonly WIND_BOLT: 14286869;
            readonly CURSE: 14286870;
            readonly BIND: 14286871;
            readonly LOW_ALCHEMY: 14286872;
            readonly WATER_BOLT: 14286873;
            readonly VARROCK_TELEPORT: 14286874;
            readonly ENCHANT_2: 14286875;
            readonly EARTH_BOLT: 14286876;
            readonly LUMBRIDGE_TELEPORT: 14286877;
            readonly TELEGRAB: 14286878;
            readonly FIRE_BOLT: 14286879;
            readonly FALADOR_TELEPORT: 14286880;
            readonly CRUMBLE_UNDEAD: 14286881;
            readonly TELEPORT_TO_YOUR_HOUSE: 14286882;
            readonly WIND_BLAST: 14286883;
            readonly SUPERHEAT: 14286884;
            readonly CAMELOT_TELEPORT: 14286885;
            readonly WATER_BLAST: 14286886;
            readonly KOUREND_TELEPORT: 14286887;
            readonly ENCHANT_3: 14286888;
            readonly IBAN_BLAST: 14286889;
            readonly SNARE: 14286890;
            readonly MAGIC_DART: 14286891;
            readonly ARDOUGNE_TELEPORT: 14286892;
            readonly EARTH_BLAST: 14286893;
            readonly FORTIS_TELEPORT: 14286894;
            readonly HIGH_ALCHEMY: 14286895;
            readonly CHARGE_WATER_ORB: 14286896;
            readonly ENCHANT_4: 14286897;
            readonly WATCHTOWER_TELEPORT: 14286898;
            readonly FIRE_BLAST: 14286899;
            readonly CHARGE_EARTH_ORB: 14286900;
            readonly BONES_PEACHES: 14286901;
            readonly SARADOMIN_STRIKE: 14286902;
            readonly CLAWS_OF_GUTHIX: 14286903;
            readonly FLAMES_OF_ZAMORAK: 14286904;
            readonly TROLLHEIM_TELEPORT: 14286905;
            readonly WIND_WAVE: 14286906;
            readonly CHARGE_FIRE_ORB: 14286907;
            readonly APE_TELEPORT: 14286908;
            readonly WATER_WAVE: 14286909;
            readonly CHARGE_AIR_ORB: 14286910;
            readonly VULNERABILITY: 14286911;
            readonly ENCHANT_5: 14286912;
            readonly EARTH_WAVE: 14286913;
            readonly ENFEEBLE: 14286914;
            readonly TELEOTHER_LUMBRIDGE: 14286915;
            readonly FIRE_WAVE: 14286916;
            readonly ENTANGLE: 14286917;
            readonly STUN: 14286918;
            readonly CHARGE: 14286919;
            readonly WIND_SURGE: 14286920;
            readonly TELEOTHER_FALADOR: 14286921;
            readonly WATER_SURGE: 14286922;
            readonly TELEPORT_BLOCK: 14286923;
            readonly BOUNTY_TARGET: 14286924;
            readonly ENCHANT_6: 14286925;
            readonly TELEOTHER_CAMELOT: 14286926;
            readonly EARTH_SURGE: 14286927;
            readonly ENCHANT_7: 14286928;
            readonly FIRE_SURGE: 14286929;
            readonly TELEPORT_BOAT_TO_ME: 14286930;
            readonly TELEPORT_ME_TO_BOAT: 14286931;
            readonly ICE_RUSH: 14286932;
            readonly ICE_BLITZ: 14286933;
            readonly ICE_BURST: 14286934;
            readonly ICE_BARRAGE: 14286935;
            readonly BLOOD_RUSH: 14286936;
            readonly BLOOD_BLITZ: 14286937;
            readonly BLOOD_BURST: 14286938;
            readonly BLOOD_BARRAGE: 14286939;
            readonly SMOKE_RUSH: 14286940;
            readonly SMOKE_BLITZ: 14286941;
            readonly SMOKE_BURST: 14286942;
            readonly SMOKE_BARRAGE: 14286943;
            readonly SHADOW_RUSH: 14286944;
            readonly SHADOW_BLITZ: 14286945;
            readonly SHADOW_BURST: 14286946;
            readonly SHADOW_BARRAGE: 14286947;
            readonly ZAROSTELEPORT1: 14286948;
            readonly ZAROSTELEPORT2: 14286949;
            readonly ZAROSTELEPORT3: 14286950;
            readonly ZAROSTELEPORT4: 14286951;
            readonly ZAROSTELEPORT5: 14286952;
            readonly ZAROSTELEPORT6: 14286953;
            readonly ZAROSTELEPORT7: 14286954;
            readonly ZAROSTELEPORT8: 14286955;
            readonly TELEPORT_HOME_ZAROS: 14286956;
            readonly TELEPORT_HOME_LUNAR: 14286957;
            readonly BAKE_PIE: 14286958;
            readonly CURE_PLANT: 14286959;
            readonly MONSTER_EXAMINE: 14286960;
            readonly NPC_CONTACT: 14286961;
            readonly CURE_OTHER: 14286962;
            readonly HUMIDIFY: 14286963;
            readonly TELE_MOONCLAN: 14286964;
            readonly TELE_GROUP_MOONCLAN: 14286965;
            readonly CURE_ME: 14286966;
            readonly HUNTER_KIT: 14286967;
            readonly TELE_WATERBIRTH: 14286968;
            readonly TELE_GROUP_WATERBIRTH: 14286969;
            readonly CURE_GROUP: 14286970;
            readonly STAT_SPY: 14286971;
            readonly TELE_BARB_OUT: 14286972;
            readonly TELE_GROUP_BARBARIAN: 14286973;
            readonly SUPERGLASS: 14286974;
            readonly TAN_LEATHER: 14286975;
            readonly TELE_KHAZARD: 14286976;
            readonly TELE_GROUP_KHAZARD: 14286977;
            readonly DREAM: 14286978;
            readonly STRING_JEWEL: 14286979;
            readonly REST_POT_SHARE: 14286980;
            readonly MAGIC_IMBUE: 14286981;
            readonly FERTILE_SOIL: 14286982;
            readonly STREN_POT_SHARE: 14286983;
            readonly TELE_FISH: 14286984;
            readonly TELE_GROUP_FISHING_GUILD: 14286985;
            readonly PLANK_MAKE: 14286986;
            readonly TELE_CATHER: 14286987;
            readonly TELE_GROUP_CATHERBY: 14286988;
            readonly RECHARGE_DRAGONSTONE: 14286989;
            readonly TELE_GHORROCK: 14286990;
            readonly TELE_GROUP_GHORROCK: 14286991;
            readonly ENERGY_TRANS: 14286992;
            readonly HEAL_OTHER: 14286993;
            readonly VENGEANCE_OTHER: 14286994;
            readonly VENGEANCE: 14286995;
            readonly HEAL_GROUP: 14286996;
            readonly SPELLBOOK_SWAP: 14286997;
            readonly GEOMANCY: 14286998;
            readonly SPIN_FLAX: 14286999;
            readonly OURANIA_TELEPORT: 14287000;
            readonly TELEPORT_HOME_ARCEUUS: 14287001;
            readonly REANIMATION_BASIC: 14287002;
            readonly TELEPORT_ARCEUUS_LIBRARY: 14287003;
            readonly REANIMATION_ADEPT: 14287004;
            readonly REANIMATION_EXPERT: 14287005;
            readonly REANIMATION_MASTER: 14287006;
            readonly TELEPORT_DRAYNOR_MANOR: 14287007;
            readonly NECROMANCY_DOG: 14287008;
            readonly TELEPORT_MIND_ALTAR: 14287009;
            readonly TELEPORT_RESPAWN: 14287010;
            readonly TELEPORT_SALVE_GRAVEYARD: 14287011;
            readonly TELEPORT_FENKENSTRAIN_CASTLE: 14287012;
            readonly TELEPORT_WEST_ARDOUGNE: 14287013;
            readonly TELEPORT_HARMONY_ISLAND: 14287014;
            readonly TELEPORT_CEMETERY: 14287015;
            readonly RESURRECT_CROPS: 14287016;
            readonly TELEPORT_BARROWS: 14287017;
            readonly TELEPORT_APE_ATOLL_DUNGEON: 14287018;
            readonly TELEPORT_BATTLEFRONT: 14287019;
            readonly INFERIOR_DEMONBANE: 14287020;
            readonly SUPERIOR_DEMONBANE: 14287021;
            readonly DARK_DEMONBANE: 14287022;
            readonly MARK_OF_DARKNESS: 14287023;
            readonly GHOSTLY_GRASP: 14287024;
            readonly SKELETAL_GRASP: 14287025;
            readonly UNDEAD_GRASP: 14287026;
            readonly WARD_OF_ARCEUUS: 14287027;
            readonly LESSER_CORRUPTION: 14287028;
            readonly GREATER_CORRUPTION: 14287029;
            readonly DEMONIC_OFFERING: 14287030;
            readonly SINISTER_OFFERING: 14287031;
            readonly DEGRIME: 14287032;
            readonly SHADOW_VEIL: 14287033;
            readonly VILE_VIGOUR: 14287034;
            readonly DARK_LURE: 14287035;
            readonly DEATH_CHARGE: 14287036;
            readonly RESURRECT_LESSER_GHOST: 14287037;
            readonly RESURRECT_LESSER_SKELETON: 14287038;
            readonly RESURRECT_LESSER_ZOMBIE: 14287039;
            readonly RESURRECT_SUPERIOR_GHOST: 14287040;
            readonly RESURRECT_SUPERIOR_SKELETON: 14287041;
            readonly RESURRECT_SUPERIOR_ZOMBIE: 14287042;
            readonly RESURRECT_GREATER_GHOST: 14287043;
            readonly RESURRECT_GREATER_SKELETON: 14287044;
            readonly RESURRECT_GREATER_ZOMBIE: 14287045;
            readonly MONSTER_INSPECT: 14287046;
            readonly TRANSMUTE_UPGRADE: 14287047;
            readonly TRANSMUTE_DOWNGRADE: 14287048;
            readonly INFOLAYER: 14287049;
            readonly INFOLAYER_GRAPHIC0: 14287050;
            readonly FILTERMENU_CONTAINER: 14287051;
            readonly FILTERMENU_CONTAINER_GRAPHIC0: 14287052;
            readonly FILTERMENU_CONTAINER_TEXT1: 14287053;
            readonly FILTERMENU: 14287054;
            readonly BOTTOM: 14287055;
            readonly INFOBUTTON: 14287056;
            readonly FILTERBUTTON: 14287057;
            readonly TOOLTIP: 14287058;
        };
    };

    /**
     * Named varbit ids. Mirrors `titan::Varbits::*` in
     * [shared/titan/varbits.h](shared/titan/varbits.h). Pass to
     * `titan.state.vars.varbit(...)`. Added in SDK 39.
     */
    const Varbits: {
        readonly QUICK_PRAYER: 4103;
        readonly PRAYER_THICK_SKIN: 4104;
        readonly PRAYER_BURST_OF_STRENGTH: 4105;
        readonly PRAYER_CLARITY_OF_THOUGHT: 4106;
        readonly PRAYER_SHARP_EYE: 4122;
        readonly PRAYER_MYSTIC_WILL: 4123;
        readonly PRAYER_ROCK_SKIN: 4107;
        readonly PRAYER_SUPERHUMAN_STRENGTH: 4108;
        readonly PRAYER_IMPROVED_REFLEXES: 4109;
        readonly PRAYER_RAPID_RESTORE: 4110;
        readonly PRAYER_RAPID_HEAL: 4111;
        readonly PRAYER_PROTECT_ITEM: 4112;
        readonly PRAYER_HAWK_EYE: 4124;
        readonly PRAYER_MYSTIC_LORE: 4125;
        readonly PRAYER_STEEL_SKIN: 4113;
        readonly PRAYER_ULTIMATE_STRENGTH: 4114;
        readonly PRAYER_INCREDIBLE_REFLEXES: 4115;
        readonly PRAYER_PROTECT_FROM_MAGIC: 4116;
        readonly PRAYER_PROTECT_FROM_MISSILES: 4117;
        readonly PRAYER_PROTECT_FROM_MELEE: 4118;
        readonly PRAYER_EAGLE_EYE: 4126;
        readonly PRAYER_MYSTIC_MIGHT: 4127;
        readonly PRAYER_RETRIBUTION: 4119;
        readonly PRAYER_REDEMPTION: 4120;
        readonly PRAYER_SMITE: 4121;
        readonly PRAYER_CHIVALRY: 4128;
        readonly PRAYER_PIETY: 4129;
        readonly PRAYER_PRESERVE: 5466;
        readonly PRAYER_RIGOUR: 5464;
        readonly PRAYER_AUGURY: 5465;
        readonly PRAYER_DEADEYE: 16090;
        readonly PRAYER_MYSTIC_VIGOUR: 16091;
        readonly PRAYER_RP_REJUVENATION: 14840;
        readonly PRAYER_RP_ANCIENT_STRENGTH: 14829;
        readonly PRAYER_RP_ANCIENT_SIGHT: 14830;
        readonly PRAYER_RP_ANCIENT_WILL: 14831;
        readonly PRAYER_RP_PROTECT_ITEM: 14966;
        readonly PRAYER_RP_RUINOUS_GRACE: 14841;
        readonly PRAYER_RP_DAMPEN_MAGIC: 14964;
        readonly PRAYER_RP_DAMPEN_RANGED: 14963;
        readonly PRAYER_RP_DAMPEN_MELEE: 14962;
        readonly PRAYER_RP_TRINITAS: 14832;
        readonly PRAYER_RP_BERSERKER: 14844;
        readonly PRAYER_RP_PURGE: 14839;
        readonly PRAYER_RP_METABOLISE: 14843;
        readonly PRAYER_RP_REBUKE: 14850;
        readonly PRAYER_RP_VINDICATION: 14851;
        readonly PRAYER_RP_DECIMATE: 14833;
        readonly PRAYER_RP_ANNIHILATE: 14834;
        readonly PRAYER_RP_VAPORISE: 14835;
        readonly PRAYER_RP_FUMUS_VOW: 14845;
        readonly PRAYER_RP_UMBRA_VOW: 14847;
        readonly PRAYER_RP_CRUORS_VOW: 14846;
        readonly PRAYER_RP_GLACIES_VOW: 14848;
        readonly PRAYER_RP_WRATH: 14842;
        readonly PRAYER_RP_INTENSIFY: 14965;
        readonly PRAYERBOOK: 14826;
        readonly SPELLBOOK: 4070;
        readonly SPELLBOOK_SUBMENU: 9730;
        readonly RUN_SLOWED_DEPLETION_ACTIVE: 25;
        readonly STAMINA_EFFECT: 24;
        readonly ANTIFIRE: 3981;
        readonly SUPER_ANTIFIRE: 6101;
        readonly MAGIC_IMBUE: 5438;
        readonly VENGEANCE_ACTIVE: 2450;
        readonly VENGEANCE_COOLDOWN: 2451;
        readonly IMBUED_HEART_COOLDOWN: 5361;
        readonly RING_OF_ENDURANCE_EFFECT: 10385;
        readonly DIVINE_SUPER_ATTACK: 8429;
        readonly DIVINE_SUPER_STRENGTH: 8430;
        readonly DIVINE_SUPER_DEFENCE: 8431;
        readonly DIVINE_RANGING: 8432;
        readonly DIVINE_MAGIC: 8433;
        readonly DIVINE_SUPER_COMBAT: 13663;
        readonly DIVINE_BASTION: 13664;
        readonly DIVINE_BATTLEMAGE: 13665;
        readonly DEATH_CHARGE: 12411;
        readonly DEATH_CHARGE_COOLDOWN: 12138;
        readonly RESURRECT_THRALL: 12413;
        readonly SHADOW_VEIL: 12414;
        readonly SHADOW_VEIL_COOLDOWN: 12291;
        readonly NMZ_OVERLOAD_REFRESHES_REMAINING: 3955;
        readonly COX_OVERLOAD_REFRESHES_REMAINING: 5418;
        readonly MULTICOMBAT_AREA: 4605;
        readonly IN_WILDERNESS: 5963;
        readonly PVP_SPEC_ORB: 8121;
        readonly ACCOUNT_TYPE: 1777;
        readonly EQUIPPED_WEAPON_TYPE: 357;
        readonly BOSS_HEALTH_CURRENT: 6099;
        readonly BOSS_HEALTH_MAXIMUM: 6100;
        readonly BOSS_HEALTH_OVERLAY: 12389;
        readonly SLAYER_POINTS: 4068;
        readonly SLAYER_TASK_STREAK: 4069;
        readonly SLAYER_TASK_BOSS: 4723;
        readonly SUPERIOR_ENABLED: 5362;
        readonly IN_RAID: 5432;
        readonly RAID_STATE: 5425;
        readonly RAID_TOTAL_POINTS: 5431;
        readonly THEATRE_OF_BLOOD: 6440;
        readonly TOA_RAID_LEVEL: 14380;
        readonly TOA_RAID_DAMAGE: 14325;
        readonly BANK_REARRANGE_MODE: 3959;
        readonly CURRENT_BANK_TAB: 4150;
        readonly BANK_QUANTITY_TYPE: 6590;
        readonly BANK_LEAVEPLACEHOLDERS: 3755;
        readonly DIARY_ARDOUGNE_EASY: 4458;
        readonly DIARY_ARDOUGNE_MEDIUM: 4459;
        readonly DIARY_ARDOUGNE_HARD: 4460;
        readonly DIARY_ARDOUGNE_ELITE: 4461;
        readonly DIARY_DESERT_EASY: 4483;
        readonly DIARY_DESERT_MEDIUM: 4484;
        readonly DIARY_DESERT_HARD: 4485;
        readonly DIARY_DESERT_ELITE: 4486;
        readonly DIARY_FALADOR_EASY: 4462;
        readonly DIARY_FALADOR_MEDIUM: 4463;
        readonly DIARY_FALADOR_HARD: 4464;
        readonly DIARY_FALADOR_ELITE: 4465;
        readonly DIARY_VARROCK_EASY: 4479;
        readonly DIARY_VARROCK_MEDIUM: 4480;
        readonly DIARY_VARROCK_HARD: 4481;
        readonly DIARY_VARROCK_ELITE: 4482;
        readonly DIARY_LUMBRIDGE_EASY: 4495;
        readonly DIARY_LUMBRIDGE_MEDIUM: 4496;
        readonly DIARY_LUMBRIDGE_HARD: 4497;
        readonly DIARY_LUMBRIDGE_ELITE: 4498;
        readonly DIARY_MORYTANIA_EASY: 4487;
        readonly DIARY_MORYTANIA_MEDIUM: 4488;
        readonly DIARY_MORYTANIA_HARD: 4489;
        readonly DIARY_MORYTANIA_ELITE: 4490;
        readonly DIARY_KANDARIN_EASY: 4475;
        readonly DIARY_KANDARIN_MEDIUM: 4476;
        readonly DIARY_KANDARIN_HARD: 4477;
        readonly DIARY_KANDARIN_ELITE: 4478;
        readonly DIARY_FREMENNIK_EASY: 4491;
        readonly DIARY_FREMENNIK_MEDIUM: 4492;
        readonly DIARY_FREMENNIK_HARD: 4493;
        readonly DIARY_FREMENNIK_ELITE: 4494;
        readonly DIARY_WILDERNESS_EASY: 4466;
        readonly DIARY_WILDERNESS_MEDIUM: 4467;
        readonly DIARY_WILDERNESS_HARD: 4468;
        readonly DIARY_WILDERNESS_ELITE: 4469;
        readonly DIARY_WESTERN_EASY: 4471;
        readonly DIARY_WESTERN_MEDIUM: 4472;
        readonly DIARY_WESTERN_HARD: 4473;
        readonly DIARY_WESTERN_ELITE: 4474;
        readonly DIARY_KARAMJA_EASY: 3578;
        readonly DIARY_KARAMJA_MEDIUM: 3599;
        readonly DIARY_KARAMJA_HARD: 3611;
        readonly DIARY_KARAMJA_ELITE: 4566;
        readonly DIARY_KOUREND_EASY: 7925;
        readonly DIARY_KOUREND_MEDIUM: 7926;
        readonly DIARY_KOUREND_HARD: 7927;
        readonly DIARY_KOUREND_ELITE: 7928;
        readonly TELEBLOCK: 4163;
        readonly NMZ_ABSORPTION: 3956;
        readonly NMZ_POINTS: 3949;
        readonly DRAGONFIRE_SHIELD_COOLDOWN: 6539;
        readonly MENAPHITE_REMEDY: 14448;
        readonly BUFF_STAT_BOOST: 14344;
        readonly COLOSSEUM_DOOM: 9801;
        readonly TRANSPARENT_CHATBOX: 4608;
        readonly SIDE_PANELS: 4607;
        readonly EXPERIENCE_TRACKER_POSITION: 4692;
    };

    const QuestID: {
        readonly COOKS_ASSISTANT: 17;
        readonly DRAGON_SLAYER_I: 31;
        readonly DRAGON_SLAYER_II: 32;
        readonly RECIPE_FOR_DISASTER: 117;
        readonly MONKEY_MADNESS_I: 95;
        readonly MONKEY_MADNESS_II: 96;
        readonly DESERT_TREASURE_I: 27;
        readonly DESERT_TREASURE_II: 2343;
        readonly SONG_OF_THE_ELVES: 137;
        readonly SINS_OF_THE_FATHER: 134;
        readonly A_NIGHT_AT_THE_THEATRE: 104;
        readonly PRIEST_IN_PERIL: 111;
        readonly LEGENDS_QUEST: 85;
        readonly REGICIDE: 119;
        readonly UNDERGROUND_PASS: 154;
        readonly WATERFALL_QUEST: 158;
        readonly LUNAR_DIPLOMACY: 88;
        readonly DREAM_MENTOR: 33;
        readonly THE_FREMENNIK_EXILES: 55;
        readonly MAKING_FRIENDS_WITH_MY_ARM: 91;
        readonly BONE_VOYAGE: 11;
        readonly X_MARKS_THE_SPOT: 162;
        readonly WHILE_GUTHIX_SLEEPS: 3467;
        readonly DEFENDER_OF_VARROCK: 3466;
        readonly THE_FINAL_DAWN: 5189;
    };
}
