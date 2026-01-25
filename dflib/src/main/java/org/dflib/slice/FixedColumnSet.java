package org.dflib.slice;

import org.dflib.BoolValueMapper;
import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.ColumnSet;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DoubleValueMapper;
import org.dflib.Exp;
import org.dflib.FloatValueMapper;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowPredicate;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.Udf1;
import org.dflib.exp.ExpEvaluator;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.RowMappedSeries;
import org.dflib.series.SingleValueSeries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FixedColumnSet implements ColumnSet {

    private final DataFrame source;
    private final UnaryOperator<Series<?>> colCompactor;

    // defer index resolving until a terminal method is called, as the DataFrame can be affected by expansions, etc.
    private final Function<Index, String[]> csIndexResolver;

    public static FixedColumnSet of(DataFrame source, Index csIndex) {
        return new FixedColumnSet(source, i -> csIndex.toArray(), null);
    }

    public static FixedColumnSet of(DataFrame source, String[] csIndex) {
        return new FixedColumnSet(source, i -> csIndex, null);
    }

    /**
     * @since 2.0.0
     */
    public static FixedColumnSet of(DataFrame source, Predicate<String> condition) {
        return new FixedColumnSet(source, i -> i.select(condition).toArray(), null);
    }

    public static FixedColumnSet ofAppend(DataFrame source, String[] csIndex) {
        return new FixedColumnSet(
                source,
                i -> FixedColumnSetIndex.ofAppend(i, csIndex).getLabels(),
                null);
    }

    public static FixedColumnSet of(DataFrame source, int[] csIndex) {
        return new FixedColumnSet(
                source,
                i -> FixedColumnSetIndex.of(i, csIndex).getLabels(),
                null);
    }

    /**
     * @since 2.0.0
     */
    public static FixedColumnSet ofColsRange(DataFrame source, int fromInclusive, int toExclusive) {
        return new FixedColumnSet(
                source,
                i -> FixedColumnSetIndex.of(i, fromInclusive, toExclusive).getLabels(),
                null);
    }

    /**
     * @since 2.0.0
     */
    public static FixedColumnSet ofColsExcept(DataFrame source, String[] columns) {
        return new FixedColumnSet(source, i -> i.selectExcept(columns).toArray(), null);
    }

    /**
     * @since 2.0.0
     */
    public static FixedColumnSet ofColsExcept(DataFrame source, int[] columns) {
        return new FixedColumnSet(source, i -> i.select(i.positionsExcept(columns)).toArray(), null);
    }

    /**
     * @since 2.0.0
     */
    public static FixedColumnSet ofColsExcept(DataFrame source, Predicate<String> condition) {
        return new FixedColumnSet(source, i -> i.selectExcept(condition).toArray(), null);
    }

    FixedColumnSet(DataFrame source, Function<Index, String[]> csIndexResolver, UnaryOperator<Series<?>> colCompactor) {
        this.source = source;
        this.csIndexResolver = csIndexResolver;
        this.colCompactor = colCompactor;
    }

    @Override
    public ColumnSet expand(Exp<? extends Iterable<?>> splitExp) {
        return doExpand(ColumnSets.mapIterables(source, splitExp));
    }

    @Override
    public ColumnSet expandArray(Exp<? extends Object[]> splitExp) {
        return doExpand(ColumnSets.mapArrays(source, splitExp));
    }

    @Override
    public RowColumnSet rows() {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rows().cols(csIndex());
    }

    @Override
    public RowColumnSet rows(IntSeries positions) {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rows(positions).cols(csIndex());
    }

    @Override
    public RowColumnSet rows(RowPredicate condition) {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rows(condition).cols(csIndex());
    }

    @Override
    public RowColumnSet rows(Condition condition) {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rows(condition).cols(csIndex());
    }

    @Override
    public RowColumnSet rows(BooleanSeries condition) {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rows(condition).cols(csIndex());
    }

    @Override
    public RowColumnSet rowsRange(int fromInclusive, int toExclusive) {
        // resolving csIndex here will only work for as long as RowColumnSet does not define operations that expand
        // its columns
        return source.rowsRange(fromInclusive, toExclusive).cols(csIndex());
    }

    @Override
    public DataFrame drop() {
        return source.colsExcept(csIndex()).select();
    }

    @Override
    public DataFrame as(String... newColumnNames) {

        String[] csIndex = csIndex();

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
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(newColumnNames),
                compactColsIfNeeded(doSelect(csIndex)));
    }

    @Override
    public DataFrame as(UnaryOperator<String> renamer) {

        String[] csIndex = csIndex();

        Map<String, String> oldToNewMap = new HashMap<>((int) Math.ceil(csIndex.length / 0.75));
        for (String l : csIndex) {
            oldToNewMap.put(l, renamer.apply(l));
        }

        return as(oldToNewMap);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        String[] csIndex = csIndex();

        return new ColumnDataFrame(
                null,
                Index.of(csIndex).replace(renamer),
                compactColsIfNeeded(doSelect(csIndex)));
    }

    @Override
    public DataFrame as(Map<String, String> oldToNewNames) {
        String[] csIndex = csIndex();

        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(csIndex, i);
        }

        return doMerge(csIndex, columns, oldToNewNames);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.of(csIndex).replace(oldToNewNames),
                compactColsIfNeeded(doSelect(csIndex)));
    }

    @Override
    public DataFrame fill(Object... values) {
        String[] csIndex = csIndex();

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

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame fillNulls(Object value) {

        if (value == null) {
            return source;
        }

        String[] csIndex = csIndex();

        int w = csIndex.length;
        int h = source.height();

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    csIndex,
                    i,
                    e -> ((Series<Object>) e).fillNulls(value),
                    () -> Series.ofVal(value, h));
        }

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame fillNullsBackwards() {
        String[] csIndex = csIndex();

        int w = csIndex.length;
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    csIndex,
                    i,
                    Series::fillNullsBackwards,
                    () -> new SingleValueSeries<>(null, h));
        }

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame fillNullsForward() {
        String[] csIndex = csIndex();

        int w = csIndex.length;
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(
                    csIndex,
                    i,
                    Series::fillNullsForward,
                    () -> new SingleValueSeries<>(null, h));
        }

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {
        String[] csIndex = csIndex();

        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = getOrCreateColumn(csIndex, i);
            columns[i] = s.fillNullsFromSeries(series);
        }

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame fillNullsWithExp(Exp<?> replacementValuesExp) {
        return fillNullsFromSeries(replacementValuesExp.eval(source));
    }

    @Override
    public ColumnSet compact() {
        return new FixedColumnSet(source, csIndexResolver, Series::compact);
    }

    @Override
    public ColumnSet compactBool() {
        return new FixedColumnSet(source, csIndexResolver, Series::compactBool);
    }

    @Override
    public <V> ColumnSet compactBool(BoolValueMapper<V> mapper) {
        BoolValueMapper raw = mapper;
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactBool(raw));
    }

    @Override
    public ColumnSet compactInt(int forNull) {
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactInt(forNull));
    }

    @Override
    public <V> ColumnSet compactInt(IntValueMapper<V> mapper) {
        IntValueMapper raw = mapper;
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactInt(raw));
    }

    @Override
    public ColumnSet compactLong(long forNull) {
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactLong(forNull));
    }

    @Override
    public <V> ColumnSet compactLong(LongValueMapper<V> mapper) {
        LongValueMapper raw = mapper;
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactLong(raw));
    }

    @Override
    public ColumnSet compactFloat(float forNull) {
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactFloat(forNull));
    }

    @Override
    public <V> ColumnSet compactFloat(FloatValueMapper<V> mapper) {
        FloatValueMapper raw = mapper;
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactFloat(raw));
    }

    @Override
    public ColumnSet compactDouble(double forNull) {
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactDouble(forNull));
    }

    @Override
    public <V> ColumnSet compactDouble(DoubleValueMapper<V> mapper) {
        DoubleValueMapper raw = mapper;
        return new FixedColumnSet(source, csIndexResolver, s -> s.compactDouble(raw));
    }

    @Override
    public DataFrame merge() {
        String[] csIndex = csIndex();

        return doMerge(csIndex, doSelect(csIndex));
    }

    @Override
    public DataFrame select() {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(doSelect(csIndex)));
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        String[] csIndex = csIndex();
        return doMerge(csIndex, doMap(csIndex, exps));
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(doMap(csIndex, exps)));
    }

    private Series<?>[] doMap(String[] csIndex, Exp<?>[] exps) {

        int w = exps.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': Exp[] size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        return ExpEvaluator.eval(source, exps);
    }

    @Override
    public DataFrame merge(Series<?>... columns) {
        String[] csIndex = csIndex();

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

        return doMerge(csIndex, columns);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... exps) {
        String[] csIndex = csIndex();
        return doMerge(csIndex, doMap(csIndex, exps));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... exps) {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(doMap(csIndex, exps)));
    }

    @Deprecated
    @Override
    public DataFrame selectExpand(Exp<? extends Iterable<?>> splitExp) {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(doMapIterables(csIndex, splitExp)));
    }

    @Deprecated
    private Series<?>[] doMapIterables(String[] csIndex, Exp<? extends Iterable<?>> mapper) {

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

    @Deprecated
    @Override
    public DataFrame selectExpandArray(Exp<? extends Object[]> splitExp) {
        String[] csIndex = csIndex();
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(doMapArrays(csIndex, splitExp)));
    }

    private Series<?>[] doMapArrays(String[] csIndex, Exp<? extends Object[]> mapper) {
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

    private Series<?>[] doMap(String[] csIndex, RowToValueMapper<?>[] mappers) {

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
        String[] csIndex = csIndex();

        MultiArrayRowBuilder b = new MultiArrayRowBuilder(Index.of(csIndex), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return doMerge(csIndex, b.getData());
    }

    @Override
    public DataFrame mergeAll(Udf1<?, ?> udf) {

        Index srcIndex = source.getColumnsIndex();
        String[] csIndex = csIndex();
        int w = csIndex.length;
        Exp[] exps = new Exp[w];
        for (int i = 0; i < w; i++) {
            exps[i] = srcIndex.contains(csIndex[i]) ? udf.call(csIndex[i]) : Exp.$val(null);
        }

        return merge(exps);
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        String[] csIndex = csIndex();

        MultiArrayRowBuilder b = new MultiArrayRowBuilder(Index.of(csIndex), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(b.getData()));
    }

    @Override
    public DataFrame selectAll(Udf1<?, ?> udf) {

        Index srcIndex = source.getColumnsIndex();
        String[] csIndex = csIndex();
        int w = csIndex.length;
        Exp[] exps = new Exp[w];
        for (int i = 0; i < w; i++) {
            exps[i] = srcIndex.contains(csIndex[i]) ? udf.call(csIndex[i]) : Exp.$val(null);
        }

        return select(exps);
    }

    @Override
    public DataFrame agg(Exp<?>... aggregatingExps) {
        String[] csIndex = csIndex();

        int w = aggregatingExps.length;
        if (w != csIndex.length) {
            throw new IllegalArgumentException(
                    "Can't perform 'agg': Exp[] size is different from the ColumnSet size: " + w + " vs. " + csIndex.length);
        }

        Series<?>[] aggregated = ExpEvaluator.reduce(source, aggregatingExps);
        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(csIndex),
                compactColsIfNeeded(aggregated));
    }

    private Series<?> getOrCreateColumn(String[] csIndex, int pos) {
        String name = csIndex[pos];
        return source.getColumnsIndex().contains(name)
                ? source.getColumn(name)
                : new SingleValueSeries<>(null, source.height());
    }

    private Series<?> getOrCreateColumn(
            String[] csIndex,
            int pos,
            UnaryOperator<Series<?>> andApplyToExisting,
            Supplier<Series<?>> createNew) {

        String name = csIndex[pos];
        return source.getColumnsIndex().contains(name)
                ? andApplyToExisting.apply(source.getColumn(name))
                : createNew.get();
    }

    private DataFrame doMerge(String[] csIndex, Series<?>[] columns) {
        return ColumnSetMerger.merge(source, csIndex, compactColsIfNeeded(columns));
    }

    private DataFrame doMerge(String[] csIndex, Series<?>[] columns, Map<String, String> oldToNewNames) {
        return ColumnSetMerger.mergeAs(source,
                csIndex,
                Index.of(csIndex).replace(oldToNewNames).toArray(),
                compactColsIfNeeded(columns));
    }

    private Series<?>[] doSelect(String[] csIndex) {
        int w = csIndex.length;
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = getOrCreateColumn(csIndex, i);
        }

        return columns;
    }

    private ColumnSet doExpand(Series[] expansionColumns) {
        return new FixedColumnSet(
                source.cols().merge(expansionColumns),
                csIndexResolver,
                colCompactor
        );
    }

    // should only be called by terminal methods, as we need to defer until all expansions and other operations
    // changing the columns of the source are applied
    private String[] csIndex() {
        return csIndexResolver.apply(source.getColumnsIndex());
    }

    private Series<?>[] compactColsIfNeeded(Series<?>[] columns) {
        if (colCompactor == null) {
            return columns;
        }

        int w = columns.length;
        Series<?>[] compact = new Series[w];

        for (int i = 0; i < w; i++) {
            compact[i] = colCompactor.apply(columns[i]);
        }
        return compact;
    }
}
