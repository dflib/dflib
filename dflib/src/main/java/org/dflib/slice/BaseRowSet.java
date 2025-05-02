package org.dflib.slice;

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
import org.dflib.series.RowMappedSeries;
import org.dflib.sort.IntComparator;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final Index sourceColumnsIndex;
    protected final Series[] sourceColumns;
    protected final int expansionColumn;
    protected final int[] uniqueKeyColumns;

    protected BaseRowSet(DataFrame source, Series<?>[] sourceColumns, int expansionColumn, int[] uniqueKeyColumns) {
        this.source = source;
        this.sourceColumnsIndex = source.getColumnsIndex();
        this.sourceColumns = sourceColumns;
        this.expansionColumn = expansionColumn;
        this.uniqueKeyColumns = uniqueKeyColumns;
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

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        return mergeProcessor(merger())
                .mapper(df -> df.cols(sourceColumnsIndex).merge(mapper))
                .merge(source, false);
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
        IntComparator comparator = IntComparator.of(rsDf, sorters);
        DataFrame rowsAsDf = rsDf.rows(comparator.sortIndex(rsDf.height())).select();

        RowSetMerger merger = merger();

        int w = sourceColumnsIndex.size();
        Series[] to = new Series[w];
        for (int i = 0; i < w; i++) {
            to[i] = merger.merge(sourceColumns[i], rowsAsDf.getColumn(i));
        }

        return DataFrame.byColumn(sourceColumnsIndex).of(to);
    }

    @Override
    public RowSet unique() {
        return unique(sourceColumnsIndex.positions(c -> true));
    }

    @Override
    public RowSet unique(String... uniqueKeyColumns) {
        return unique(sourceColumnsIndex.positions(uniqueKeyColumns));
    }

    @Override
    public DataFrame select() {
        return selectProcessor().select(sourceColumnsIndex);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return selectProcessor().select(sourceColumnsIndex.replace(oldToNewNames));
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return selectProcessor().select(sourceColumnsIndex.replace(renamer));
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return selectProcessor().select(Index.of(newColumnNames));
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

        return selectProcessor().mapper(df -> df.cols(sourceColumnsIndex).merge(mapper)).select(sourceColumnsIndex);
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        if (w != sourceColumnsIndex.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + sourceColumnsIndex.size() + ")");
        }

        return selectByColumn((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]));
    }

    private DataFrame mergeByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        return mergeProcessor(merger()).colMapper(columnMaker).merge(source, false);
    }

    private DataFrame selectByColumn(IntObjectFunction2<DataFrame, Series<?>> columnMaker) {

        if (sourceColumns.length == 0) {
            return source;
        }

        if (sourceColumns[0].size() == 0) {
            return source;
        }

        return selectProcessor().colMapper(columnMaker).select(sourceColumnsIndex);
    }

    protected abstract int size();

    protected RowSetMergeProcessor mergeProcessor(RowSetMerger merger) {
        return RowSetMergeProcessor.of(selectRows(), merger)
                .expansion(expansionColumn)

                // TODO: should we apply "unique" before or after expansion depending
                //  whether it was called before or after "expand(..)"? If we go this way, I suppose we should also allow
                //  any sequence of expansions / unique calls and respect their order. Seems confusing?

                .unique(sourceColumnsIndex, uniqueKeyColumns);
    }

    // TODO: We are rebuilding all columns to match the RowSet condition and applying expansion to the result. This
    //  was done to simplify downstream "merge(..)" algorithm, but it will result in poor merge performance as we are
    //  rebuilding columns twice. To improve it, we should skip selection all together, let merge work purely based on
    //  indices. Note that uniquing is already done correctly; we don't apply it here, and return an index instead.
    protected RowSetSelectProcessor selectProcessor() {
        return RowSetSelectProcessor.of(selectRows())
                .expansion(expansionColumn)

                // TODO: should we apply "unique" before or after expansion depending
                //  whether it was called before or after "expand(..)"? If we go this way, I suppose we should also allow
                //  any sequence of expansions / unique calls and respect their order. Seems confusing?

                .unique(sourceColumnsIndex, uniqueKeyColumns);
    }

    private Series[] selectRows() {
        int w = sourceColumns.length;
        Series<?>[] to = new Series[w];

        for (int i = 0; i < w; i++) {
            to[i] = selectCol(sourceColumns[i]);
        }

        return to;
    }

    protected abstract <T> Series<T> selectCol(Series<T> sourceColumn);

    protected abstract RowSetMerger merger();
}
