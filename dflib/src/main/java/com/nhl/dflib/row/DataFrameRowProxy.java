package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

public class DataFrameRowProxy implements RowProxy {

    private DataFrame dataFrame;
    private int rowIndex;
    private int height;

    public DataFrameRowProxy(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.height = dataFrame.height();
        this.rowIndex = -1;
    }

    @Override
    public Index getIndex() {
        return dataFrame.getColumnsIndex();
    }

    @Override
    public Object get(int columnPos) {
        return dataFrame.getColumn(columnPos).get(rowIndex);
    }

    @Override
    public Object get(String columnName) {
        return dataFrame.getColumn(columnName).get(rowIndex);
    }

    @Override
    public void copyRange(RowBuilder to, int fromOffset, int toOffset, int len) {
        // row can be missing in joins...
        if (rowIndex >= 0) {
            int w = dataFrame.width();
            for (int i = 0; i < w; i++) {
                to.set(i + toOffset, dataFrame.getColumn(i).get(rowIndex));
            }
        }
    }

    public boolean hasNext() {
        return rowIndex + 1 < height;
    }

    public DataFrameRowProxy rewind() {
        this.rowIndex++;
        return this;
    }

    public DataFrameRowProxy rewind(int index) {
        this.rowIndex = index;
        return this;
    }
}
