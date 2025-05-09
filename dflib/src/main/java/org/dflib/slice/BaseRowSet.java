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
        return new DefaultRowColumnSet(source, this, df -> df.cols());
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns));
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columnsIndex));
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns));
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(condition));
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns));
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns));
    }

    @Override
    public RowSet expand(String columnName) {
        return expand(source.getColumnsIndex().position(columnName));
    }

    @Override
    public DataFrame merge() {
        if (source.width() == 0) {
            return source;
        }

        return createAndConfigMerger().mapColumns((i, rowsAsDf) -> rowsAsDf.getColumn(i)).merge();
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {

        int w = exps.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return createAndConfigMerger().mapColumns((i, rowsAsDf) -> exps[i].eval(rowsAsDf)).merge();
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {

        int w = mappers.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return createAndConfigMerger().mapColumns((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i])).merge();
    }

    @Override
    public DataFrame merge(RowMapper mapper) {

        return createAndConfigMerger()
                .mapDf(df -> df.cols(source.getColumnsIndex()).merge(mapper))
                .merge();
    }

    @Override
    public DataFrame sort(Sorter... sorters) {

        if (sorters.length == 0) {
            return source;
        }

        return createAndConfigMerger().sort(sorters).merge();
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
        return createAndConfigSelector().select();
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return createAndConfigSelector().select().cols().selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return createAndConfigSelector().select().cols().selectAs(renamer);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return createAndConfigSelector().select().cols().selectAs(newColumnNames);
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        int w = exps.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return createAndConfigSelector()
                .mapColumns((i, rowsAsDf) -> exps[i].eval(rowsAsDf))
                .select();
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        int w = mappers.length;
        if (w != source.width()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + source.width() + ")");
        }

        return createAndConfigSelector()
                .mapColumns((i, rowsAsDf) -> new RowMappedSeries<>(rowsAsDf, mappers[i]))
                .select();
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return createAndConfigSelector().mapDf(df -> df.cols(source.getColumnsIndex()).merge(mapper)).select();
    }

    protected RowSetMerger createAndConfigMerger() {
        return createMerger()
                .expand(expansionColumn)
                .unique(uniqueKeyColumns);
    }

    protected RowSetSelector createAndConfigSelector() {
        return createSelector()
                .expand(expansionColumn)
                .unique(uniqueKeyColumns);
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
