package net.titan.api.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Checkbox-grid plugin setting: row labels down the left, column labels across
/// the top, a checkbox in each cell. Collapses a run of repetitive per-row
/// booleans into one control. Mirrors the C++ {@code titan::MatrixSetting}.
///
/// The value is a cell bitmask, {@code bit = row * columnCount() + column}
/// (row-major). {@link #MAX_CELLS} caps the grid at 31 cells so bit 31 is never
/// used and the mask is always a non-negative {@code int} on the wire.
///
/// Cells outside {@link #availability()} do not exist: the renderer leaves a
/// blank gap, and every mutator here drops their bits, so a value persisted
/// against an older grid shape can never resurrect a cell the plugin has since
/// removed.
///
/// Runtime-constructed like {@link BoolSetting}. Plugin authors declare a matrix
/// with {@link ConfigItem#columns()} / {@link ConfigItem#rows()} on an abstract
/// {@code boolean[][]} config method, not by instantiating this.
public final class MatrixSetting implements ConfigSetting {
    /// Maximum addressable cells (rows * columns). Bit 31 is deliberately
    /// unused so the mask survives Java's signed int and the protocol's int32
    /// value unchanged.
    public static final int MAX_CELLS = 31;

    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private volatile boolean hidden;
    private final int defaultValue;
    private final int availability;
    private final List<String> rowLabels;
    private final List<String> columnLabels;
    private volatile int value;

    public MatrixSetting(String key, String name, String section,
                         String description, int position, boolean hidden,
                         int defaultValue, int availability,
                         List<String> rowLabels, List<String> columnLabels) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.availability = availability;
        this.defaultValue = defaultValue & availability;
        this.rowLabels = rowLabels == null ? Collections.<String>emptyList()
            : Collections.unmodifiableList(new ArrayList<>(rowLabels));
        this.columnLabels = columnLabels == null ? Collections.<String>emptyList()
            : Collections.unmodifiableList(new ArrayList<>(columnLabels));
        this.value = this.defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public int rowCount() { return rowLabels.size(); }
    public int columnCount() { return columnLabels.size(); }
    public List<String> rowLabels() { return rowLabels; }
    public List<String> columnLabels() { return columnLabels; }

    /// Bitmask of cells that exist and are interactive. Travels in the protocol
    /// Setting's matrixAvailable field.
    public int availability() { return availability; }

    /// Cell bitmask the plugin declared as its default.
    public int defaultValue() { return defaultValue; }

    /// Raw cell bitmask.
    public int get() { return value; }

    /// Replace the whole mask. Bits outside {@link #availability()} are dropped.
    public synchronized void set(int value) { this.value = value & availability; }

    public boolean isAvailable(int row, int column) { return bit(row, column) != 0; }

    /// False for an out-of-range or unavailable cell.
    public boolean get(int row, int column) {
        int b = bit(row, column);
        return b != 0 && (value & b) != 0;
    }

    /// No-op on an out-of-range or unavailable cell.
    public synchronized void set(int row, int column, boolean on) {
        int b = bit(row, column);
        if (b == 0) return;
        value = on ? (value | b) : (value & ~b);
    }

    /// Flips the cell and returns its new state; false (no change) when the cell
    /// is out of range or unavailable.
    public synchronized boolean toggle(int row, int column) {
        int b = bit(row, column);
        if (b == 0) return false;
        boolean next = (value & b) == 0;
        value = next ? (value | b) : (value & ~b);
        return next;
    }

    /// Fresh row-major snapshot, {@code rowCount() x columnCount()}. Never
    /// aliases the live mask, so callers may keep or mutate it freely -- but it
    /// is an allocation, so hoist it out of per-tick loops.
    public boolean[][] toGrid() {
        int snapshot = value;
        int rows = rowCount();
        int columns = columnCount();
        boolean[][] out = new boolean[rows][columns];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                int b = 1 << (r * columns + c);
                out[r][c] = (availability & b) != 0 && (snapshot & b) != 0;
            }
        }
        return out;
    }

    /// Replace every cell from a row-major grid. Short or null rows and
    /// unavailable cells read as false.
    public synchronized void setGrid(boolean[][] grid) {
        int rows = rowCount();
        int columns = columnCount();
        int mask = 0;
        for (int r = 0; grid != null && r < rows && r < grid.length; ++r) {
            boolean[] row = grid[r];
            if (row == null) continue;
            for (int c = 0; c < columns && c < row.length; ++c) {
                if (row[c]) mask |= 1 << (r * columns + c);
            }
        }
        value = mask & availability;
    }

    private int bit(int row, int column) {
        int columns = columnCount();
        if (row < 0 || column < 0 || row >= rowCount() || column >= columns) return 0;
        int b = 1 << (row * columns + column);
        return (availability & b) != 0 ? b : 0;
    }

    @Override
    public Kind kind() { return Kind.MATRIX; }
}
