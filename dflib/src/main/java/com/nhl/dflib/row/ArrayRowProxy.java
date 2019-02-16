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
    public void copyRange(RowBuilder to, int fromOffset, int toOffset, int len) {
        // row can be null in joins...
        if (row != null) {

            // TODO: may be a hotspot?
            if (index.isCompact()) {
                to.setRange(row, fromOffset, toOffset, len);
            } else {
                for (int i = 0; i < index.size(); i++) {
                    to.set(i + toOffset, index.get(row, i));
                }
            }
        }
    }

    public ArrayRowProxy reset(Object[] row) {
        this.row = row;
        return this;
    }
}
