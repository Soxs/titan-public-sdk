package net.titan.gamevals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamevalsCatalogTest {
    @Test
    void exposesGeneratedConstantsAndLookups() {
        assertTrue(ItemID.ABYSSAL_WHIP > 0);
        assertTrue(ObjectID.BANKBOOTH > 0);
        assertEquals(963, NpcID.KALPHITE_QUEEN);
        assertEquals(128, NpcID.POH_MOUNTED_KQ);
        assertEquals(3028, NpcID.GOBLIN);
        assertEquals(12773, ItemID.ABYSSAL_WHIP_LAVA);
        assertEquals(10355, ObjectID.BANKBOOTH);
        assertEquals(5465, VarbitID.PRAYER_AUGURY);
        assertEquals(95, InventoryID.BANK);
        assertEquals(62, VarPlayerID.GOBLINQUEST);
        assertEquals(51, VarClientID.BANK_SCROLLPOS);
        assertEquals(308, AnimationID.GOBLIN_WALK);
        assertEquals(341, SpotanimID.SP_ATTACK_ABYSSAL_WHIP);
        assertEquals(0, DBTableID.Quest.Row.QUEST_ANIMALMAGNETISM);
        assertEquals(219, InterfaceID.CHATMENU);
        assertEquals(14352385, InterfaceID.Chatmenu.OPTIONS);
        assertEquals(InterfaceID.Chatmenu.OPTIONS, InterfaceID.pack(InterfaceID.CHATMENU, 1));
        assertEquals(123, QuestID.QUEST_ANIMALMAGNETISM);

        GamevalEntry item = ItemID.byId(ItemID.ABYSSAL_WHIP).orElseThrow();
        GamevalEntry object = ObjectID.byId(ObjectID.BANKBOOTH).orElseThrow();
        GamevalEntry npc = NpcID.byId(NpcID.GOBLIN).orElseThrow();
        GamevalEntry interfaceGroup = InterfaceID.byId(InterfaceID.CHATMENU).orElseThrow();
        QuestEntry quest = QuestID.byQuestId(QuestID.QUEST_ANIMALMAGNETISM).orElseThrow();
        QuestEntry questByRow = QuestID.byRowId(0).orElseThrow();

        assertEquals("objtypes", item.sourceCatalog());
        assertEquals("loctypes", object.sourceCatalog());
        assertEquals("npctypes", npc.sourceCatalog());
        assertEquals("iftypes", interfaceGroup.sourceCatalog());
        assertEquals("questtypes", quest.sourceCatalog());
        assertEquals(0, quest.rowId());
        assertEquals(QuestID.QUEST_ANIMALMAGNETISM, questByRow.questId());
    }
}
