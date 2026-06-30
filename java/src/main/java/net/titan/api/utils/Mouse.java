package net.titan.api.utils;

import net.titan.api.ScreenPoint;
import net.titan.api.internal.TitanRuntime;

import java.util.Optional;

public final class Mouse {
    private Mouse() {}

    public static Optional<ScreenPoint> resolveActionClickPoint(long opcode, int identifier,
                                                                 int param0, int param1,
                                                                 long worldViewId) {
        return TitanRuntime.getMouseBackend().resolveActionClickPoint(
            opcode, identifier, param0, param1, worldViewId);
    }

    public static Optional<ScreenPoint> resolveActionClickPoint(long opcode, int identifier,
                                                                 int param0, int param1,
                                                                 long worldViewId,
                                                                 int targetPlane,
                                                                 int targetSizeX,
                                                                 int targetSizeY,
                                                                 int targetLayer,
                                                                 long targetEntityPtr,
                                                                 long targetPackedId) {
        return TitanRuntime.getMouseBackend().resolveActionClickPoint(
            opcode, identifier, param0, param1, worldViewId,
            targetPlane, targetSizeX, targetSizeY, targetLayer,
            targetEntityPtr, targetPackedId);
    }
}
