package net.titan.api;

import java.util.List;
import java.util.Objects;

/** Owned stored runtime body-model IDs; never filled with definition models. */
public final class NpcModelOverrides {
    private final ActorDataStatus status;
    private final List<Integer> modelIds;
    public NpcModelOverrides(ActorDataStatus status, List<Integer> modelIds) {
        this.status = Objects.requireNonNull(status);
        this.modelIds = status == ActorDataStatus.ACTIVE ? List.copyOf(modelIds) : List.of();
    }
    public static NpcModelOverrides failure(ActorDataStatus status) {
        return new NpcModelOverrides(status, List.of());
    }
    public ActorDataStatus status() { return status; }
    public boolean active() { return status == ActorDataStatus.ACTIVE; }
    public boolean available() { return active() || status == ActorDataStatus.NONE; }
    /** Empty is valid with ACTIVE: native omitted/empty replacement lists are indistinguishable. */
    public List<Integer> modelIds() { return modelIds; }
}
