package net.titan.api.internal;

public interface LiveStateBackend {
    default net.titan.api.ActorAnimationProgress actorAnimationProgress(net.titan.api.Actor actor) {
        return net.titan.api.ActorAnimationProgress.failure(net.titan.api.ActorDataStatus.UNAVAILABLE);
    }
    default net.titan.api.NpcModelOverrides npcModelOverrides(net.titan.api.NPC npc) {
        return net.titan.api.NpcModelOverrides.failure(net.titan.api.ActorDataStatus.UNAVAILABLE);
    }
    default String actorOverheadText(long entityPtr) { return null; }
    /** SDK 159 op base at {@code address}; null when the host cannot read it. */
    default String widgetName(net.titan.api.WidgetAddress address) { return null; }
    /**
     * SDK 159 op slots at {@code address}, null where empty; an empty array when
     * the widget has no op array; null when the host cannot read them.
     */
    default String[] widgetActions(net.titan.api.WidgetAddress address) { return null; }
    default Integer actorOverheadTextCycles(long entityPtr) { return null; }
    /** SDK 162 unlockCam flag; null when the host or revision lacks it. */
    default Boolean cameraPitchUnlocked() { return null; }
    /** SDK 162 game-thread write; true only when the host wrote it. */
    default boolean setCameraPitchUnlocked(boolean unlocked) { return false; }
    /** SDK 162 camera-input tilt; null when the host or revision lacks it. */
    default Integer cameraPitchTarget() { return null; }
    default boolean setCameraPitchTarget(int pitch) { return false; }
    /** SDK 162 {smallViewport, largeViewport}; null when unavailable. */
    default int[] viewportFov() { return null; }
    default boolean setViewportFov(int smallViewport, int largeViewport) { return false; }
    /** SDK 162 {fovMin, fovMax, zoomMin, zoomMax}; null when unavailable. */
    default int[] viewportFovClamp() { return null; }
    default boolean setViewportFovClamp(int fovMin, int fovMax, int zoomMin, int zoomMax) { return false; }
    <T> T current(T value);
    boolean exists(Object value);
    String identity(Object value);
    <T> T snapshot(T value);
}
