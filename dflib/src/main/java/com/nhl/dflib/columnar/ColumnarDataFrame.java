package com.nhl.dflib.columnar;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;

public class ColumnarDataFrame implements DataFrame {

    private Index columnsIndex;
    private Series[] columnsData;

    public ColumnarDataFrame(Index columnsIndex, Series[] columnsData) {
        this.columnsIndex = columnsIndex;
        this.columnsData = columnsData;
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data column-by-column.
     */
    public static DataFrame fromColumnSequence(Index columns, Object... sequence) {

        int w = columns.size();
        int lastColumnH = sequence.length % w;
        boolean partialLastColumn = lastColumnH > 0;
        int fullColumnsW = partialLastColumn
                ? w - 1
                : w;

        int h = partialLastColumn
                ? 1 + sequence.length / w
                : sequence.length / w;

        Object[][] data = new Object[w][h];

        for (int i = 0; i < fullColumnsW; i++) {
            System.arraycopy(sequence, i * h, data[i], 0, h);
        }

        if (partialLastColumn) {
            System.arraycopy(sequence, fullColumnsW * h, data[fullColumnsW], 0, lastColumnH);
        }

        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(data[i]);
        }

        return new ColumnarDataFrame(columns, series);
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data row-by-row. This is NOT a very efficient
     * way to initialize a columnar DataFrame. Use {@link #fromColumnSequence(Index, Object...)} when possible.
     */
    public static DataFrame fromRowSequence(Index columns, Object... sequence) {

        int width = columns.size();
        int lastRowWidth = sequence.length % width;

        int minHeight = sequence.length / width;
        int fullHeight = lastRowWidth > 0 ? minHeight + 1 : minHeight;

        Object[][] data = new Object[width][fullHeight];

        for (int i = 0; i < minHeight; i++) {
            for (int j = 0; j < width; j++) {
                data[j][i] = sequence[i * width + j];
            }
        }

        if (lastRowWidth > 0) {
            int lastRowIndex = minHeight;
            for (int j = 0; j < lastRowWidth; j++) {
                data[j][lastRowIndex] = sequence[lastRowIndex * width + j];
            }
        }

        Series[] series = new Series[width];

        for (int i = 0; i < width; i++) {
            series[i] = new ArraySeries(data[i]);
        }

        return new ColumnarDataFrame(columns, series);
    }

    @Override
    public int height() {
        return columnsData.length > 0 ? columnsData[0].size() : 0;
    }

    @Override
    public Index getColumns() {
        return columnsIndex;
    }

    @Override
    public <V> DataFrame addColumns(String[] columnNames, RowToValueMapper<V>... columnValueProducers) {

        int width = width();
        int extraWidth = columnNames.length;
        Index expandedIndex = columnsIndex.addNames(columnNames);

        Series[] newData = new Series[width + extraWidth];
        System.arraycopy(columnsData, 0, newData, 0, width);

        for (int i = 0; i < extraWidth; i++) {
            newData[width + i] = new RowMappedSeries(this, columnValueProducers[i]);
        }

        return new ColumnarDataFrame(expandedIndex, newData);
    }

    @Override
    public <V, VR> DataFrame mapColumnValue(String columnName, ValueMapper<V, VR> m) {

        int width = width();

        Series[] newData = new Series[width];
        System.arraycopy(columnsData, 0, newData, 0, width);

        int pos = columnsIndex.position(columnName).ordinal();
        newData[pos] = new ColumnMappedSeries(columnsData[pos], m);
        return new ColumnarDataFrame(columnsIndex, newData);
    }

    @Override
    public DataFrame dropColumns(String... columnNames) {

        Index newIndex = columnsIndex.dropNames(columnNames).compactIndex();

        // if no columns were dropped (e.g. the names didn't match anything
        if (newIndex.size() == columnsIndex.size()) {
            return this;
        }

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            int oldIndex = columnsIndex.position(newIndex.getPositions()[i].name()).ordinal();
            newColumns[i] = columnsData[oldIndex];
        }

        return new ColumnarDataFrame(newIndex, newColumns);
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            final CrossColumnRowProxy rowProxy = new CrossColumnRowProxy(columnsIndex, columnsData, height());

            @Override
            public boolean hasNext() {
                return rowProxy.hasNext();
            }

            @Override
            public RowProxy next() {
                return rowProxy.rewind();
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("ColumnarMaterializedDataFrame ["), this).append("]").toString();
    }
}
