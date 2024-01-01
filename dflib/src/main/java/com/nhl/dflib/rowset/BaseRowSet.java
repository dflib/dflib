package com.nhl.dflib.rowset;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.RowSet;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.explode.Exploder;
import com.nhl.dflib.f.IntObjectFunction2;
import com.nhl.dflib.row.ColumnsRowProxy;
import com.nhl.dflib.row.MultiArrayRowBuilder;
import com.nhl.dflib.series.IntSingleValueSeries;
import com.nhl.dflib.series.RowMappedSeries;

/**
 * @since 1.0.0-M19
 */
public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final Index columnsIndex;
    protected final Series[] data;

    protected BaseRowSet(DataFrame source, Series[] data) {
        this.source = source;
        this.columnsIndex = source.getColumnsIndex();
        this.data = data;
    }

    @Override
    public DataFrame explode(String columnName) {
        return explode(columnsIndex.position(columnName));
    }

    @Override
    public DataFrame explode(int columnPos) {

        Exploder exploder = Exploder.explode(selectRowSet(data[columnPos]));

        int w = source.width();
        Series[] explodedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            explodedColumns[i] = i == columnPos
                    ? explodeMerge(source.getColumn(i), exploder.getExploded(), exploder.getStretchCounts())
                    : stretchMerge(source.getColumn(i), exploder.getStretchCounts());
        }

        return DataFrame.byColumn(columnsIndex).of(explodedColumns);
    }

    @Override
    public DataFrame map(Exp<?>... exps) {

        int w = exps.length;
        if (w != columnsIndex.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + columnsIndex.size() + ")");
        }

        return mapByColumn((i, rowsAsDf) -> exps[i].eval(rowsAsDf));
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {

        int w = mappers.length;
        if (w != columnsIndex.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + columnsIndex.size() + ")");
        }

        return mapByColumn((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]));
    }

    @Override
    public DataFrame map(RowMapper mapper) {

        int h = data[0].size();

        ColumnsRowProxy from = new ColumnsRowProxy(columnsIndex, data, h);
        MultiArrayRowBuilder to = new MultiArrayRowBuilder(columnsIndex, h);

        mapByRow(mapper, from, to);

        return DataFrame.byColumn(columnsIndex).of(to.getData());
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        if (data.length == 0) {
            return source;
        }

        if (data[0].size() < 2) {
            return source;
        }

        DataFrame rowsAsDf = select().sort(sorters);

        int w = columnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = noResizeMerge(data[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(columnsIndex).of(to);
    }

    @Override
    public DataFrame unique() {
        return unique(columnsIndex.getLabels());
    }

    @Override
    public DataFrame unique(String... columnNamesToCompare) {
        if (columnNamesToCompare.length == 0) {
            throw new IllegalArgumentException("No 'columnNamesToCompare' for uniqueness checks");
        }

        int w = data.length;
        if (w == 0) {
            return source;
        }

        if (data[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();
        BooleanSeries uniqueIndex = rowsAsDf
                .over().partitioned(columnNamesToCompare).rowNumber()
                .eq(new IntSingleValueSeries(1, rowsAsDf.height()));

        if (uniqueIndex.isTrue()) {
            return source;
        }

        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = shrinkMerge(data[i], rowsAsDf.getColumn(i), uniqueIndex);
        }

        return DataFrame.byColumn(columnsIndex).of(to);
    }

    @Override
    public DataFrame unique(int... columnPositionsToCompare) {
        if (columnPositionsToCompare.length == 0) {
            throw new IllegalArgumentException("No 'columnPositionsToCompare' for uniqueness checks");
        }

        int w = data.length;
        if (w == 0) {
            return source;
        }

        if (data[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();
        BooleanSeries uniqueIndex = rowsAsDf
                .over().partitioned(columnPositionsToCompare).rowNumber()
                .eq(new IntSingleValueSeries(1, rowsAsDf.height()));

        if (uniqueIndex.isTrue()) {
            return source;
        }

        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = shrinkMerge(data[i], rowsAsDf.getColumn(i), uniqueIndex);
        }

        return DataFrame.byColumn(columnsIndex).of(to);
    }

    @Override
    public DataFrame select() {

        int w = data.length;
        Series<?>[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = selectRowSet(data[i]);
        }

        return new ColumnDataFrame(null, columnsIndex, to);
    }

    protected abstract void mapByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to);

    protected DataFrame mapByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (data.length == 0) {
            return source;
        }

        if (data[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();

        int w = columnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = noResizeMerge(data[i], columnMaker.apply(i, rowsAsDf));
        }

        return DataFrame.byColumn(columnsIndex).of(to);
    }

    protected abstract <T> Series<T> selectRowSet(Series<T> sourceColumn);

    protected abstract <T> Series<T> noResizeMerge(Series<T> sourceColumn, Series<T> rowSetColumn);

    protected abstract <T> Series<T> shrinkMerge(
            Series<T> sourceColumn,
            Series<T> rowSetColumn,
            BooleanSeries rowSetIndex);

    protected abstract <T> Series<T> explodeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetExplodedColumn,
            IntSeries rowSetStretchCounts);

    protected abstract <T> Series<T> stretchMerge(
            Series<T> sourceColumn,
            IntSeries rowSetStretchCounts);

    protected static <T> Series<T> doNoResizeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetColumn,
            BooleanSeries sourceIndex) {

        // TODO: preserve primitive Series

        int len = sourceIndex.size();
        ObjectAccum<T> values = new ObjectAccum<>(len);

        // TODO: would it be faster if we populate all ObjectAccums for the DataFrame in a single loop?
        for (int i = 0, mi = 0; i < len; i++) {
            T val = sourceIndex.getBool(i) ? rowSetColumn.get(mi++) : sourceColumn.get(i);
            values.push(val);
        }

        return values.toSeries();
    }

    protected static <T> Series<T> doShrinkMerge(
            Series<T> sourceColumn,
            Series<T> rowSetColumn,
            BooleanSeries sourceIndex,
            BooleanSeries rowSetIndex) {

        // TODO: preserve primitive Series

        int len = sourceColumn.size();
        ObjectAccum<T> values = new ObjectAccum<>(len);

        // TODO: would it be faster if we populate all ObjectAccums for the DataFrame in a single loop?
        for (int i = 0, mi = 0; i < len; i++) {
            if (sourceIndex.getBool(i)) {
                if (rowSetIndex.getBool(mi)) {
                    values.push(rowSetColumn.get(mi));
                }
                // else - skip value

                mi++;
            } else {
                values.push(sourceColumn.get(i));
            }
        }

        return values.toSeries();
    }

    protected static <T> Series<T> doExplodeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetExplodedColumn,
            BooleanSeries sourceIndex,
            IntSeries rowSetStretchCounts) {

        // TODO: preserve primitive Series

        int len = sourceColumn.size();
        int elen = (int) (len - rowSetStretchCounts.size() + rowSetStretchCounts.sum());

        ObjectAccum<T> values = new ObjectAccum<>(elen);

        for (int i = 0, mi = 0, offset = 0; i < len; i++) {
            if (sourceIndex.getBool(i)) {

                int stretchCount = rowSetStretchCounts.getInt(mi++);
                for (int j = 0; j < stretchCount; j++) {
                    values.push(rowSetExplodedColumn.get(offset + j));
                }

                offset += stretchCount;
            } else {
                values.push(sourceColumn.get(i));
            }
        }

        return values.toSeries();
    }

    protected static <T> Series<T> doStretchMerge(
            Series<T> sourceColumn,
            BooleanSeries sourceIndex,
            IntSeries rowSetStretchCounts) {

        // TODO: preserve primitive Series

        int len = sourceIndex.size();
        int elen = (int) (len - rowSetStretchCounts.size() + rowSetStretchCounts.sum());

        ObjectAccum<T> values = new ObjectAccum<>(elen);

        for (int i = 0, mi = 0; i < len; i++) {
            int explode = sourceIndex.getBool(i) ? rowSetStretchCounts.getInt(mi++) : 1;
            if (explode > 1) {
                values.fill(values.size(), values.size() + explode, sourceColumn.get(i));
            } else {
                values.push(sourceColumn.get(i));
            }
        }

        return values.toSeries();
    }
}
