package net.titan.api.internal;

import net.titan.api.TileObject;
import net.titan.api.WidgetAddress;

public interface InteractionBackend {
    boolean interactNpcByIndex(String action, int hashIndex);
    default boolean interactNpcByIndex(String action, int hashIndex, int worldViewId) {
        return interactNpcByIndex(action, hashIndex);
    }
    boolean interactTileObject(String action, TileObject object);
    boolean interactGroundItem(String action, int itemId, int tileX, int tileY);
    default boolean interactGroundItem(String action, int itemId, int tileX, int tileY,
                                       int worldViewId) {
        return interactGroundItem(action, itemId, tileX, tileY);
    }

    boolean interactInventoryItem(int itemId, String action);
    boolean interactInventoryItemAtSlot(int slot, int itemId, String action);
    boolean useInventoryItemOnItem(int srcSlot, int srcItemId,
                                   int targetSlot, int targetItemId);
    boolean useInventoryItemOnNpc(int srcSlot, int srcItemId, int npcHashIndex);
    boolean useInventoryItemOnObject(int srcSlot, int srcItemId,
                                     int objectId, int tileX, int tileY);

    boolean widgetInteract(int opcode, int identifier, int param0, int param1);
    boolean widgetInteractAtPath(WidgetAddress address, int opcode,
                                 int identifier, int childSlot);
    boolean setWidgetText(int packedId, String text);
    boolean setWidgetTextAtPath(WidgetAddress address, String text);
}
