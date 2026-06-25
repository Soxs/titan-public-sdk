package net.titan.gamevals;

public final class QuestEntry {
    private final int rowId;
    private final int questId;
    private final String constant;
    private final String name;
    private final String sortName;
    private final String description;
    private final String sourceCatalog;

    public QuestEntry(int rowId, int questId, String constant, String name,
                      String sortName, String description, String sourceCatalog) {
        this.rowId = rowId;
        this.questId = questId;
        this.constant = constant;
        this.name = name;
        this.sortName = sortName;
        this.description = description;
        this.sourceCatalog = sourceCatalog;
    }

    public int rowId() {
        return rowId;
    }

    public int questId() {
        return questId;
    }

    public String constant() {
        return constant;
    }

    public String name() {
        return name;
    }

    public String sortName() {
        return sortName;
    }

    public String description() {
        return description;
    }

    public String sourceCatalog() {
        return sourceCatalog;
    }
}
