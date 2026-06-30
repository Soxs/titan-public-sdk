package net.titan.api.internal;

import net.titan.api.ScreenPoint;

import java.util.Optional;

public interface MouseBackend {
    default Optional<ScreenPoint> resolveActionClickPoint(long opcode, int identifier,
                                                          int param0, int param1,
                                                          long worldViewId) {
        return resolveActionClickPoint(
            opcode, identifier, param0, param1, worldViewId,
            -1, 1, 1, -1, 0, 0);
    }

    Optional<ScreenPoint> resolveActionClickPoint(long opcode, int identifier,
                                                  int param0, int param1,
                                                  long worldViewId,
                                                  int targetPlane,
                                                  int targetSizeX,
                                                  int targetSizeY,
                                                  int targetLayer,
                                                  long targetEntityPtr,
                                                  long targetPackedId);
}
