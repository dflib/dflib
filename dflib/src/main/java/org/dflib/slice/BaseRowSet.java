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
import org.dflib.Udf1;
import org.dflib.series.RowMappedSeries;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class BaseRowSet implements RowSet {

    protected final DataFrame source;
    protected final Exp<?> expansionExp;
    protected final int[] uniqueKeyColumns;
    protected final Sorter[] sorters;

    protected BaseRowSet(
            DataFrame source,
            Exp<?> expansionExp,
            int[] uniqueKeyColumns,
            Sorter[] sorters) {

        this.source = source;
        this.expansionExp = expansionExp;
        this.uniqueKeyColumns = uniqueKeyColumns;
        this.sorters = sorters;
    }

    /**
     * Returns the columns index of the row set after the expansion is applied. It is the same as the source index,
     * unless the expansion expression produces a column that is not present in the source.
     */
    protected Index expandedIndex() {
        Index index = source.getColumnsIndex();
        if (expansionExp == null) {
            return index;
        }

        String name = expansionExp.getColumnName(source);
        return index.contains(name) ? index : index.expand(name);
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(this, DataFrame::cols);
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
    public RowSet unique() {
        // uniqueness is checked against the row set columns, that may include a column added by the expansion
        return unique(expandedIndex().positions(c -> true));
    }

    @Override
    public RowSet unique(String... uniqueKeyColumns) {
        return unique(expandedIndex().positions(uniqueKeyColumns));
    }

    @Override
    public DataFrame select() {
        return runSelect(s -> s);
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        Index index = expandedIndex();
        int w = exps.length;
        if (w != index.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + index.size() + ")");
        }

        return runSelect(s -> s.mapColumns(exps));
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        Index index = expandedIndex();
        int w = mappers.length;
        if (w != index.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + index.size() + ")");
        }

        return runSelect(s -> s.mapColumns((i, rowSet) -> new RowMappedSeries<>(rowSet, mappers[i])));
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        Index index = expandedIndex();
        return runSelect(s -> s.mapDf(rowSet -> rowSet.cols(index).merge(mapper)));
    }

    // executes a standard select sequence with a single customizable step
    private DataFrame runSelect(UnaryOperator<RowSetSelector> columnMapStep) {
        RowSetSelector selector = createSelector()
                .expand(expansionExp);

        return columnMapStep
                .apply(selector)
                .unique(uniqueKeyColumns)
                .sort(sorters)
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

        Index index = expandedIndex();
        int w = exps.length;
        if (w != index.size()) {
            throw new IllegalArgumentException("The number of column expressions (" + w + ") is different from the DataFrame width (" + index.size() + ")");
        }

        return runMerge(m -> m.mapColumns(exps));
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {

        Index index = expandedIndex();
        int w = mappers.length;
        if (w != index.size()) {
            throw new IllegalArgumentException("The number of column mappers (" + w + ") is different from the DataFrame width (" + index.size() + ")");
        }

        return runMerge(m -> m.mapColumns((i, rowSet) -> new RowMappedSeries<>(rowSet, mappers[i])));
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        Index index = expandedIndex();
        return runMerge(m -> m.mapDf(df -> df.cols(index).merge(mapper)));
    }

    @Override
    public DataFrame mergeAll(Udf1<?, ?> udf) {
        Index index = expandedIndex();
        int w = index.size();
        Exp[] exps = new Exp[w];
        for (int i = 0; i < w; i++) {
            exps[i] = udf.call(index.get(i));
        }

        return merge(exps);
    }

    // executes a standard merge sequence with a single customizable step
    private DataFrame runMerge(UnaryOperator<RowSetMerger> columnMapStep) {

        RowSetMerger merger = createMerger()
                .expand(expansionExp);

        return columnMapStep
                .apply(merger)
                .unique(uniqueKeyColumns)
                .sort(sorters)
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
