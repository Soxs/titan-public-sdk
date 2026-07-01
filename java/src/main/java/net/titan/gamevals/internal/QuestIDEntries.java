package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class QuestIDEntries {
    private QuestIDEntries() {}

    public static QuestEntry[] entries() {
        QuestEntry[] out = new QuestEntry[213];
        int offset = 0;
        offset = copy(out, offset, QuestIDEntries_0.entries());
        return out;
    }

    private static int copy(QuestEntry[] out, int offset, QuestEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<QuestEntry> byQuestId(int questId) {
        Optional<QuestEntry> hit;
        hit = QuestIDEntries_0.byQuestId(questId);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

    public static Optional<QuestEntry> byRowId(int rowId) {
        Optional<QuestEntry> hit;
        hit = QuestIDEntries_0.byRowId(rowId);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
