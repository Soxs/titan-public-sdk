package net.titan.gamevals;

public final class GamevalEntry {
    private final int id;
    private final String constant;
    private final String name;
    private final String sourceCatalog;

    public GamevalEntry(int id, String constant, String name, String sourceCatalog) {
        this.id = id;
        this.constant = constant;
        this.name = name;
        this.sourceCatalog = sourceCatalog;
    }

    public int id() {
        return id;
    }

    public String constant() {
        return constant;
    }

    public String name() {
        return name;
    }

    public String sourceCatalog() {
        return sourceCatalog;
    }
}
