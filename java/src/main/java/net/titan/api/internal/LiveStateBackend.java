package net.titan.api.internal;

public interface LiveStateBackend {
    default net.titan.api.ActorAnimationProgress actorAnimationProgress(net.titan.api.Actor actor) {
        return net.titan.api.ActorAnimationProgress.failure(net.titan.api.ActorDataStatus.UNAVAILABLE);
    }
    default net.titan.api.NpcModelOverrides npcModelOverrides(net.titan.api.NPC npc) {
        return net.titan.api.NpcModelOverrides.failure(net.titan.api.ActorDataStatus.UNAVAILABLE);
    }
    default String actorOverheadText(long entityPtr) { return null; }
    default Integer actorOverheadTextCycles(long entityPtr) { return null; }
    <T> T current(T value);
    boolean exists(Object value);
    String identity(Object value);
    <T> T snapshot(T value);
}
