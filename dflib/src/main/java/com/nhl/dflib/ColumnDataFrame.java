package com.nhl.dflib;

import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.filter.FilterIndexer;
import com.nhl.dflib.groupby.Grouper;
import com.nhl.dflib.join.HashJoiner;
import com.nhl.dflib.join.NestedLoopJoiner;
import com.nhl.dflib.join.JoinMerger;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.row.CrossColumnRowProxy;
import com.nhl.dflib.sort.SortIndexer;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.row.TransformingIterable;
import com.nhl.dflib.series.ArrayIterator;
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
import java.util.function.Function;
import java.util.stream.Stream;

public class ColumnDataFrame implements DataFrame {

    private Index columnsIndex;
    private Series[] dataColumns;

    public ColumnDataFrame(Index columnsIndex, Series[] dataColumns) {
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
        this.dataColumns = Objects.requireNonNull(dataColumns);
    }

    public static DataFrame fromRows(Index columns, Object[]... rows) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        for (Object[] r : rows) {
            for (int i = 0; i < width; i++) {
                data[i].add(r[i]);
            }
        }

        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columns, columnsData);
    }

    public static DataFrame fromListOfRows(Index columns, List<Object[]> sources) {
        return fromRows(columns, sources);
    }

    public static DataFrame fromRows(Index columns, Iterable<Object[]> sources) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object>[] data = new List[width];
        for (int i = 0; i < width; i++) {
            data[i] = new ArrayList<>();
        }

        for (Object[] r : sources) {
            for (int i = 0; i < width; i++) {
                data[i].add(r[i]);
            }
        }

        int height = data[0].size();
        Series[] columnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            Object[] columnData = new Object[height];
            data[i].toArray(columnData);
            columnsData[i] = new ArraySeries(columnData);
        }

        return new ColumnDataFrame(columns, columnsData);
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

        Series[] series = Series.fromColumnarData(data);
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

        Series[] series = Series.fromColumnarData(data);
        return new ColumnDataFrame(columns, series);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    public static <T> DataFrame fromObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return fromRows(columns, new TransformingIterable<>(rows, rowMapper)).materialize();
    }

    @Override
    public int height() {
        return dataColumns.length > 0 ? dataColumns[0].size() : 0;
    }

    @Override
    public Index getColumns() {
        return columnsIndex;
    }

    @Override
    public Iterable<Series<?>> getDataColumns() {
        return () -> new ArrayIterator(dataColumns);
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
            newColumnsData[i] = new HeadSeries<>(dataColumns[i], len);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame materialize() {

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataColumns[i].materialize();
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame map(Index mappedColumns, RowMapper rowMapper) {
        return Mapper.map(this, mappedColumns, rowMapper);
    }

    @Override
    public DataFrame renameColumns(String... newColumnNames) {
        Index renamed = getColumns().rename(newColumnNames);
        return new ColumnDataFrame(renamed, dataColumns);
    }

    @Override
    public DataFrame renameColumns(Map<String, String> oldToNewNames) {
        Index renamed = getColumns().rename(oldToNewNames);
        return new ColumnDataFrame(renamed, dataColumns);
    }

    @Override
    public DataFrame filter(RowPredicate p) {
        Series<Integer> filteredIndex = FilterIndexer.filteredIndex(this, p);
        return filterWithIndex(filteredIndex);
    }

    @Override
    public <V> DataFrame filterByColumn(int columnPos, ValuePredicate<V> p) {
        Series<Integer> filteredIndex = FilterIndexer.filteredIndex(dataColumns[columnPos], p);
        return filterWithIndex(filteredIndex);
    }

    private DataFrame filterWithIndex(Series<Integer> filteredIndex) {
        if (filteredIndex.size() == height()) {
            return this;
        }

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(dataColumns[i], filteredIndex);
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
        Series<Integer> sortedIndex = SortIndexer.sortedIndex(this, rowComparator);

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(dataColumns[i], sortedIndex);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    private Comparator<Integer> toIntComparator(Comparator<RowProxy> rowComparator) {
        int h = height();
        CrossColumnRowProxy p1 = new CrossColumnRowProxy(columnsIndex, dataColumns, h);
        CrossColumnRowProxy p2 = new CrossColumnRowProxy(columnsIndex, dataColumns, h);
        return (i1, i2) -> rowComparator.compare(p1.rewind(i1), p2.rewind(i2));
    }

    @Override
    public DataFrame hConcat(JoinType how, DataFrame df) {
        Index zipIndex = HConcat.zipIndex(getColumns(), df.getColumns());
        return new HConcat(how).concat(zipIndex, this, df);
    }

    @Override
    public DataFrame hConcat(Index zippedColumns, JoinType how, DataFrame df, RowCombiner c) {
        return new HConcat(how).concat(zippedColumns, this, df, c);
    }

    @Override
    public DataFrame vConcat(JoinType how, DataFrame... dfs) {
        if (dfs.length == 0) {
            return this;
        }

        DataFrame[] combined = new DataFrame[dfs.length + 1];
        combined[0] = this;
        System.arraycopy(dfs, 0, combined, 1, dfs.length);

        return VConcat.concat(how, combined);
    }

    @Override
    public DataFrame join(DataFrame df, JoinPredicate p, JoinType how) {
        NestedLoopJoiner joiner = new NestedLoopJoiner(p, how);
        JoinMerger merger = joiner.joinMerger(this, df);
        return merger.join(joiner.joinIndex(this.getColumns(), df.getColumns()), this, df);
    }

    @Override
    public DataFrame join(DataFrame df, Hasher leftHasher, Hasher rightHasher, JoinType how) {
        HashJoiner joiner = new HashJoiner(leftHasher, rightHasher, how);
        JoinMerger merger = joiner.joinMerger(this, df);
        return merger.join(joiner.joinIndex(this.getColumns(), df.getColumns()), this, df);
    }

    @Override
    public <V> DataFrame addColumns(String[] columnNames, RowToValueMapper<V>... columnValueProducers) {

        int width = width();
        int extraWidth = columnNames.length;
        Index expandedIndex = columnsIndex.addNames(columnNames);

        Series[] newData = new Series[width + extraWidth];
        System.arraycopy(dataColumns, 0, newData, 0, width);

        for (int i = 0; i < extraWidth; i++) {
            newData[width + i] = new RowMappedSeries(this, columnValueProducers[i]);
        }

        return new ColumnDataFrame(expandedIndex, newData);
    }

    @Override
    public <V, VR> DataFrame mapColumnValue(String columnName, ValueMapper<V, VR> m) {

        int width = width();

        Series[] newData = new Series[width];
        System.arraycopy(dataColumns, 0, newData, 0, width);

        int pos = columnsIndex.position(columnName).ordinal();
        newData[pos] = new ColumnMappedSeries(dataColumns[pos], m);
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
            newColumns[i] = dataColumns[newIndex.getPositions()[i].position()];
        }

        // note that we compact the index only after resolving series positions above
        return new ColumnDataFrame(newIndex.compactIndex(), newColumns);
    }

    @Override
    public DataFrame selectColumns(String... columnNames) {
        Index newIndex = columnsIndex.selectNames(columnNames);

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = dataColumns[newIndex.getPositions()[i].position()];
        }

        // note that we compact the index only after resolving series positions above
        return new ColumnDataFrame(newIndex.compactIndex(), newColumns);
    }

    @Override
    public GroupBy groupBy(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            final CrossColumnRowProxy rowProxy = new CrossColumnRowProxy(columnsIndex, dataColumns, height());

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
        return Printers.inline.append(this, new StringBuilder("ColumnarDataFrame [")).append("]").toString();
    }
}
