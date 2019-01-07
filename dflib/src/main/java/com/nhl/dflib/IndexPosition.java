package com.nhl.dflib;

public class IndexPosition {

    private int ordinal;
    private int rowIndex;
    private String name;

    public IndexPosition(int ordinal, int rowIndex, String name) {
        this.ordinal = ordinal;
        this.rowIndex = rowIndex;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public int ordinal() {
        return ordinal;
    }

    public int rowIndex() {
        return rowIndex;
    }

    public Object get(Object[] row) {
        return row[rowIndex];
    }

    public void set(Object[] row, Object val) {
        row[rowIndex] = val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof IndexPosition)) {
            return false;
        }

        IndexPosition ip = (IndexPosition) o;
        return ip.ordinal == this.ordinal && ip.rowIndex == this.rowIndex && ip.name.equals(this.name);
    }

    @Override
    public String toString() {
        return "IndexPosition [" + ordinal + "|" + rowIndex + "|" + name + "]";
    }
}
