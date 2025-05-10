package org.dflib.slice;

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
import org.dflib.series.RowMappedSeries;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final int expansionColumn;
    protected final int[] uniqueKeyColumns;

    protected BaseRowSet(DataFrame source, int expansionColumn, int[] uniqueKeyColumns) {
        this.source = source;
        this.expansionColumn = expansionColumn;
        this.uniqueKeyColumns = uniqueKeyColumns;
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(this, df -> df.cols());
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(this, df -> df.cols(columns));
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(this, df -> df.cols(columnsIndex));
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(this, df -> df.cols(columns));
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(this, df -> df.cols(condition));
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(this, df -> df.colsExcept(columns));
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(this, df -> df.colsExcept(columns));
    }

    @Override
    public RowSet expand(String columnName) {
        return expand(source.getColumnsIndex().position(columnName));
    }


    @Override
    public DataFrame sort(Sorter... sorters) {

        if (sorters.length == 0) {
            return source;
        }

        return createMerger()
                .expand(expansionColumn)
                .unique(uniqueKeyColumns)
                .sort(sorters)
                .merge();
    }

    @Override
    public RowSet unique() {
        return unique(source.getColumnsIndex().positions(c -> true));
    }

    @Override
    public RowSet unique(String... uniqueKeyColumns) {
        return unique(source.getColumnsIndex().positions(uniqueKeyColumns));
    }

    @Override
    public DataFrame select() {
        return runSelect(s -> s);
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return runSelect(s -> s.mapColumns((i, rowSet) -> exps[i].eval(rowSet)));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return runSelect(s -> s.mapColumns((i, rowSet) -> new RowMappedSeries<>(rowSet, mappers[i])));
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return runSelect(s -> s.mapDf(rowSet -> rowSet.cols(source.getColumnsIndex()).merge(mapper)));
    }

    // executes a standard select sequence with a single customizable step
    private DataFrame runSelect(UnaryOperator<RowSetSelector> columnMapStep) {
        RowSetSelector selector = createSelector()
                .expand(expansionColumn);

        return columnMapStep
                .apply(selector)
                .unique(uniqueKeyColumns)
                .select();
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return select().cols().selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return select().cols().selectAs(renamer);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return select().cols().selectAs(newColumnNames);
    }

    @Override
    public DataFrame merge() {
        return runMerge(m -> m);
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {

        int w = exps.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return runMerge(m -> m.mapColumns((i, rowSet) -> exps[i].eval(rowSet)));
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {

        int w = mappers.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return runMerge(m -> m.mapColumns((i, rowSet) -> new RowMappedSeries<>(rowSet, mappers[i])));
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        return runMerge(m -> m.mapDf(df -> df.cols(source.getColumnsIndex()).merge(mapper)));
    }

    // executes a standard merge sequence with a single customizable step
    private DataFrame runMerge(UnaryOperator<RowSetMerger> columnMapStep) {

        RowSetMerger merger = createMerger()
                .expand(expansionColumn);

        return columnMapStep
                .apply(merger)
                .unique(uniqueKeyColumns)
                .merge();
    }

    protected abstract RowSetMerger createMerger();

    protected RowSetSelector createSelector() {
        return new RowSetSelector(selectRows());
    }

    protected DataFrame selectRows() {
        int w = source.width();
        Series<?>[] to = new Series[w];

        for (int i = 0; i < w; i++) {
            to[i] = selectCol(source.getColumn(i));
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), to);
    }

    protected abstract <T> Series<T> selectCol(Series<T> sourceColumn);
}
