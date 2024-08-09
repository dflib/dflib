package org.dflib.parquet.read;

import java.util.Arrays;

public class Row {

    private final Object[] columns;

    public Row(int size) {
        columns = new Object[size];
    }

    public void set(int idx, Object value) {
        columns[idx] = value;
    }

    public Object get(int idx) {
        return columns[idx];
    }

    public void resetParams() {
        Arrays.fill(columns, null);
    }

}
