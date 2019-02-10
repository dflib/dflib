package com.nhl.dflib.row;

import com.nhl.dflib.Index;

public class ArrayRowBuilder implements RowBuilder {

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
        index.set(rowToWrite(), columnName, value);
    }

    @Override
    public void set(int columnPos, Object value) {
        // we are dealing with compact row here .. can write directly to array bypassing the index
        rowToWrite()[columnPos] = value;
    }
    
    @Override
    public void bulkSet(Object[] values, int offset, int length) {

        // very visible optimization - not cloning the row saves us lots of cycles...
        if (offset == 0 && length == index.size() && length == values.length) {
            // reusing the original row... need to prevent further modification
            this.protect = true;
            this.row = values;
            return;
        }

        if (length + offset > index.size()) {
            throw new IllegalArgumentException("Provided values won't fit in the row: "
                    + (length + offset)
                    + " > "
                    + index.size());
        }

        Object[] target = rowToWrite();
        if (length > 0) {
            System.arraycopy(values, 0, target, offset, length);
        }
    }

    private Object[] rowToWrite() {

        if (row == null) {
            row = new Object[index.size()];
            return row;
        }

        if (protect) {
            Object[] clone = new Object[index.size()];
            System.arraycopy(row, 0, clone, 0, row.length);
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
