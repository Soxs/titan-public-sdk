package net.titan.gamevals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamevalsCatalogTest {
    @Test
    void exposesGeneratedConstantsAndLookups() {
        assertTrue(ItemID.ABYSSAL_WHIP > 0);
        assertTrue(ObjectID.BANK > 0);
        assertTrue(NpcID.GOBLIN > 0);
        assertEquals(219, InterfaceID.CHATMENU);
        assertEquals(14352385, InterfaceID.Chatmenu.OPTIONS);
        assertEquals(InterfaceID.Chatmenu.OPTIONS, InterfaceID.pack(InterfaceID.CHATMENU, 1));
        assertEquals(123, QuestID.ANIMAL_MAGNETISM);

        GamevalEntry item = ItemID.byId(ItemID.ABYSSAL_WHIP).orElseThrow();
        GamevalEntry object = ObjectID.byId(ObjectID.BANK).orElseThrow();
        GamevalEntry npc = NpcID.byId(NpcID.GOBLIN).orElseThrow();
        GamevalEntry interfaceGroup = InterfaceID.byId(InterfaceID.CHATMENU).orElseThrow();
        QuestEntry quest = QuestID.byQuestId(QuestID.ANIMAL_MAGNETISM).orElseThrow();
        QuestEntry questByRow = QuestID.byRowId(0).orElseThrow();

        assertEquals("objtypes", item.sourceCatalog());
        assertEquals("loctypes", object.sourceCatalog());
        assertEquals("npctypes", npc.sourceCatalog());
        assertEquals("iftypes", interfaceGroup.sourceCatalog());
        assertEquals("questtypes", quest.sourceCatalog());
        assertEquals(0, quest.rowId());
        assertEquals(QuestID.ANIMAL_MAGNETISM, questByRow.questId());
    }
}
