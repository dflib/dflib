package com.nhl.dflib;

public class IndexPosition {

    private int ordinal;
    private int position;
    private String name;

    public IndexPosition(int ordinal, int position, String name) {
        this.ordinal = ordinal;
        this.position = position;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public int ordinal() {
        return ordinal;
    }

    public int position() {
        return position;
    }

    public Object get(Object[] row) {
        return row[position];
    }

    public void set(Object[] row, Object val) {
        row[position] = val;
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
        return ip.ordinal == this.ordinal && ip.position == this.position && ip.name.equals(this.name);
    }

    @Override
    public String toString() {
        return "IndexPosition [" + ordinal + "|" + position + "|" + name + "]";
    }
}
