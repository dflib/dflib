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
import org.dflib.row.DynamicColsRowBuilder;
import org.dflib.series.RowMappedSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public DeferredColumnSet(DataFrame source, Series<?>[] sourceColumns) {
        this.source = source;
        this.sourceColumns = sourceColumns;
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
                sourceColumns);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return new ColumnDataFrame(
                null,
                Index.of(newColumnNames),
                sourceColumns);
    }

    @Override
    public DataFrame as(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(renamer),
                sourceColumns);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(renamer),
                sourceColumns);
    }

    @Override
    public DataFrame as(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(oldToNewNames),
                sourceColumns);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().replace(oldToNewNames),
                sourceColumns);
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

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNulls(Object value) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNulls(value);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsBackwards() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsBackwards();
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsForward() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsForward();
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsFromSeries(series);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsWithExp(Exp<?> replacementValuesExp) {
        return fillNullsFromSeries(replacementValuesExp.eval(source));
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
        return source;
    }

    @Override
    public DataFrame select() {
        return source;
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return delegate(Exps.labels(source, exps)).merge(exps);
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = exps[i].eval(source);
        }

        return new ColumnDataFrame(null,
                Index.ofDeduplicated(Exps.labels(source, exps)),
                columns);
    }

    @Override
    public DataFrame merge(Series<?>... columns) {

        int w = columns.length;
        int h = source.height();
        int srcW = source.width();
        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {

            if (columns[i].size() != h) {
                throw new IllegalArgumentException("The mapped column height (" + columns[i].size() + ") is different from the DataFrame height (" + h + ")");
            }

            labels[i] = String.valueOf(srcW + i);
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
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;

        String[] labels = new String[w];
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
            columns[i] = new RowMappedSeries<>(source, mappers[i]);
        }

        return new ColumnDataFrame(null, Index.of(labels), columns);
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
    public DataFrame select(RowMapper mapper) {
        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return new ColumnDataFrame(null, Index.of(b.getLabels()), b.getData());
    }

    @Override
    public DataFrame expand(Exp<? extends Iterable<?>> splitExp) {
        Series<?>[] columns = doMapIterables(splitExp);

        int w = columns.length;
        int srcW = source.width();

        int[] positions = new int[w];
        for (int i = 0; i < w; i++) {
            positions[i] = srcW + i;
        }

        return delegate(positions).expand(splitExp);
    }

    @Override
    public DataFrame selectExpand(Exp<? extends Iterable<?>> splitExp) {
        Series<?>[] columns = doMapIterables(splitExp);

        int w = columns.length;
        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(null, Index.of(labels), columns);
    }

    private Series<?>[] doMapIterables(Exp<? extends Iterable<?>> mapper) {
        Series<? extends Iterable<?>> ranges = mapper.eval(source);

        int h = source.height();
        List<Object[]> data = new ArrayList<>();

        for (int i = 0; i < h; i++) {

            Iterable<?> r = ranges.get(i);
            if (r == null) {
                continue;
            }

            Iterator<?> rit = r.iterator();
            for (int j = 0; rit.hasNext(); j++) {

                if (j >= data.size()) {
                    data.add(new Object[h]);
                }

                data.get(j)[i] = rit.next();
            }
        }

        int w = data.size();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data.get(i));
        }

        return columns;
    }

    @Override
    public DataFrame expandArray(Exp<? extends Object[]> splitExp) {

        Series<?>[] columns = doMapArrays(splitExp);
        int w = columns.length;
        int[] positions = new int[w];
        int srcW = source.width();

        for (int i = 0; i < w; i++) {
            positions[i] = srcW + i;
        }

        return delegate(positions).expandArray(splitExp);
    }

    @Override
    public DataFrame selectExpandArray(Exp<? extends Object[]> splitExp) {
        Series<?>[] columns = doMapArrays(splitExp);
        int w = columns.length;
        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(null, Index.of(labels), doMapArrays(splitExp));
    }

    @Override
    public DataFrame agg(Exp<?>... aggregators) {
        Series<?>[] aggregated = DataFrameAggregator.agg(source, aggregators);
        Index index = Exps.index(source, aggregators);
        return new ColumnDataFrame(null, index, aggregated);
    }

    private Series<?>[] doMapArrays(Exp<? extends Object[]> mapper) {
        Series<? extends Object[]> ranges = mapper.eval(source);

        int h = source.height();
        List<Object[]> data = new ArrayList<>();

        for (int i = 0; i < h; i++) {

            Object[] r = ranges.get(i);
            if (r == null) {
                continue;
            }

            int rw = r.length;
            for (int j = 0; j < rw; j++) {

                if (j >= data.size()) {
                    data.add(new Object[h]);
                }

                data.get(j)[i] = r[j];
            }
        }

        int w = data.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data.get(i));
        }

        return columns;
    }

    private ColumnSet delegate(String[] csIndex) {
        return FixedColumnSet.of(source, csIndex);
    }

    private ColumnSet delegate(int[] csIndex) {
        return FixedColumnSet.of(source, csIndex);
    }
}
