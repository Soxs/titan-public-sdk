package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.QuestIDEntries;

public final class QuestID
        implements QuestIDConstants_0 {
    private QuestID() {}

    public static final String SOURCE_CATALOG = "questtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static QuestEntry[] entries() { return QuestIDEntries.entries(); }

    public static Optional<QuestEntry> byQuestId(int questId) {
        return QuestIDEntries.byQuestId(questId);
    }

    public static Optional<QuestEntry> byRowId(int rowId) {
        return QuestIDEntries.byRowId(rowId);
    }
}
