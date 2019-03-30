package com.nhl.dflib;

import com.nhl.dflib.row.RowBuilder;

class ArrayRowBuilder implements RowBuilder {

    private Index index;
    private Object[] row;
    private boolean protect;

    public ArrayRowBuilder(Index index) {
        this.index = index;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public void set(String columnName, Object value) {
        rowToWrite()[index.position(columnName)] = value;
    }

    @Override
    public void set(int columnPos, Object value) {
        rowToWrite()[columnPos] = value;
    }

    @Override
    public void setRange(Object[] values, int fromOffset, int toOffset, int len) {

        if (len + toOffset > index.size()) {
            throw new IllegalArgumentException("Provided values won't fit in the row: "
                    + (len + toOffset)
                    + " > "
                    + index.size());
        }

        // if the bounds match, avoid cloning the rows (this is a very visible optimization),
        // but in this case will need to prevent the original row modification

        // TODO: any way to do this range checking once per DataFrame instead of per row?
        if (toOffset == 0 && fromOffset == 0 && len == index.size() && len + fromOffset <= values.length) {
            this.protect = true;
            this.row = values;
        } else {
            Object[] target = rowToWrite();
            System.arraycopy(values, fromOffset, target, toOffset, len);
        }
    }

    private Object[] rowToWrite() {

        if (row == null) {
            row = new Object[index.size()];
            return row;
        }

        if (protect) {
            Object[] clone = new Object[index.size()];
            System.arraycopy(row, 0, clone, 0, Math.min(row.length, clone.length));
            row = clone;
            protect = false;
            return clone;
        }

        return row;
    }

    public Object[] reset() {

        // ignoring "protect" flag here, as we are done building the row..
        Object[] returned = (row == null) ? new Object[index.size()] : row;

        this.row = null;
        this.protect = false;
        return returned;
    }
}

