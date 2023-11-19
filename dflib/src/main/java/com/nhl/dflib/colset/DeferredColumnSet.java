package com.nhl.dflib.colset;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.ColumnSet;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.map.DynamicColsRowBuilder;
import com.nhl.dflib.series.RowMappedSeries;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A {@link ColumnSet} implementation that defers defining the resulting columns until the operation is applied, and
 * does it based on the operation semantics.
 *
 * @since 0.19
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
    public DataFrame rename(String... newColumnNames) {
        return new ColumnDataFrame(
                source.getColumnsIndex().rename(newColumnNames),
                sourceColumns);
    }

    @Override
    public DataFrame rename(UnaryOperator<String> renameFunction) {
        return new ColumnDataFrame(
                source.getColumnsIndex().rename(renameFunction),
                sourceColumns);
    }

    @Override
    public DataFrame rename(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(
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
            columns[i] = new SingleValueSeries<>(values[i], h);
        }

        return new ColumnDataFrame(source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNulls(Object value) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNulls(value);
        }

        return new ColumnDataFrame(source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsBackwards() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsBackwards();
        }

        return new ColumnDataFrame(source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsForward() {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsForward();
        }

        return new ColumnDataFrame(source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {
        int w = source.width();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(i).fillNullsFromSeries(series);
        }

        return new ColumnDataFrame(source.getColumnsIndex(), columns);
    }

    @Override
    public DataFrame select() {
        return source;
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        int w = exps.length;

        String[] labels = new String[w];
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            labels[i] = exps[i].getColumnName(source);
            columns[i] = exps[i].eval(source);
        }

        return ColumnSetIndex.of(source, Index.of(labels)).merge(columns);
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

        return new ColumnDataFrame(Index.of(labels), columns);
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

        return ColumnSetIndex.of(source, Index.of(labels)).merge(columns);
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        int srcW = source.width();

        String[] labels = new String[w];
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(srcW + i);
            columns[i] = new RowMappedSeries<>(source, mappers[i]);
        }

        return ColumnSetIndex.of(source, Index.of(labels)).merge(columns);
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

        return new ColumnDataFrame(Index.of(labels), columns);
    }

    @Override
    public DataFrame map(RowMapper mapper) {

        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            mapper.map(from, b);
            b.reset();
        });

        return ColumnSetIndex.of(source, Index.of(b.getLabels())).merge(b.getData());
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        DynamicColsRowBuilder b = new DynamicColsRowBuilder(source.height());
        source.forEach(from -> {
            mapper.map(from, b);
            b.reset();
        });

        return ColumnSetIndex.of(source, Index.of(b.getLabels())).select(b.getData());
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

        return ColumnSetIndex.of(source, positions).merge(columns);
    }

    @Override
    public DataFrame selectIterables(Exp<? extends Iterable<?>> mapper) {
        Series<?>[] columns = doMapIterables(mapper);

        int w = columns.length;
        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(Index.of(labels), columns);
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

        return ColumnSetIndex.of(source, positions).merge(columns);
    }

    @Override
    public DataFrame selectArrays(Exp<? extends Object[]> mapper) {
        Series<?>[] columns = doMapArrays(mapper);
        int w = columns.length;
        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = String.valueOf(i);
        }

        return new ColumnDataFrame(Index.of(labels), doMapArrays(mapper));
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

    @Override
    public DataFrame map(String... existingColumns) {

        // check that all columns exist, but otherwise return the unchanged DataFrame, as we do not have target
        // column names specified. "getColumn(..)" with a non-existing name throws.
        for (String c : existingColumns) {
            source.getColumn(c);
        }

        return source;
    }

    @Override
    public DataFrame map(int... existingColumns) {

        // check that all columns exist, but otherwise return the unchanged DataFrame, as we do not have target
        // column names specified. "getColumn(..)" with a non-existing name throws.
        for (int c : existingColumns) {
            source.getColumn(c);
        }

        return source;
    }

    @Override
    public DataFrame select(String... existingColumns) {

        // select without renaming
        int w = existingColumns.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(existingColumns[i]);
        }

        return new ColumnDataFrame(Index.of(existingColumns), columns);
    }

    @Override
    public DataFrame select(int... existingColumns) {
        // select without renaming
        int w = existingColumns.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(existingColumns[i]);
        }

        return new ColumnDataFrame(source.getColumnsIndex().selectPositions(existingColumns), columns);
    }
}
