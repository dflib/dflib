package com.nhl.dflib;

import com.nhl.dflib.builder.BooleanAccum;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.explode.Exploder;
import com.nhl.dflib.select.RowIndexer;
import com.nhl.dflib.groupby.Grouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.row.CrossColumnRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.series.*;
import com.nhl.dflib.sort.DataFrameSorter;
import com.nhl.dflib.stack.Stacker;
import com.nhl.dflib.window.RowNumberer;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ColumnDataFrame implements DataFrame {

    private final Index columnsIndex;
    private final Series[] dataColumns;

    private static Series[] alignColumns(int w, Series<?>... columns) {

        Objects.requireNonNull(columns, "Null 'columns'");

        int sw = columns.length;
        if (w == sw) {
            return columns;
        }

        if (sw > 0) {
            throw new IllegalArgumentException("Index size is not the same as data columns size: " + w + " != " + sw);
        }

        // no data columns means empty DataFrame with non-empty index

        Series[] finalColumns = new Series[w];
        EmptySeries es = new EmptySeries();
        Arrays.fill(finalColumns, es);
        return finalColumns;
    }

    public ColumnDataFrame(Index columnsIndex, Series<?>... dataColumns) {
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
        this.dataColumns = alignColumns(columnsIndex.size(), dataColumns);
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
    public IntSeries getColumnAsInt(int pos) {
        Series<?> s = getColumn(pos);
        if (s instanceof IntSeries) {
            return (IntSeries) s;
        }

        throw new IllegalArgumentException("Column at " + pos + " is not an IntSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public IntSeries getColumnAsInt(String name) throws IllegalArgumentException {
        Series<?> s = getColumn(name);
        if (s instanceof IntSeries) {
            return (IntSeries) s;
        }

        throw new IllegalArgumentException("Column '" + name + "' is not an IntSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public DoubleSeries getColumnAsDouble(int pos) throws IllegalArgumentException {
        Series<?> s = getColumn(pos);
        if (s instanceof DoubleSeries) {
            return (DoubleSeries) s;
        }

        throw new IllegalArgumentException("Column at " + pos + " is not a DoubleSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public DoubleSeries getColumnAsDouble(String name) throws IllegalArgumentException {
        Series<?> s = getColumn(name);
        if (s instanceof DoubleSeries) {
            return (DoubleSeries) s;
        }

        throw new IllegalArgumentException("Column '" + name + "' is not a DoubleSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public BooleanSeries getColumnAsBoolean(int pos) throws IllegalArgumentException {
        Series<?> s = getColumn(pos);
        if (s instanceof BooleanSeries) {
            return (BooleanSeries) s;
        }

        throw new IllegalArgumentException("Column at " + pos + " is not a BooleanSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public BooleanSeries getColumnAsBoolean(String name) throws IllegalArgumentException {
        Series<?> s = getColumn(name);
        if (s instanceof BooleanSeries) {
            return (BooleanSeries) s;
        }

        throw new IllegalArgumentException("Column at " + name + " is not a BooleanSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public LongSeries getColumnAsLong(int pos) throws IllegalArgumentException {
        Series<?> s = getColumn(pos);
        if (s instanceof LongSeries) {
            return (LongSeries) s;
        }

        throw new IllegalArgumentException("Column at " + pos + " is not a LongSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public LongSeries getColumnAsLong(String name) throws IllegalArgumentException {
        Series<?> s = getColumn(name);
        if (s instanceof LongSeries) {
            return (LongSeries) s;
        }

        throw new IllegalArgumentException("Column at " + name + " is not a LongSeries: " + s.getClass().getSimpleName());
    }

    @Override
    public DataFrame addRowNumberColumn(String columnName) {
        return addColumn(columnName, RowNumberer.sequence(height()));
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
    public <T> Series<T> mapColumn(RowToValueMapper<T> rowMapper) {
        return new RowMappedSeries<>(this, rowMapper);
    }

    @Override
    public BooleanSeries mapColumnAsBoolean(RowToBooleanValueMapper rowMapper) {
        // don't bother to make it lazy... boolean columns are very compact compared to the rest of the data set
        BooleanAccum data = new BooleanAccum(height());

        for (RowProxy row : this) {
            data.pushBoolean(rowMapper.map(row));
        }

        return data.toSeries();
    }

    @Override
    public DataFrame renameColumns(UnaryOperator<String> renameFunction) {
        Index renamed = getColumnsIndex().rename(renameFunction);
        return new ColumnDataFrame(renamed, dataColumns);
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
    public DataFrame selectRows(int... rowPositions) {
        return selectRows(new IntArraySeries(rowPositions));
    }

    @Override
    public DataFrame selectRows(IntSeries rowPositions) {

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataColumns[i].select(rowPositions);
        }

        return new ColumnDataFrame(columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame selectRows(BooleanSeries condition) {
        return selectRows(condition.indexTrue());
    }

    @Override
    public DataFrame selectRows(RowPredicate p) {

        IntSeries rowPositions = RowIndexer.index(this, p);

        // there's no reordering or index duplication during "select", so we can compare size to detect changes
        if (rowPositions.size() == height()) {
            return this;
        }

        return selectRows(rowPositions);
    }

    @Override
    public <V> DataFrame selectRows(int columnPos, ValuePredicate<V> p) {
        IntSeries rowPositions = dataColumns[columnPos].index(p);

        // there's no reordering or index duplication during "select", so we can compare size to detect changes
        if (rowPositions.size() == height()) {
            return this;
        }

        return selectRows(rowPositions);
    }

    @Override
    public DataFrame selectRows(Condition condition) {
        return selectRows(condition.eval(this).indexTrue());
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        return new DataFrameSorter(this).sort(sorters);
    }

    @Override
    public DataFrame sort(String[] columns, boolean[] ascending) {
        return new DataFrameSorter(this).sort(columns, ascending);
    }

    @Override
    public DataFrame sort(int[] columns, boolean[] ascending) {
        return new DataFrameSorter(this).sort(columns, ascending);
    }

    @Override
    public DataFrame sort(int column, boolean ascending) {
        return new DataFrameSorter(this).sort(column, ascending);
    }

    @Override
    public DataFrame sort(String column, boolean ascending) {
        return new DataFrameSorter(this).sort(column, ascending);
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
    public DataFrame addColumns(Exp<?>... exps) {

        int extraWidth = exps.length;
        if (extraWidth == 0) {
            return this;
        }

        String[] extraNames = new String[extraWidth];
        for (int i = 0; i < extraWidth; i++) {
            extraNames[i] = exps[i].getColumnName(this);
        }

        int width = width();

        Index expandedIndex = columnsIndex.addLabels(extraNames);

        Series[] newData = new Series[width + extraWidth];
        System.arraycopy(dataColumns, 0, newData, 0, width);

        for (int i = 0; i < extraWidth; i++) {
            newData[width + i] = exps[i].eval(this);
        }

        return new ColumnDataFrame(expandedIndex, newData);
    }

    @Override
    public DataFrame addColumns(String[] columnLabels, RowToValueMapper<?>... columnValueProducers) {

        int width = width();
        int extraWidth = columnLabels.length;

        if (extraWidth != columnValueProducers.length) {
            throw new IllegalArgumentException("Number of new column labels [" + extraWidth +
                    "] doesn't match the number of supplied producers [" + columnValueProducers.length + "]");
        }

        Index expandedIndex = columnsIndex.addLabels(columnLabels);

        Series[] newData = new Series[width + extraWidth];
        System.arraycopy(dataColumns, 0, newData, 0, width);

        for (int i = 0; i < extraWidth; i++) {
            newData[width + i] = mapColumn(columnValueProducers[i]);
        }

        return new ColumnDataFrame(expandedIndex, newData);
    }

    @Override
    public DataFrame addColumns(String[] columnLabels, RowMapper rowMapper) {
        return hConcat(map(Index.forLabels(columnLabels), rowMapper));
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

    /**
     * @since 0.11
     */
    @Override
    public DataFrame convertColumn(String name, Exp<?> exp) {
        int pos = getColumnsIndex().position(name);
        return replaceColumn(pos, exp.eval(this));
    }

    /**
     * @since 0.11
     */
    @Override
    public DataFrame convertColumn(int position, Exp<?> exp) {
        return replaceColumn(position, exp.eval(this));
    }

    @Override
    public <V> DataFrame toIntColumn(int pos, IntValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, IntSeries.forSeries(c, converter));
    }

    @Override
    public <V> DataFrame toDoubleColumn(int pos, DoubleValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, DoubleSeries.forSeries(c, converter));
    }

    @Override
    public <V> DataFrame toBooleanColumn(int pos, BooleanValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, BooleanSeries.forSeries(c, converter));
    }

    @Override
    public <V> DataFrame toLongColumn(int pos, LongValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, LongSeries.forSeries(c, converter));
    }

    @Override
    public DataFrame dropColumns(String... columnNames) {
        Index newIndex = columnsIndex.dropLabels(columnNames);
        return newIndex == columnsIndex ? this : selectColumns(newIndex);
    }

    @Override
    public DataFrame dropColumns(Predicate<String> labelCondition) {
        Index newIndex = columnsIndex.dropLabels(labelCondition);
        return newIndex == columnsIndex ? this : selectColumns(newIndex);
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
    public DataFrame selectColumns(int pos0, int... otherPositions) {

        int[] positions = new int[otherPositions.length + 1];
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
    public DataFrame selectColumns(Index columnsIndex) {
        Series[] newColumns = new Series[columnsIndex.size()];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = dataColumns[this.columnsIndex.position(columnsIndex.getLabel(i))];
        }

        return new ColumnDataFrame(columnsIndex, newColumns);
    }

    /**
     * @since 0.7
     */
    @Override
    public DataFrame selectColumns(Predicate<String> includeCondition) {
        Index newIndex = columnsIndex.selectLabels(includeCondition);
        return newIndex != columnsIndex ? selectColumns(newIndex) : this;
    }

    /**
     * @since 0.11
     */
    @Override
    public DataFrame selectColumns(Exp<?> exp0, Exp<?>... otherExps) {
        int w = otherExps.length + 1;
        String[] labels = new String[w];
        labels[0] = exp0.getColumnName(this);
        for (int i = 1; i < w; i++) {
            labels[i] = otherExps[i - 1].getColumnName(this);
        }

        Series[] data = new Series[w];
        data[0] = exp0.eval(this);

        int h = data[0].size();
        for (int i = 1; i < w; i++) {
            data[i] = otherExps[i - 1].eval(this);

            // sanity check - all columns must be the same size
            // TODO: move this check to the DataFrame builder?
            if (data[i].size() != h) {
                throw new IllegalStateException("Unexpected column size for '" + labels[i] + "': " + data[i].size() + ", (expected " + h + ")");
            }
        }

        return DataFrame.byColumn(labels).array(data);
    }

    @Override
    public GroupBy group(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    public DataFrame explode(int columnPos) {
        return Exploder.explode(this, columnPos);
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
    public DataFrame fillNullsFromSeries(int columnPos, Series<?> values) {
        return replaceColumn(columnPos, dataColumns[columnPos].fillNullsFromSeries(values));
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
    public DataFrame nullify(DataFrame condition) {
        int w = width();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {

            String label = columnsIndex.getLabel(i);

            if (condition.getColumnsIndex().hasLabel(label)) {
                BooleanSeries cc = condition.getColumnAsBoolean(label);
                newColumns[i] = dataColumns[i].replace(cc, null);
            } else {
                newColumns[i] = dataColumns[i];
            }
        }

        return new ColumnDataFrame(columnsIndex, newColumns);
    }

    @Override
    public DataFrame nullifyNoMatch(DataFrame condition) {
        int w = width();
        int h = height();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {

            String label = columnsIndex.getLabel(i);

            if (condition.getColumnsIndex().hasLabel(label)) {
                BooleanSeries cc = condition.getColumnAsBoolean(label);
                newColumns[i] = dataColumns[i].replaceNoMatch(cc, null);
            } else {
                newColumns[i] = new SingleValueSeries<>(null, h);
            }
        }

        return new ColumnDataFrame(columnsIndex, newColumns);
    }


    @Override
    public DataFrame eq(DataFrame another) {

        // after this comparision passes, we can compare columns by position
        checkShapeMatches(another);

        int w = width();
        BooleanSeries[] resultColumns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            resultColumns[i] = dataColumns[i].eq(another.getColumn(i));
        }

        return new ColumnDataFrame(columnsIndex, resultColumns);
    }

    @Override
    public DataFrame ne(DataFrame another) {

        // after this comparision passes, we can compare columns by position
        checkShapeMatches(another);

        int w = width();
        BooleanSeries[] resultColumns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            resultColumns[i] = dataColumns[i].ne(another.getColumn(i));
        }

        return new ColumnDataFrame(columnsIndex, resultColumns);
    }

    private void checkShapeMatches(DataFrame another) {

        if (!columnsIndex.equals(another.getColumnsIndex())) {
            // either sizes are different, or labels do not match
            int w = width();
            int aw = another.width();
            if (w != aw) {
                throw new IllegalArgumentException("Another DataFrame width is not the same as this width (" + aw + " vs " + w + ")");
            } else {
                throw new IllegalArgumentException("Another DataFrame columnsIndex is not equals to this columnsIndex");
            }
        }

        int h = height();
        int ah = another.height();
        if (h != ah) {
            throw new IllegalArgumentException("Another DataFrame height is not the same as this height (" + ah + " vs " + h + ")");
        }
    }

    @Override
    public DataFrame stack() {
        return Stacker.stackExcludeNulls(this);
    }

    @Override
    public DataFrame stackIncludeNulls() {
        return Stacker.stackIncludeNulls(this);
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

    /**
     * @since 0.7
     */
    @Override
    public DataFrame sampleRows(int size) {
        return selectRows(Sampler.sampleIndex(size, height()));
    }

    /**
     * @since 0.7
     */
    @Override
    public DataFrame sampleRows(int size, Random random) {
        return selectRows(Sampler.sampleIndex(size, height(), random));
    }

    @Override
    public DataFrame sampleColumns(int size) {
        return selectColumns(columnsIndex.sample(size));
    }

    @Override
    public DataFrame sampleColumns(int size, Random random) {
        return selectColumns(columnsIndex.sample(size, random));
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
