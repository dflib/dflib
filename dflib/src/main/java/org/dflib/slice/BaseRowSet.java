package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.RowSet;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.f.IntObjectFunction2;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.IntSingleValueSeries;
import org.dflib.series.RowMappedSeries;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final Index sourceColumnsIndex;
    protected final Series[] sourceColumns;

    protected BaseRowSet(DataFrame source, Series<?>[] sourceColumns) {
        this.source = source;
        this.sourceColumnsIndex = source.getColumnsIndex();
        this.sourceColumns = sourceColumns;
    }

    @Override
    public DataFrame expand(String columnName) {
        return expand(sourceColumnsIndex.position(columnName));
    }

    @Override
    public DataFrame expand(int columnPos) {

        ColumnExpander expander = ColumnExpander.expand(doSelect(sourceColumns[columnPos]));

        RowSetMerger merger = merger();
        RowSetMerger expandMerger = merger.expandCols(expander);
        RowSetMerger stretchMerger = merger.stretchCols(expander);

        int w = source.width();
        Series[] explodedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            RowSetMerger m = i == columnPos ? expandMerger : stretchMerger;
            explodedColumns[i] = m.merge(source.getColumn(i), expander.getExpanded());
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(explodedColumns);
    }

    @Override
    public DataFrame map(Exp<?>... exps) {

        int w = exps.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return mapByColumn((i, rowsAsDf) -> exps[i].eval(rowsAsDf));
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {

        int w = mappers.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return mapByColumn((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]));
    }

    @Override
    public DataFrame map(RowMapper mapper) {
        Series<?>[] mapped = mapper().map(sourceColumnsIndex, sourceColumns, mapper);
        return DataFrame.byColumn(sourceColumnsIndex).of(mapped);
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() < 2) {
            return source;
        }

        DataFrame rowsAsDf = select().sort(sorters);

        RowSetMerger merger = merger();

        int w = sourceColumnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = merger.merge(sourceColumns[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    @Override
    public DataFrame unique() {
        return unique(sourceColumnsIndex.getLabels());
    }

    @Override
    public DataFrame unique(String... uniqueKeyColumns) {
        if (uniqueKeyColumns.length == 0) {
            throw new IllegalArgumentException("No 'columnNamesToCompare' for uniqueness checks");
        }

        int w = sourceColumns.length;
        if (w == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();
        BooleanSeries uniqueIndex = rowsAsDf
                .over().partitioned(uniqueKeyColumns).rowNumber()
                .eq(new IntSingleValueSeries(1, rowsAsDf.height()));

        if (uniqueIndex.isTrue()) {
            return source;
        }

        RowSetMerger merger = merger().removeUnmatchedRows(uniqueIndex);
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = merger.merge(sourceColumns[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    @Override
    public DataFrame unique(int... uniqueKeyColumns) {
        if (uniqueKeyColumns.length == 0) {
            throw new IllegalArgumentException("No 'columnPositionsToCompare' for uniqueness checks");
        }

        int w = sourceColumns.length;
        if (w == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();
        BooleanSeries uniqueIndex = rowsAsDf
                .over().partitioned(uniqueKeyColumns).rowNumber()
                .eq(new IntSingleValueSeries(1, rowsAsDf.height()));

        if (uniqueIndex.isTrue()) {
            return source;
        }

        RowSetMerger merger = merger().removeUnmatchedRows(uniqueIndex);
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = merger.merge(sourceColumns[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    @Override
    public DataFrame select() {
        return new ColumnDataFrame(null, sourceColumnsIndex, doSelect());
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, sourceColumnsIndex.rename(oldToNewNames), doSelect());
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(null, sourceColumnsIndex.rename(renamer), doSelect());
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return new ColumnDataFrame(null, sourceColumnsIndex.rename(newColumnNames), doSelect());
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return selectByColumn((i, rowsAsDf) -> exps[i].eval(rowsAsDf));
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        int h = sourceColumns[0].size();
        int ih = size();

        ColumnsRowProxy from = new ColumnsRowProxy(sourceColumnsIndex, sourceColumns, h);
        MultiArrayRowBuilder to = new MultiArrayRowBuilder(sourceColumnsIndex, ih);

        doSelectByRow(mapper, from, to);

        return DataFrame.byColumn(sourceColumnsIndex).of(to.getData());
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return selectByColumn((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]));
    }

    @Override
    public DataFrame selectExpand(String columnName) {
        return selectExpand(sourceColumnsIndex.position(columnName));
    }

    @Override
    public DataFrame selectExpand(int columnPos) {

        Series<?>[] rowSetCols = doSelect();
        ColumnExpander expander = ColumnExpander.expand(rowSetCols[columnPos]);
        int[] stretchIndex = expander.getStretchIndex();

        int w = source.width();
        Series[] explodedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            explodedColumns[i] = i == columnPos
                    ? expander.getExpanded()
                    : rowSetCols[i].select(stretchIndex);
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(explodedColumns);
    }

    protected DataFrame mapByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();

        RowSetMerger merger = merger();
        int w = sourceColumnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = merger.merge(sourceColumns[i], columnMaker.apply(i, rowsAsDf));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    protected DataFrame selectByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        DataFrame rowsAsDf = select();

        int w = sourceColumnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = columnMaker.apply(i, rowsAsDf);
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    protected abstract int size();

    protected Series<?>[] doSelect() {
        int w = sourceColumns.length;
        Series<?>[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = doSelect(sourceColumns[i]);
        }
        return to;
    }

    protected abstract void doSelectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to);

    protected abstract <T> Series<T> doSelect(Series<T> sourceColumn);

    protected abstract RowSetMapper mapper();

    protected abstract RowSetMerger merger();
}
