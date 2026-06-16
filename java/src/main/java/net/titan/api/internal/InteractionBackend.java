package net.titan.api.internal;

import net.titan.api.TileObject;
import net.titan.api.WidgetAddress;

public interface InteractionBackend {
    boolean interactNpcByIndex(String action, int hashIndex);
    boolean interactTileObject(String action, TileObject object);
    boolean interactGroundItem(String action, int itemId, int tileX, int tileY);

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
