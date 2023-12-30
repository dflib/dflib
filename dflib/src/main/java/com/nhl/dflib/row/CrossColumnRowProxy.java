package com.nhl.dflib.row;

import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

public class CrossColumnRowProxy implements RowProxy {

    private final Index columns;
    private final Series[] data;
    private final int height;

    private int rowIndex;

    public CrossColumnRowProxy(Index columns, Series[] data, int height) {
        this.columns = columns;
        this.data = data;
        this.height = height;
        this.rowIndex = -1;
    }

    @Override
    public Index getIndex() {
        return columns;
    }

    @Override
    public Object get(int columnPos) {
        return data[columnPos].get(rowIndex);
    }

    @Override
    public Object get(String columnName) {
        return data[columns.position(columnName)].get(rowIndex);
    }

    @Override
    public int getInt(int columnPos) {
        return data[columnPos].castAsInt().getInt(rowIndex);
    }

    @Override
    public int getInt(String columnName) {
        return getInt(columns.position(columnName));
    }

    @Override
    public long getLong(int columnPos) {
        return data[columnPos].castAsLong().getLong(rowIndex);
    }

    @Override
    public long getLong(String columnName) {
        return getLong(columns.position(columnName));
    }

    @Override
    public double getDouble(int columnPos) {
        return data[columnPos].castAsDouble().getDouble(rowIndex);
    }

    @Override
    public double getDouble(String columnName) {
        return getDouble(columns.position(columnName));
    }

    @Override
    public boolean getBool(int columnPos) {
        return data[columnPos].castAsBool().getBool(rowIndex);
    }

    @Override
    public boolean getBool(String columnName) {
        return getBool(columns.position(columnName));
    }

    @Override
    public void copyRange(RowBuilder to, int fromOffset, int toOffset, int len) {
        // row can be missing in joins...
        if (rowIndex >= 0) {
            for (int i = 0; i < columns.size(); i++) {
                to.set(i + toOffset, data[i].get(rowIndex));
            }
        }
    }

    public boolean hasNext() {
        return rowIndex + 1 < height;
    }

    public CrossColumnRowProxy rewind() {
        this.rowIndex++;
        return this;
    }

    public CrossColumnRowProxy rewind(int index) {
        this.rowIndex = index;
        return this;
    }
}
