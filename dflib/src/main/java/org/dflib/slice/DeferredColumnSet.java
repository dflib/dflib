package org.dflib.slice;

import org.dflib.ColumnDataFrame;
import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
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
 *
 * @since 1.0.0-M19
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
    public DataFrame drop() {
        return DataFrame.empty();
    }

    @Override
    public DataFrame rename(String... newColumnNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(newColumnNames),
                sourceColumns);
    }

    @Override
    public DataFrame selectRename(String... newColumnNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(newColumnNames),
                sourceColumns);
    }

    @Override
    public DataFrame rename(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(renamer),
                sourceColumns);
    }

    @Override
    public DataFrame selectRename(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(renamer),
                sourceColumns);
    }

    @Override
    public DataFrame rename(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(oldToNewNames),
                sourceColumns);
    }

    @Override
    public DataFrame selectRename(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
                null,
                source.getColumnsIndex().rename(oldToNewNames),
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
    public DataFrame select() {
        return source;
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        int w = exps.length;

        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = exps[i].getColumnName(source);
        }

        return delegate(labels).map(exps);
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;

        String[] labels = new String[w];
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            labels[i] = exps[i].getColumnName(source);
            columns[i] = exps[i].eval(source);
        }

        return new ColumnDataFrame(null, Index.of(labels), columns);
    }

    @Override
    public DataFrame map(Series<?>... columns) {

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

        return delegate(labels).map(columns);
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        int srcW = source.width();

        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(srcW + i);
        }

        return delegate(labels).map(mappers);
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
    public DataFrame map(RowMapper mapper) {

        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return delegate(b.getLabels()).map(b.getData());
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
    public DataFrame mapIterables(Exp<? extends Iterable<?>> mapper) {
        Series<?>[] columns = doMapIterables(mapper);

        int w = columns.length;
        int srcW = source.width();

        int[] positions = new int[w];
        for (int i = 0; i < w; i++) {
            positions[i] = srcW + i;
        }

        return delegate(positions).mapIterables(mapper);
    }

    @Override
    public DataFrame selectIterables(Exp<? extends Iterable<?>> mapper) {
        Series<?>[] columns = doMapIterables(mapper);

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
    public DataFrame mapArrays(Exp<? extends Object[]> mapper) {

        Series<?>[] columns = doMapArrays(mapper);
        int w = columns.length;
        int[] positions = new int[w];
        int srcW = source.width();

        for (int i = 0; i < w; i++) {
            positions[i] = srcW + i;
        }

        return delegate(positions).mapArrays(mapper);
    }

    @Override
    public DataFrame selectArrays(Exp<? extends Object[]> mapper) {
        Series<?>[] columns = doMapArrays(mapper);
        int w = columns.length;
        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(null, Index.of(labels), doMapArrays(mapper));
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
        return FixedColumnSet.of(source, sourceColumns, csIndex);
    }

    private ColumnSet delegate(int[] csIndex) {
        return FixedColumnSet.of(source, sourceColumns, csIndex);
    }
}
