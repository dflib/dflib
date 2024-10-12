package org.dflib.slice;

import org.dflib.BoolValueMapper;
import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.ColumnSet;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.DoubleValueMapper;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.IntValueMapper;
import org.dflib.LongSeries;
import org.dflib.LongValueMapper;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowPredicate;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.agg.DataFrameAggregator;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.RowMappedSeries;
import org.dflib.series.SingleValueSeries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FixedColumnSet implements ColumnSet {

    protected final DataFrame source;
    protected final String[] csIndex;

    public static FixedColumnSet of(DataFrame source, Index csIndex) {
        return new FixedColumnSet(source, csIndex.toArray());
    }

    public static FixedColumnSet of(DataFrame source, String[] csIndex) {
        return new FixedColumnSet(source, csIndex);
    }

    public static FixedColumnSet ofAppend(DataFrame source, String[] csIndex) {
        return new FixedColumnSet(
                source,
                FixedColumnSetIndex.ofAppend(source.getColumnsIndex(), csIndex).getLabels());
    }

    public static FixedColumnSet of(DataFrame source, int[] csIndex) {
        return new FixedColumnSet(
                source,
                FixedColumnSetIndex.of(source.getColumnsIndex(), csIndex).getLabels());
    }

    protected FixedColumnSet(DataFrame source, String[] csIndex) {
        this.source = source;
        this.csIndex = csIndex;
    }

    /**
     * Returns the index of this column set. This is the index produced by various "select" operations
     */
    public Index getColumnSetIndex() {
        return Index.ofDeduplicated(csIndex);
    }

    @Override
    public RowColumnSet rows() {
        return source.rows().cols(csIndex);
    }

    @Override
    public RowColumnSet rows(IntSeries positions) {
        return source.rows(positions).cols(csIndex);
    }

    @Override
    public RowColumnSet rows(RowPredicate condition) {
        return source.rows(condition).cols(csIndex);
    }

    @Override
    public RowColumnSet rows(Condition condition) {
        return source.rows(condition).cols(csIndex);
    }

    @Override
    public RowColumnSet rows(BooleanSeries condition) {
        return source.rows(condition).cols(csIndex);
    }

    @Override
    public RowColumnSet rowsRange(int fromInclusive, int toExclusive) {
        return source.rowsRange(fromInclusive, toExclusive).cols(csIndex);
    }

    @Override
    public DataFrame drop() {
        return source.colsExcept(csIndex).select();
    }

    @Override
    public DataFrame as(String... newColumnNames) {
        int w = newColumnNames.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': column names size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        Map<String, String> oldToNewMap = new HashMap<>((int) Math.ceil(w / 0.75));
        for (int i = 0; i < w; i++) {
            oldToNewMap.put(csIndex[i], newColumnNames[i]);
        }

        return as(oldToNewMap);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return new ColumnDataFrame(null, Index.ofDeduplicated(newColumnNames), doSelect());
    }

    @Override
    public DataFrame as(UnaryOperator<String> renamer) {

        Map<String, String> oldToNewMap = new HashMap<>((int) Math.ceil(csIndex.length / 0.75));
        for (String l : csIndex) {
            oldToNewMap.put(l, renamer.apply(l));
        }

        return as(oldToNewMap);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(null, Index.of(csIndex).replace(renamer), doSelect());

    }

    @Override
    public DataFrame as(Map<String, String> oldToNewNames) {

        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(i);
        }

        return doMerge(columns, oldToNewNames);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, Index.of(csIndex).replace(oldToNewNames), doSelect());
    }

    @Override
    public DataFrame fill(Object... values) {

        int w = values.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'fill': values size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        int h = source.height();

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofVal(values[i], h);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame fillNulls(Object value) {

        if (value == null) {
            return source;
        }

        int w = csIndex.length;
        int h = source.height();

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    i,
                    e -> ((Series<Object>) e).fillNulls(value),
                    () -> Series.ofVal(value, h));
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame fillNullsBackwards() {

        int w = csIndex.length;
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    i,
                    e -> e.fillNullsBackwards(),
                    () -> new SingleValueSeries<>(null, h));
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame fillNullsForward() {

        int w = csIndex.length;
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    i,
                    e -> e.fillNullsForward(),
                    () -> new SingleValueSeries<>(null, h));
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {

        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s.fillNullsFromSeries(series);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame fillNullsWithExp(Exp<?> replacementValuesExp) {
        return fillNullsFromSeries(replacementValuesExp.eval(source));
    }

    @Override
    public DataFrame compactBool() {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s instanceof BooleanSeries ? s.castAsBool() : s.mapAsBool(BoolValueMapper.fromObject());
        }

        return doMerge(columns);
    }

    @Override
    public <V> DataFrame compactBool(BoolValueMapper<V> converter) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s.mapAsBool(converter);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame compactInt(int forNull) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        IntValueMapper<?> converter = IntValueMapper.fromObject(forNull);

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s instanceof IntSeries ? s.castAsInt() : s.mapAsInt(converter);
        }

        return doMerge(columns);
    }

    @Override
    public <V> DataFrame compactInt(IntValueMapper<V> converter) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s.mapAsInt(converter);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame compactLong(long forNull) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        LongValueMapper<?> converter = LongValueMapper.fromObject(forNull);

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s instanceof LongSeries ? s.castAsLong() : s.mapAsLong(converter);
        }

        return doMerge(columns);
    }

    @Override
    public <V> DataFrame compactLong(LongValueMapper<V> converter) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s.mapAsLong(converter);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame compactDouble(double forNull) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        DoubleValueMapper<?> converter = DoubleValueMapper.fromObject(forNull);

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s instanceof DoubleSeries ? s.castAsDouble() : s.mapAsDouble(converter);
        }

        return doMerge(columns);
    }

    @Override
    public <V> DataFrame compactDouble(DoubleValueMapper<V> converter) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(i);
            columns[i] = s.mapAsDouble(converter);
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame select() {
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), doSelect());
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return doMerge(doMap(exps));
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), doMap(exps));
    }

    private Series<?>[] doMap(Exp<?>[] exps) {

        int w = exps.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': Exp[] size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = exps[i].eval(source);
        }

        return columns;
    }

    @Override
    public DataFrame merge(Series<?>... columns) {

        int w = csIndex.length;

        if (columns.length != w) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': Series[] size is different from the ColumnSet size: " + columns.length + " vs. " + w);
        }

        int h = source.height();
        for (int i = 0; i < w; i++) {
            if (columns[i].size() != h) {
                throw new IllegalArgumentException("The mapped column height (" + columns[i].size() + ") is different from the DataFrame height (" + h + ")");
            }
        }

        return doMerge(columns);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... exps) {
        return doMerge(doMap(exps));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... exps) {
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), doMap(exps));
    }

    private Series<?>[] doMap(RowToValueMapper<?>[] mappers) {

        int w = mappers.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': RowToValueMappers size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = new RowMappedSeries<>(source, mappers[i]);
        }

        return columns;
    }

    @Override
    public DataFrame merge(RowMapper mapper) {

        MultiArrayRowBuilder b = new MultiArrayRowBuilder(Index.of(csIndex), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return doMerge(b.getData());
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        MultiArrayRowBuilder b = new MultiArrayRowBuilder(Index.of(csIndex), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), b.getData());
    }

    @Override
    public DataFrame expand(Exp<? extends Iterable<?>> splitExp) {
        return doMerge(doMapIterables(splitExp));
    }

    @Override
    public DataFrame selectExpand(Exp<? extends Iterable<?>> splitExp) {
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), doMapIterables(splitExp));
    }

    private Series<?>[] doMapIterables(Exp<? extends Iterable<?>> mapper) {

        Series<? extends Iterable<?>> ranges = mapper.eval(source);

        int w = csIndex.length;
        int h = source.height();
        Object[][] data = new Object[w][h];
        for (int j = 0; j < w; j++) {
            data[j] = new Object[h];
        }

        for (int i = 0; i < h; i++) {

            Iterable<?> r = ranges.get(i);
            if (r == null) {
                continue;
            }

            Iterator<?> rit = r.iterator();
            for (int j = 0; j < w && rit.hasNext(); j++) {
                data[j][i] = rit.next();
            }
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data[i]);
        }

        return columns;
    }

    @Override
    public DataFrame expandArray(Exp<? extends Object[]> splitExp) {
        return doMerge(doMapArrays(splitExp));
    }

    @Override
    public DataFrame selectExpandArray(Exp<? extends Object[]> splitExp) {
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), doMapArrays(splitExp));
    }

    @Override
    public DataFrame agg(Exp<?>... aggregators) {
        int w = aggregators.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'agg': Exp[] size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        Series<?>[] aggregated = DataFrameAggregator.agg(source, aggregators);
        return new ColumnDataFrame(null, Index.ofDeduplicated(csIndex), aggregated);
    }

    private Series<?>[] doMapArrays(Exp<? extends Object[]> mapper) {
        Series<? extends Object[]> ranges = mapper.eval(source);

        int w = csIndex.length;
        int h = source.height();
        Object[][] data = new Object[w][h];
        for (int j = 0; j < w; j++) {
            data[j] = new Object[h];
        }

        for (int i = 0; i < h; i++) {

            Object[] r = ranges.get(i);
            if (r == null) {
                continue;
            }

            int rw = Math.min(r.length, w);
            for (int j = 0; j < rw; j++) {
                data[j][i] = r[j];
            }
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data[i]);
        }

        return columns;
    }

    private Series<?> getOrCreateColumn(int pos) {
        String name = csIndex[pos];
        return source.getColumnsIndex().contains(name)
                ? source.getColumn(name)
                : new SingleValueSeries<>(null, source.height());
    }

    private Series<?> getOrCreateColumn(
            int pos,
            UnaryOperator<Series<?>> andApplyToExisting,
            Supplier<Series<?>> createNew) {

        String name = csIndex[pos];
        return source.getColumnsIndex().contains(name)
                ? andApplyToExisting.apply(source.getColumn(name))
                : createNew.get();
    }

    private DataFrame doMerge(Series<?>[] columns) {
        return ColumnSetMerger.merge(source, csIndex, columns);
    }

    private DataFrame doMerge(Series<?>[] columns, Map<String, String> oldToNewNames) {
        return ColumnSetMerger.mergeAs(source,
                csIndex,
                Index.of(csIndex).replace(oldToNewNames).toArray(),
                columns);
    }

    private Series<?>[] doSelect() {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(i);
        }

        return columns;
    }
}
