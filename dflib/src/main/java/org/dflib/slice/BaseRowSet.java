package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowSet;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.f.IntObjectFunction2;
import org.dflib.f.Tuple2;
import org.dflib.series.IntSingleValueSeries;
import org.dflib.series.RowMappedSeries;
import org.dflib.sort.Comparators;
import org.dflib.sort.DataFrameSorter;
import org.dflib.sort.IntComparator;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final Index sourceColumnsIndex;
    protected final Series[] sourceColumns;
    protected final int expansionColumn;

    protected BaseRowSet(DataFrame source, Series<?>[] sourceColumns, int expansionColumn) {
        this.source = source;
        this.sourceColumnsIndex = source.getColumnsIndex();
        this.sourceColumns = sourceColumns;
        this.expansionColumn = expansionColumn;
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(source, this, df -> df.cols(), this::merger);
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columnsIndex), this::merger);
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(condition), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
    }

    @Override
    public RowSet expand(String columnName) {
        return expand(sourceColumnsIndex.position(columnName));
    }

    @Override
    public DataFrame merge() {
        return mergeByColumn((i, rowsAsDf) -> rowsAsDf.getColumn(i));
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {

        int w = exps.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return mergeByColumn((i, rowsAsDf) -> exps[i].eval(rowsAsDf));
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {

        int w = mappers.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return mergeByColumn((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]));
    }

    @Override
    public DataFrame merge(RowMapper mapper) {

        // TODO: duplicating "mergeByColumn" almost verbatim, except the mapper transform happens on top of row set
        //   DataFrame instead of column by column ... Unify?
        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        // this will do expansion within the RowSet scope if needed
        Tuple2<Series<?>[], ColumnExpander> rows = doSelect();

        RowSetMerger merger = expansionColumn >= 0 ? merger().expandCols(rows.two) : merger();

        DataFrame rowsAsDf = new ColumnDataFrame(null, sourceColumnsIndex, rows.one)
                .cols(sourceColumnsIndex).merge(mapper);

        int w = sourceColumnsIndex.size();
        Series[] resultColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            resultColumns[i] = merger.merge(sourceColumns[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(resultColumns);
    }

    @Override
    public DataFrame sort(Sorter... sorters) {

        if (sorters.length == 0) {
            return source;
        }

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() < 2) {
            return source;
        }

        DataFrame rsDf = select();
        IntComparator comparator = Comparators.of(rsDf, sorters);
        DataFrame rowsAsDf = rsDf.rows(DataFrameSorter.sort(comparator, rsDf.height())).select();

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
        return unique(sourceColumnsIndex.toArray());
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
        return new ColumnDataFrame(null, sourceColumnsIndex, doSelect().one);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, sourceColumnsIndex.replace(oldToNewNames), doSelect().one);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return new ColumnDataFrame(null, sourceColumnsIndex.replace(renamer), doSelect().one);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return new ColumnDataFrame(null, Index.of(newColumnNames), doSelect().one);
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
        // TODO: duplicating "selectByColumn" logic, except the mapper transform happens on top of row set
        //   DataFrame instead of column by column ... Unify?
        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        // this will do expansion within the RowSet scope if needed
        Tuple2<Series<?>[], ColumnExpander> rows = doSelect();
        return new ColumnDataFrame(null, sourceColumnsIndex, rows.one)
                .cols(sourceColumnsIndex).merge(mapper);
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
    public DataFrame selectUnique() {
        return selectUnique(source.getColumnsIndex().toArray());
    }

    @Override
    public DataFrame selectUnique(String... uniqueKeyColumns) {
        return selectUnique(sourceColumnsIndex.positions(uniqueKeyColumns));
    }

    @Override
    public DataFrame selectUnique(int... uniqueKeyColumns) {
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
            return rowsAsDf;
        }

        return rowsAsDf.rows(uniqueIndex).select();
    }

    protected DataFrame mergeByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        // this will do expansion within the RowSet scope if needed
        Tuple2<Series<?>[], ColumnExpander> rows = doSelect();

        RowSetMerger merger = expansionColumn >= 0 ? merger().expandCols(rows.two) : merger();
        int w = sourceColumnsIndex.size();

        Series[] resultColumns = new Series[w];
        DataFrame rowsAsDf = new ColumnDataFrame(null, sourceColumnsIndex, rows.one);
        for (int i = 0; i < w; i++) {
            resultColumns[i] = merger.merge(sourceColumns[i], columnMaker.apply(i, rowsAsDf));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(resultColumns);
    }

    protected DataFrame selectByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        // this will do expansion within the RowSet scope if needed
        Tuple2<Series<?>[], ColumnExpander> rows = doSelect();
        DataFrame rowsAsDf = new ColumnDataFrame(null, sourceColumnsIndex, rows.one);

        int w = sourceColumnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = columnMaker.apply(i, rowsAsDf);
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    protected abstract int size();

    protected Tuple2<Series<?>[], ColumnExpander> doSelect() {
        int w = sourceColumns.length;
        Series<?>[] to = new Series[w];

        for (int i = 0; i < w; i++) {
            to[i] = doSelect(sourceColumns[i]);
        }

        if (expansionColumn >= 0) {
            ColumnExpander expander = ColumnExpander.expand(to[expansionColumn]);
            int[] stretchIndex = expander.getStretchIndex();

            for (int i = 0; i < w; i++) {
                to[i] = i == expansionColumn
                        ? expander.getExpanded()
                        : to[i].select(stretchIndex);
            }

            return new Tuple2<>(to, expander);
        }

        return new Tuple2<>(to, null);
    }

    protected abstract <T> Series<T> doSelect(Series<T> sourceColumn);

    protected abstract RowSetMerger merger();
}
