package org.dflib;

import org.dflib.slice.DeferredColumnSet;
import org.dflib.concat.HConcat;
import org.dflib.concat.VConcat;
import org.dflib.explode.Exploder;
import org.dflib.groupby.Grouper;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.RowProxy;
import org.dflib.sample.Sampler;
import org.dflib.select.RowIndexer;
import org.dflib.series.EmptySeries;
import org.dflib.series.IntArraySeries;
import org.dflib.series.SingleValueSeries;
import org.dflib.sort.DataFrameSorter;
import org.dflib.stack.Stacker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ColumnDataFrame implements DataFrame {

    private final String name;
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

    /**
     * @deprecated in favor of {@link #ColumnDataFrame(String, Index, Series[])}
     */
    @Deprecated(since = "0.19", forRemoval = true)
    public ColumnDataFrame(Index columnsIndex, Series<?>... dataColumns) {
        this(null, columnsIndex, dataColumns);
    }

    public ColumnDataFrame(String name, Index columnsIndex, Series<?>... dataColumns) {
        this.name = name;
        this.columnsIndex = Objects.requireNonNull(columnsIndex);
        this.dataColumns = alignColumns(columnsIndex.size(), dataColumns);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataFrame as(String name) {
        return Objects.equals(name, this.name) ? this : new ColumnDataFrame(name, columnsIndex, dataColumns);
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
    public DataFrame head(int len) {

        if (Math.abs(len) >= height()) {
            return this;
        }

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataColumns[i].head(len);
        }

        return new ColumnDataFrame(null, columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame tail(int len) {

        if (Math.abs(len) >= height()) {
            return this;
        }

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataColumns[i].tail(len);
        }

        return new ColumnDataFrame(null, columnsIndex, newColumnsData);
    }

    @Override
    public DataFrame materialize() {

        int width = width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataColumns[i].materialize();
        }

        return new ColumnDataFrame(null, columnsIndex, newColumnsData);
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

        return new ColumnDataFrame(null, columnsIndex, newColumnsData);
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
    public DataFrame uniqueRows(String... columnNamesToCompare) {

        if (columnNamesToCompare.length == 0) {
            throw new IllegalArgumentException("No 'columnNamesToCompare' for uniqueness checks");
        }

        int w = width();
        Exp[] aggregators = new Exp[w];

        for (int i = 0; i < w; i++) {
            aggregators[i] = Exp.$col(i).first();
        }

        return group(columnNamesToCompare).agg(aggregators);
    }

    @Override
    public DataFrame uniqueRows(int... columnNamesToCompare) {
        if (columnNamesToCompare.length == 0) {
            throw new IllegalArgumentException("No 'columnNamesToCompare' for uniqueness checks");
        }

        int w = width();
        Exp[] aggregators = new Exp[w];

        for (int i = 0; i < w; i++) {
            aggregators[i] = Exp.$col(i).first();
        }

        return group(columnNamesToCompare).agg(aggregators);
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
        Index zipIndex = HConcat.zipIndex(getColumnsIndex(), df.getColumnsIndex().getLabels());
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
    public DataFrame addRow(Map<String, Object> row) {

        int w = width();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {
            newColumns[i] = dataColumns[i].add(row.get(columnsIndex.getLabel(i)));
        }

        return DataFrame.byColumn(getColumnsIndex()).of(newColumns);
    }

    @Override
    public <V, VR> DataFrame convertColumn(int pos, ValueMapper<V, VR> converter) {
        // do not use Exp.mapVal(..) as it will not pass null values to the mapper
        return replaceColumn(pos, dataColumns[pos].map(converter));
    }

    @Override
    public <V> DataFrame compactInt(int pos, IntValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, c.mapAsInt(converter));
    }

    @Override
    public <V> DataFrame compactDouble(int pos, DoubleValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, c.mapAsDouble(converter));
    }

    @Override
    public <V> DataFrame compactBool(int pos, BoolValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, c.mapAsBool(converter));
    }

    @Override
    public <V> DataFrame compactLong(int pos, LongValueMapper<V> converter) {
        Series<V> c = dataColumns[pos];
        return replaceColumn(pos, c.mapAsLong(converter));
    }

    @Override
    public GroupBy group(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    public DataFrame vExplode(int columnPos) {
        return Exploder.explode(this, columnPos);
    }

    @Override
    public DataFrame nullify(DataFrame condition) {
        int w = width();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {

            String label = columnsIndex.getLabel(i);

            if (condition.getColumnsIndex().hasLabel(label)) {
                BooleanSeries cc = condition.getColumn(label).castAsBool();
                newColumns[i] = dataColumns[i].replace(cc, null);
            } else {
                newColumns[i] = dataColumns[i];
            }
        }

        return new ColumnDataFrame(null, columnsIndex, newColumns);
    }

    @Override
    public DataFrame nullifyNoMatch(DataFrame condition) {
        int w = width();
        int h = height();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {

            String label = columnsIndex.getLabel(i);

            if (condition.getColumnsIndex().hasLabel(label)) {
                BooleanSeries cc = condition.getColumn(label).castAsBool();
                newColumns[i] = dataColumns[i].replaceNoMatch(cc, null);
            } else {
                newColumns[i] = new SingleValueSeries<>(null, h);
            }
        }

        return new ColumnDataFrame(null, columnsIndex, newColumns);
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

        return new ColumnDataFrame(null, columnsIndex, resultColumns);
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

        return new ColumnDataFrame(null, columnsIndex, resultColumns);
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
        return new Iterator<>() {

            final ColumnsRowProxy rowProxy = new ColumnsRowProxy(columnsIndex, dataColumns, height());

            @Override
            public boolean hasNext() {
                return rowProxy.hasNext();
            }

            @Override
            public RowProxy next() {
                return rowProxy.next();
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
    public ColumnSet cols() {
        return new DeferredColumnSet(this, dataColumns);
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

        return new ColumnDataFrame(null, columnsIndex, newColumns);
    }
}