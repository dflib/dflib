package com.nhl.dflib.columnar;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.MaterializedDataFrame;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColumnarDataFrame implements DataFrame {

    private Index columnsIndex;
    private Series[] columnsData;

    public ColumnarDataFrame(Index columnsIndex, Series[] columnsData) {
        this.columnsIndex = columnsIndex;
        this.columnsData = columnsData;
    }

    /**
     * Creates a columnar DataFrame by folding the provided array of objects into rows and columns.
     */
    public static DataFrame fromSequence(Index columns, Object... sequence) {

        int width = columns.size();
        int rows = sequence.length / width;

        Series[] data = new Series[width];
        

        List<Object[]> folded = new ArrayList<>(rows + 1);
        for (int i = 0; i < rows; i++) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, i * width, row, 0, width);
            folded.add(row);
        }

        // copy partial last row
        int leftover = sequence.length % width;
        if (leftover > 0) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, rows * width, row, 0, leftover);
            folded.add(row);
        }

        return new ColumnarDataFrame(columns, folded);
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
