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
    <T> T current(T value);
    boolean exists(Object value);
    String identity(Object value);
    <T> T snapshot(T value);
}
