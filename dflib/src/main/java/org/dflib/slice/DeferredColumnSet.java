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
import org.dflib.agg.DataFrameAggregator;
import org.dflib.exp.Exps;
import org.dflib.index.StringDeduplicator;
import org.dflib.row.DynamicColsRowBuilder;
import org.dflib.series.RowMappedSeries;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A {@link ColumnSet} implementation that defers defining the resulting columns until the operation is applied, and
 * does it based on the operation semantics.
 */
public class DeferredColumnSet implements ColumnSet {

    private final DataFrame source;

    // direct access to source columns speeds up some operations, like "rename"
    private final Series<?>[] sourceColumns;

    private final boolean compact;

    /**
     * @since 2.0.0
     */
    public static DeferredColumnSet of(DataFrame source, Series<?>[] sourceColumns) {
        return new DeferredColumnSet(source, sourceColumns, false);
    }

    /**
     * @deprecated in favor of {@link #of(DataFrame, Series[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public DeferredColumnSet(DataFrame source, Series<?>[] sourceColumns) {
        this(source, sourceColumns, false);
    }

    private DeferredColumnSet(DataFrame source, Series<?>[] sourceColumns, boolean compact) {
        this.source = source;
        this.sourceColumns = sourceColumns;
        this.compact = compact;
    }

    @Override
    public ColumnSet expand(Exp<? extends Iterable<?>> splitExp) {
        return expandedDelegate(ColumnSets.mapIterables(source, splitExp));
    }

    @Override
    public ColumnSet expandArray(Exp<? extends Object[]> splitExp) {
        return expandedDelegate(ColumnSets.mapArrays(source, splitExp));
    }

    @Override
    public RowColumnSet rows() {
        return source.rows().cols();
    }

    @Override
    public RowColumnSet rows(IntSeries positions) {
        return source.rows(positions).cols();
    }

    @Override
    public RowColumnSet rows(RowPredicate condition) {
        return source.rows(condition).cols();
    }

    @Override
    public RowColumnSet rows(Condition condition) {
        return source.rows(condition).cols();
    }

    @Override
    public RowColumnSet rows(BooleanSeries condition) {
        return source.rows(condition).cols();
    }

    @Override
    public RowColumnSet rowsRange(int fromInclusive, int toExclusive) {
        return source.rowsRange(fromInclusive, toExclusive).cols();
    }

    @Override
    public DataFrame drop() {
        return DataFrame.empty();
    }

    @Override
    public DataFrame as(String... newColumnNames) {
        return new ColumnDataFrame(
                null,
                Index.of(newColumnNames),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return new ColumnDataFrame(
                null,
                Index.of(newColumnNames),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame as(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(renamer),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(renamer),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame as(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(oldToNewNames),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(oldToNewNames),
                compactColsIfNeeded(sourceColumns));
    }

    @Override
    public DataFrame fill(Object... values) {
        int w = source.width();
        if (values.length != w) {
            throw new IllegalArgumentException(
                    "Can't perform 'fill': values size is different from the ColumnSet size: " + values.length + " vs. " + w);
        }

        int h = source.height();

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofVal(values[i], h);
        }

        return new ColumnDataFrame(
                null,
                source.getColumnsIndex(),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame fillNulls(Object value) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNulls(value);
        }

        return new ColumnDataFrame(
                null,
                source.getColumnsIndex(),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame fillNullsBackwards() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsBackwards();
        }

        return new ColumnDataFrame(
                null,
                source.getColumnsIndex(),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame fillNullsForward() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsForward();
        }

        return new ColumnDataFrame(
                null,
                source.getColumnsIndex(),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsFromSeries(series);
        }

        return new ColumnDataFrame(
                null,
                source.getColumnsIndex(),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame fillNullsWithExp(Exp<?> replacementValuesExp) {
        return fillNullsFromSeries(replacementValuesExp.eval(source));
    }

    @Override
    public ColumnSet compact() {
        return this.compact ? this : new DeferredColumnSet(source, sourceColumns, true);
    }

    @Override
    public DataFrame compactBool() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactBool();
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public <V> DataFrame compactBool(BoolValueMapper<V> mapper) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactBool(mapper);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame compactInt(int forNull) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactInt(forNull);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public <V> DataFrame compactInt(IntValueMapper<V> mapper) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactInt(mapper);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame compactLong(long forNull) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactLong(forNull);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public <V> DataFrame compactLong(LongValueMapper<V> mapper) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactLong(mapper);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame compactFloat(float forNull) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactFloat(forNull);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public <V> DataFrame compactFloat(FloatValueMapper<V> mapper) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactFloat(mapper);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame compactDouble(double forNull) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactDouble(forNull);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public <V> DataFrame compactDouble(DoubleValueMapper<V> mapper) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = source.getColumn(i);
            columns[i] = s.compactDouble(mapper);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame merge() {
        return compactDFIfNeeded();
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return delegate(Exps.labels(source, exps)).merge(exps);
    }

    @Override
    public DataFrame merge(Series<?>... columns) {

        // behaves as "colsAppend(..)"
        int w = columns.length;
        StringDeduplicator deduplicator = StringDeduplicator.of(source.getColumnsIndex(), w);

        int h = source.height();
        int srcW = source.width();
        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {

            if (columns[i].size() != h) {
                throw new IllegalArgumentException("The mapped column height (" + columns[i].size() + ") is different from the DataFrame height (" + h + ")");
            }

            labels[i] = deduplicator.nonConflictingName(String.valueOf(srcW + i));
        }

        return delegate(labels).merge(columns);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        int srcW = source.width();

        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(srcW + i);
        }

        return delegate(labels).merge(mappers);
    }

    @Override
    public DataFrame merge(RowMapper mapper) {

        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return delegate(b.getLabels()).merge(b.getData());
    }

    @Override
    public DataFrame select() {
        return compactDFIfNeeded();
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = exps[i].eval(source);
        }

        return new ColumnDataFrame(
                null,
                Index.ofDeduplicated(Exps.labels(source, exps)),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;

        String[] labels = new String[w];
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
            columns[i] = new RowMappedSeries<>(source, mappers[i]);
        }

        return new ColumnDataFrame(
                null,
                Index.of(labels),
                compactColsIfNeeded(columns));
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return new ColumnDataFrame(
                null,
                Index.of(b.getLabels()),
                compactColsIfNeeded(b.getData()));
    }

    @Deprecated
    @Override
    public DataFrame selectExpand(Exp<? extends Iterable<?>> splitExp) {
        Series<?>[] columns = ColumnSets.mapIterables(source, splitExp);

        int w = columns.length;
        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(
                null,
                Index.of(labels),
                compactColsIfNeeded(columns));
    }

    @Deprecated
    @Override
    public DataFrame selectExpandArray(Exp<? extends Object[]> splitExp) {
        Series<?>[] columns = ColumnSets.mapArrays(source, splitExp);
        int w = columns.length;
        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(
                null,
                Index.of(labels),
                compactColsIfNeeded(ColumnSets.mapArrays(source, splitExp)));
    }

    @Override
    public DataFrame agg(Exp<?>... aggregators) {
        Series<?>[] aggregated = DataFrameAggregator.agg(source, aggregators);
        Index index = Exps.index(source, aggregators);
        return new ColumnDataFrame(
                null,
                index,
                compactColsIfNeeded(aggregated));
    }

    private ColumnSet delegate(String[] csIndex) {
        return compactCSIfNeeded(FixedColumnSet.of(source, csIndex));
    }

    private ColumnSet expandedDelegate(Series[] expansionColumns) {
        return compactCSIfNeeded(source.cols().merge(expansionColumns).cols());
    }

    private ColumnSet compactCSIfNeeded(ColumnSet cs) {
        return compact ? cs.compact() : cs;
    }

    private DataFrame compactDFIfNeeded() {
        if (!compact) {
            return source;
        }

        int w = sourceColumns.length;
        Series<?>[] compact = new Series[w];

        for (int i = 0; i < w; i++) {
            compact[i] = sourceColumns[i].compact();
        }
        return new ColumnDataFrame(null, source.getColumnsIndex(), compact);
    }

    private Series<?>[] compactColsIfNeeded(Series<?>[] columns) {
        if (!compact) {
            return columns;
        }

        int w = columns.length;
        Series<?>[] compact = new Series[w];

        for (int i = 0; i < w; i++) {
            compact[i] = columns[i].compact();
        }

        return compact;
    }
}
