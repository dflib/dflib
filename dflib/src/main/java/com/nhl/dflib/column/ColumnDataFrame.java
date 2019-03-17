package com.nhl.dflib.column;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.column.filter.ColumnarFilterIndexer;
import com.nhl.dflib.column.map.ColumnarMapper;
import com.nhl.dflib.column.sort.ColumnarSortIndexer;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.HConcatRowDataFrame;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.ColumnMappedSeries;
import com.nhl.dflib.series.HeadSeries;
import com.nhl.dflib.series.IndexedSeries;
import com.nhl.dflib.series.RowMappedSeries;
import com.nhl.dflib.sort.Sorters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ColumnDataFrame implements DataFrame {

    private Index columnsIndex;
    private Series[] columnsData;

    public ColumnDataFrame(Index columnsIndex, Series[] columnsData) {
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
        this.columnsData = Objects.requireNonNull(columnsData);
    }

    public static <T> DataFrame fromStreamFoldByRow(Index columns, Stream<T> stream) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        Iterator<T> it = stream.iterator();

        int p = 0;
        while (it.hasNext()) {
            data[p % width].add(it.next());
            p++;
        }

        // 'height' is max height; some columns may be smaller
        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columns, columnsData);
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data column-by-column.
     */
    public static DataFrame fromSequenceFoldByColumn(Index columns, Object... sequence) {

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

        return new ColumnDataFrame(columns, series);
    }

    /**
     * Creates a columnar DataFrame from the provided array that stores data row-by-row. This is NOT a very efficient
     * way to initialize a columnar DataFrame. Use {@link #fromSequenceFoldByColumn(Index, Object...)} when possible.
     */
    public static DataFrame fromSequenceFoldByRow(Index columns, Object... sequence) {

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

        return new ColumnDataFrame(columns, series);
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
    public DataFrame head(int len) {

        if (len < 0) {
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        int maxLen = height();
        if (maxLen <= len) {
            return this;
        }

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new HeadSeries<>(columnsData[i], len);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame materialize() {
        return this;
    }

    @Override
    public DataFrame map(Index mappedColumns, RowMapper rowMapper) {
        return ColumnarMapper.map(this, mappedColumns, rowMapper);
    }

    @Override
    public DataFrame renameColumns(String... newColumnNames) {
        Index renamed = getColumns().rename(newColumnNames);
        return new ColumnDataFrame(renamed, columnsData);
    }

    @Override
    public DataFrame renameColumns(Map<String, String> oldToNewNames) {
        Index renamed = getColumns().rename(oldToNewNames);
        return new ColumnDataFrame(renamed, columnsData);
    }

    @Override
    public DataFrame filter(RowPredicate p) {
        Series<Integer> filteredIndex = ColumnarFilterIndexer.filteredIndex(this, p);
        return filterWithIndex(filteredIndex);
    }

    @Override
    public <V> DataFrame filterByColumn(int columnPos, ValuePredicate<V> p) {
        Series<Integer> filteredIndex = ColumnarFilterIndexer.filteredIndex(columnsData[columnPos], p);
        return filterWithIndex(filteredIndex);
    }

    private DataFrame filterWithIndex(Series<Integer> filteredIndex) {
        if (filteredIndex.size() == height()) {
            return this;
        }

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(columnsData[i], filteredIndex);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(Sorters.sorter(sortKeyExtractor));
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sortByColumns(String... columns) {
        return sort(Sorters.sorter(getColumns(), columns));
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sortByColumns(int... columns) {
        return sort(Sorters.sorter(getColumns(), columns));
    }

    private DataFrame sort(Comparator<RowProxy> comparator) {
        Comparator<Integer> rowComparator = toIntComparator(comparator);
        Series<Integer> sortedIndex = ColumnarSortIndexer.sortedIndex(this, rowComparator);

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(columnsData[i], sortedIndex);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    private Comparator<Integer> toIntComparator(Comparator<RowProxy> rowComparator) {
        int h = height();
        CrossColumnRowProxy p1 = new CrossColumnRowProxy(columnsIndex, columnsData, h);
        CrossColumnRowProxy p2 = new CrossColumnRowProxy(columnsIndex, columnsData, h);
        return (i1, i2) -> rowComparator.compare(p1.rewind(i1), p2.rewind(i2));
    }

    @Override
    public DataFrame hConcat(Index zippedColumns, JoinType how, DataFrame df, RowCombiner c) {
        // TODO: columnar implementation...
        return new HConcatRowDataFrame(zippedColumns, how, this, df, c).materialize();
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

        return new ColumnDataFrame(expandedIndex, newData);
    }

    @Override
    public <V, VR> DataFrame mapColumnValue(String columnName, ValueMapper<V, VR> m) {

        int width = width();

        Series[] newData = new Series[width];
        System.arraycopy(columnsData, 0, newData, 0, width);

        int pos = columnsIndex.position(columnName).ordinal();
        newData[pos] = new ColumnMappedSeries(columnsData[pos], m);
        return new ColumnDataFrame(columnsIndex, newData);
    }

    @Override
    public DataFrame dropColumns(String... columnNames) {

        Index newIndex = columnsIndex.dropNames(columnNames);

        // if no columns were dropped (e.g. the names didn't match anything
        if (newIndex.size() == columnsIndex.size()) {
            return this;
        }

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = columnsData[newIndex.getPositions()[i].position()];
        }

        // note that we compact the index only after resolving series positions above
        return new ColumnDataFrame(newIndex.compactIndex(), newColumns);
    }

    @Override
    public DataFrame selectColumns(String... columnNames) {
        Index newIndex = columnsIndex.selectNames(columnNames);

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = columnsData[newIndex.getPositions()[i].position()];
        }

        // note that we compact the index only after resolving series positions above
        return new ColumnDataFrame(newIndex.compactIndex(), newColumns);
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
