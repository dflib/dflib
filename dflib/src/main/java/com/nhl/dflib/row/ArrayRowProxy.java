package com.nhl.dflib.row;

import com.nhl.dflib.Index;

public class ArrayRowProxy implements RowProxy {

    private Index index;
    private Object[] row;

    public ArrayRowProxy(Index index) {
        this.index = index;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public Object get(int columnPos) {
        return index.get(row, columnPos);
    }

    @Override
    public Object get(String columnName) {
        return index.get(row, columnName);
    }

    @Override
    public void copyTo(RowBuilder to, int targetOffset) {
        // row can be null in joins...
        if (row != null) {
            index.compactCopy(row, to, targetOffset);
        }
    }

    public ArrayRowProxy reset(Object[] row) {
        this.row = row;
        return this;
    }
}
