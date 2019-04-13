package com.nhl.dflib;

import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.filter.FilterIndexer;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.groupby.Grouper;
import com.nhl.dflib.join.HashJoiner;
import com.nhl.dflib.join.JoinMerger;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.join.NestedLoopJoiner;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.row.CrossColumnRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.seq.Sequences;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.ColumnMappedSeries;
import com.nhl.dflib.series.IndexedSeries;
import com.nhl.dflib.series.IntSeries;
import com.nhl.dflib.series.RowMappedSeries;
import com.nhl.dflib.sort.IndexSorter;
import com.nhl.dflib.sort.Sorters;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ColumnDataFrame implements DataFrame {

    private Index columnsIndex;
    private Series[] dataColumns;

    public ColumnDataFrame(Index columnsIndex, Series... dataColumns) {
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
        this.dataColumns = Objects.requireNonNull(dataColumns);
    }

    @Override
    public int height() {
        return dataColumns.length > 0 ? dataColumns[0].size() : 0;
    }

    @Override
    public Index getColumnsIndex() {
        return columnsIndex;
    }

    @Override
    public <T> Series<T> getColumn(int pos) {
        return dataColumns[pos];
    }

    @Override
    public <T> Series<T> getColumn(String name) {
        return dataColumns[columnsIndex.position(name)];
    }

    @Override
    public DataFrame addRowNumber(String columnName) {
        return addColumn(columnName, new ArraySeries<>(Sequences.numberSequence(height())));
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
            newColumnsData[i] = dataColumns[i].head(len);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame tail(int len) {

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
            newColumnsData[i] = dataColumns[i].tail(len);
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
        Index renamed = getColumnsIndex().rename(newColumnNames);
        return new ColumnDataFrame(renamed, dataColumns);
    }

    @Override
    public DataFrame renameColumns(Map<String, String> oldToNewNames) {
        Index renamed = getColumnsIndex().rename(oldToNewNames);
        return new ColumnDataFrame(renamed, dataColumns);
    }

    @Override
    public DataFrame select(int... rowPositions) {
        return select(new IntSeries(rowPositions));
    }

    @Override
    public DataFrame select(IntSeries rowPositions) {

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(dataColumns[i], rowPositions);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame filter(RowPredicate p) {
        IntSeries rowPositions = FilterIndexer.filteredIndex(this, p);

        // there's no reordering or index duplication during "filter", so we can compare size to detect changes
        if (rowPositions.size() == height()) {
            return this;
        }

        return select(rowPositions);
    }

    @Override
    public <V> DataFrame filter(int columnPos, ValuePredicate<V> p) {
        IntSeries rowPositions = FilterIndexer.filteredIndex(dataColumns[columnPos], p);

        // there's no reordering or index duplication during "filter", so we can compare size to detect changes
        if (rowPositions.size() == height()) {
            return this;
        }

        return select(rowPositions);
    }

    @Override
    public <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        return new IndexSorter(this).sort(Sorters.sorter(sortKeyExtractor));
    }

    @Override
    public DataFrame sort(String[] columns, boolean[] ascending) {
        return new IndexSorter(this).sort(Sorters.sorter(columnsIndex, columns, ascending));
    }

    @Override
    public DataFrame sort(int[] columns, boolean[] ascending) {
        return new IndexSorter(this).sort(Sorters.sorter(columnsIndex, columns, ascending));
    }

    @Override
    public DataFrame sort(int column, boolean ascending) {
        return new IndexSorter(this).sort(Sorters.sorter(columnsIndex, column, ascending));
    }

    @Override
    public DataFrame sort(String column, boolean ascending) {
        return new IndexSorter(this).sort(Sorters.sorter(columnsIndex, column, ascending));
    }

    @Override
    public DataFrame hConcat(JoinType how, DataFrame df) {
        Index zipIndex = HConcat.zipIndex(getColumnsIndex(), df.getColumnsIndex());
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
        return merger.join(joiner.joinIndex(this.getColumnsIndex(), df.getColumnsIndex()), this, df);
    }

    @Override
    public DataFrame join(DataFrame df, Hasher leftHasher, Hasher rightHasher, JoinType how) {
        HashJoiner joiner = new HashJoiner(leftHasher, rightHasher, how);
        JoinMerger merger = joiner.joinMerger(this, df);
        return merger.join(joiner.joinIndex(this.getColumnsIndex(), df.getColumnsIndex()), this, df);
    }

    @Override
    public <V> DataFrame addColumns(String[] columnNames, RowToValueMapper<V>... columnValueProducers) {

        int width = width();
        int extraWidth = columnNames.length;
        Index expandedIndex = columnsIndex.addLabels(columnNames);

        Series[] newData = new Series[width + extraWidth];
        System.arraycopy(dataColumns, 0, newData, 0, width);

        for (int i = 0; i < extraWidth; i++) {
            newData[width + i] = new RowMappedSeries(this, columnValueProducers[i]);
        }

        return new ColumnDataFrame(expandedIndex, newData);
    }

    @Override
    public <V> DataFrame addColumn(String columnName, Series<V> column) {

        int ch = column.size();
        int h = height();

        if (ch != h) {
            throw new IllegalArgumentException("The new column height (" + ch + ") is different from the DataFrame height (" + h + ")");
        }

        Index newIndex = columnsIndex.addLabels(columnName);

        int w = dataColumns.length;
        Series<?>[] newDataColumns = new Series[w + 1];
        System.arraycopy(dataColumns, 0, newDataColumns, 0, w);
        newDataColumns[w] = column;

        return new ColumnDataFrame(newIndex, newDataColumns);
    }

    @Override
    public <V, VR> DataFrame convertColumn(int columnPos, ValueMapper<V, VR> converter) {
        return replaceColumn(columnPos, new ColumnMappedSeries(dataColumns[columnPos], converter));
    }

    @Override
    public DataFrame dropColumns(String... columnNames) {

        Index newIndex = columnsIndex.dropLabels(columnNames);

        // if no columns were dropped (e.g. the names didn't match anything
        if (newIndex.size() == columnsIndex.size()) {
            return this;
        }

        String[] remainingLabels = newIndex.getLabels();
        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = dataColumns[columnsIndex.position(remainingLabels[i])];
        }

        return new ColumnDataFrame(newIndex, newColumns);
    }

    @Override
    public DataFrame selectColumns(String label0, String... otherLabels) {

        String[] labels = new String[otherLabels.length + 1];
        labels[0] = label0;
        System.arraycopy(otherLabels, 0, labels, 1, otherLabels.length);

        Index newIndex = columnsIndex.selectLabels(labels);

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = dataColumns[columnsIndex.position(labels[i])];
        }

        return new ColumnDataFrame(newIndex, newColumns);
    }


    @Override
    public DataFrame selectColumns(Integer pos0, Integer... otherPositions) {

        Integer[] positions = new Integer[otherPositions.length + 1];
        positions[0] = pos0;
        System.arraycopy(otherPositions, 0, positions, 1, otherPositions.length);

        Index newIndex = columnsIndex.selectPositions(positions);

        Series[] newColumns = new Series[newIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = dataColumns[positions[i]];
        }

        return new ColumnDataFrame(newIndex, newColumns);
    }

    @Override
    public GroupBy group(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    public DataFrame fillNulls(Object value) {

        int w = width();
        Series[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {
            newColumns[i] = dataColumns[i].fillNulls(value);
        }

        return new ColumnDataFrame(columnsIndex, newColumns);
    }

    @Override
    public DataFrame fillNulls(int columnPos, Object value) {
        return replaceColumn(columnPos, dataColumns[columnPos].fillNulls(value));
    }

    @Override
    public DataFrame fillNullsBackwards(int columnPos) {
        return replaceColumn(columnPos, dataColumns[columnPos].fillNullsBackwards());
    }

    @Override
    public DataFrame fillNullsForward(int columnPos) {
        return replaceColumn(columnPos, dataColumns[columnPos].fillNullsForward());
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
        return Printers.inline.print(new StringBuilder("ColumnDataFrame ["), this).append("]").toString();
    }

    protected DataFrame replaceColumn(int pos, Series<?> newColumn) {
        if (newColumn == dataColumns[pos]) {
            return this;
        }

        int w = width();
        Series[] newColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            newColumns[i] = i == pos ? newColumn : dataColumns[i];
        }

        return new ColumnDataFrame(columnsIndex, newColumns);
    }
}
