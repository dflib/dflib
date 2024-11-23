package org.dflib;

import org.dflib.concat.HConcat;
import org.dflib.concat.VConcat;
import org.dflib.groupby.Grouper;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.RowProxy;
import org.dflib.series.EmptySeries;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.SingleValueSeries;
import org.dflib.slice.AllRowSet;
import org.dflib.slice.ConditionalRowSet;
import org.dflib.slice.DeferredColumnSet;
import org.dflib.slice.EmptyRowSet;
import org.dflib.slice.IndexedRowSet;
import org.dflib.slice.RangeRowSet;
import org.dflib.stack.Stacker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
        try {
            return dataColumns[pos];
        } catch (ArrayIndexOutOfBoundsException e) {
            // fulfilling the interface method exception contract
            throw new IllegalArgumentException("Position '" + pos + "' is outside of DataFrame bounds. DataFrame width is " + width());
        }
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
    public DataFrame hConcat(JoinType how, DataFrame df) {
        Index zipIndex = getColumnsIndex().expand(df.getColumnsIndex().toArrayNoCopy());
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
            newColumns[i] = dataColumns[i].expand(row.get(columnsIndex.get(i)));
        }

        return DataFrame.byColumn(getColumnsIndex()).of(newColumns);
    }

    @Override
    public GroupBy group(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    public DataFrame nullify(DataFrame condition) {
        int w = width();
        Series<?>[] newColumns = new Series[w];

        for (int i = 0; i < w; i++) {

            String label = columnsIndex.get(i);

            if (condition.getColumnsIndex().contains(label)) {
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

            String label = columnsIndex.get(i);

            if (condition.getColumnsIndex().contains(label)) {
                BooleanSeries cc = condition.getColumn(label).castAsBool();
                newColumns[i] = dataColumns[i].replaceExcept(cc, null);
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

    @Override
    public ColumnSet cols() {
        return new DeferredColumnSet(this, dataColumns);
    }

    public RowSet rows() {
        return height() > 0 ? new AllRowSet(this, dataColumns) : new EmptyRowSet(this);
    }

    @Override
    public RowSet rows(IntSeries positions) {
        return positions.size() > 0 ? new IndexedRowSet(this, dataColumns, positions) : new EmptyRowSet(this);
    }

    @Override
    public RowSet rowsExcept(IntSeries positions) {
        // TODO: "diff" is likely more expensive vs converting IntSeries to a negated BooleanSeries matching DF height
        return rows(new IntSequenceSeries(0, height()).diff(positions));
    }

    @Override
    public RowSet rows(BooleanSeries condition) {
        return new ConditionalRowSet(this, dataColumns, condition);
    }

    @Override
    public RowSet rowsRange(int fromInclusive, int toExclusive) {
        int h = toExclusive - fromInclusive;
        if (h == 0) {
            return new EmptyRowSet(this);
        } else if (h == height()) {
            return new AllRowSet(this, dataColumns);
        } else {
            return new RangeRowSet(this, dataColumns, fromInclusive, toExclusive);
        }
    }

    @Override
    public String toString() {
        return Environment.commonEnv().printer().print(new StringBuilder(), this).toString();
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
