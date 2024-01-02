package org.dflib.colset;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.RowMappedSeries;
import org.dflib.series.SingleValueSeries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public class FixedColumnSet implements ColumnSet {

    private final ColumnSetIndex index;
    private final DataFrame source;

    public FixedColumnSet(ColumnSetIndex index, DataFrame source) {
        this.index = index;
        this.source = source;
    }

    @Override
    public DataFrame rename(String... newColumnNames) {
        int w = newColumnNames.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': column names size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        Index csIndex = index.targetIndex();
        Map<String, String> oldToNewMap = new HashMap<>((int) Math.ceil(w / 0.75));
        for (int i = 0; i < w; i++) {
            oldToNewMap.put(csIndex.getLabel(i), newColumnNames[i]);
        }

        return rename(oldToNewMap);
    }

    @Override
    public DataFrame rename(UnaryOperator<String> renameFunction) {
        Index csIndex = index.targetIndex();

        Map<String, String> oldToNewMap = new HashMap<>((int) Math.ceil(csIndex.size() / 0.75));
        for (String l : csIndex) {
            oldToNewMap.put(l, renameFunction.apply(l));
        }

        return rename(oldToNewMap);
    }

    @Override
    public DataFrame rename(Map<String, String> oldToNewNames) {

        int w = index.size();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = index.getOrCreateColumn(i);
        }

        return index.merge(columns, oldToNewNames);
    }

    @Override
    public DataFrame fill(Object... values) {

        int w = values.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'fill': values size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        int h = source.height();

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofVal(values[i], h);
        }

        return index.merge(columns);
    }

    @Override
    public DataFrame fillNulls(Object value) {

        if (value == null) {
            return source;
        }

        int w = index.size();
        int h = source.height();

        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = index.getOrCreateColumn(i,
                    e -> ((Series<Object>) e).fillNulls(value),
                    () -> Series.ofVal(value, h));
        }

        return index.merge(columns);
    }

    @Override
    public DataFrame fillNullsBackwards() {

        int w = index.size();
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = index.getOrCreateColumn(
                    i,
                    e -> e.fillNullsBackwards(),
                    () -> new SingleValueSeries<>(null, h));
        }

        return index.merge(columns);
    }

    @Override
    public DataFrame fillNullsForward() {

        int w = index.size();
        int h = source.height();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = index.getOrCreateColumn(
                    i,
                    e -> e.fillNullsForward(),
                    () -> new SingleValueSeries<>(null, h));
        }

        return index.merge(columns);
    }

    @Override
    public DataFrame fillNullsFromSeries(Series<?> series) {

        int w = index.size();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            Series s = index.getOrCreateColumn(i);
            columns[i] = s.fillNullsFromSeries(series);
        }

        return index.merge(columns);
    }

    @Override
    public DataFrame select() {

        int w = index.size();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = index.getOrCreateColumn(i);
        }

        return index.select(columns);
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        return index.merge(doMap(exps));
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return index.select(doMap(exps));
    }

    private Series<?>[] doMap(Exp<?>[] exps) {

        int w = exps.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': Exp[] size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = exps[i].eval(source);
        }

        return columns;
    }

    @Override
    public DataFrame map(Series<?>... columns) {

        int w = index.size();

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

        return index.merge(columns);
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... exps) {
        return index.merge(doMap(exps));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... exps) {
        return index.select(doMap(exps));
    }

    private Series<?>[] doMap(RowToValueMapper<?>[] mappers) {

        int w = mappers.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'map': RowToValueMappers size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = new RowMappedSeries<>(source, mappers[i]);
        }

        return columns;
    }

    @Override
    public DataFrame map(RowMapper mapper) {

        MultiArrayRowBuilder b = new MultiArrayRowBuilder(index.targetIndex(), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return index.merge(b.getData());
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        MultiArrayRowBuilder b = new MultiArrayRowBuilder(index.targetIndex(), source.height());

        source.forEach(from -> {
            b.next();
            mapper.map(from, b);
        });

        return index.select(b.getData());
    }

    @Override
    public DataFrame mapIterables(Exp<? extends Iterable<?>> mapper) {
        return index.merge(doMapIterables(mapper));
    }

    @Override
    public DataFrame selectIterables(Exp<? extends Iterable<?>> mapper) {
        return index.select(doMapIterables(mapper));
    }

    private Series<?>[] doMapIterables(Exp<? extends Iterable<?>> mapper) {

        Series<? extends Iterable<?>> ranges = mapper.eval(source);

        int w = index.size();
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
    public DataFrame mapArrays(Exp<? extends Object[]> mapper) {
        return index.merge(doMapArrays(mapper));
    }

    @Override
    public DataFrame selectArrays(Exp<? extends Object[]> mapper) {
        return index.select(doMapArrays(mapper));
    }

    private Series<?>[] doMapArrays(Exp<? extends Object[]> mapper) {
        Series<? extends Object[]> ranges = mapper.eval(source);

        int w = index.size();
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

    @Override
    public DataFrame map(String... existingColumns) {
        int w = existingColumns.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': existing columns size is different from the provided column set size: " + w + " vs. " + index.size());
        }

        // columns will be present in the same order and untransformed from the original
        int dfw = source.width();
        Series<?>[] columns = new Series[dfw];
        for (int i = 0; i < dfw; i++) {
            columns[i] = source.getColumn(i);
        }

        // index must undergo renaming
        Map<String, String> renameMap = new HashMap<>();
        Index csIndex = index.targetIndex();
        for (int i = 0; i < w; i++) {
            renameMap.put(existingColumns[i], csIndex.getLabel(i));
        }

        return index.replace(columns, renameMap);
    }

    @Override
    public DataFrame map(int... existingColumns) {
        int w = existingColumns.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': existing columns size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        // columns will be present in the same order and untransformed from the original
        int dfw = source.width();
        Series<?>[] columns = new Series[dfw];
        for (int i = 0; i < dfw; i++) {
            columns[i] = source.getColumn(i);
        }

        // index must undergo renaming
        Map<String, String> renameMap = new HashMap<>();
        Index renameIndex = index.targetIndex();
        Index sourceIndex = source.getColumnsIndex();

        for (int i = 0; i < w; i++) {
            renameMap.put(sourceIndex.getLabel(existingColumns[i]), renameIndex.getLabel(i));
        }

        return index.replace(columns, renameMap);
    }

    @Override
    public DataFrame select(String... existingColumns) {
        int w = existingColumns.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': existing columns size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(existingColumns[i]);
        }

        return index.select(columns);
    }

    @Override
    public DataFrame select(int... existingColumns) {
        int w = existingColumns.length;
        if (w != index.size()) {
            throw new IllegalArgumentException(
                    "Can't perform 'rename': existing columns size is different from the ColumnSet size: " + w + " vs. " + index.size());
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = source.getColumn(existingColumns[i]);
        }

        return index.select(columns);
    }
}
