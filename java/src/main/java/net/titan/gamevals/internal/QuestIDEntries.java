package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class QuestIDEntries {
    private QuestIDEntries() {}

    public static QuestEntry[] entries() {
        QuestEntry[][] parts = {
            QuestIDEntries_0.entries(),
        };
        int size = 0;
        for (QuestEntry[] part : parts) size += part.length;
        QuestEntry[] out = new QuestEntry[size];
        int offset = 0;
        for (QuestEntry[] part : parts) {
            System.arraycopy(part, 0, out, offset, part.length);
            offset += part.length;
        }
        return out;
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
