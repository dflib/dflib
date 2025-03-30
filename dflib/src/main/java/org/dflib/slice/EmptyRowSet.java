package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowMapper;
import org.dflib.RowSet;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.series.FalseSeries;

import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;

public class EmptyRowSet extends BaseRowSet {

    @Deprecated
    private static Series<?>[] emptyCols(int w) {
        Series<?> empty = Series.of();
        Series<?>[] emptyCols = new Series[w];
        Arrays.fill(emptyCols, 0, w, empty);
        return emptyCols;
    }

    /**
     * @deprecated in favor of a 2-arg constructor
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public EmptyRowSet(DataFrame source) {
        this(source, emptyCols(source.width()));
    }

    /**
     * @since 2.0.0
     */
    public EmptyRowSet(
            DataFrame source,
            Series<?>[] sourceColumns) {
        super(source, sourceColumns, -1, null);
    }

    @Override
    protected RowSetMerger merger() {
        return EmptyRowSetMerger.instance;
    }

    @Override
    protected int size() {
        return 0;
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        return Series.of();
    }

    @Override
    public DataFrame drop() {
        return source;
    }

    @Override
    public RowSet expand(int columnPos) {

        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().get(columnPos);

        return this;
    }

    @Override
    public DataFrame merge() {
        return source;
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return source;
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        return source;
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        return source;
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        return source;
    }

    @Override
    public RowSet unique() {
        return this;
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        // validate the argument, even though the operation does nothing
        for (int p : uniqueKeyColumns) {
            sourceColumnsIndex.get(p);
        }

        return this;
    }

    @Override
    public DataFrame select() {
        return DataFrame.empty(sourceColumnsIndex);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return DataFrame.empty(sourceColumnsIndex.replace(oldToNewNames));
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return DataFrame.empty(sourceColumnsIndex.replace(renamer));
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return DataFrame.empty(Index.of(newColumnNames));
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return DataFrame.empty(sourceColumnsIndex);
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return DataFrame.empty(sourceColumnsIndex);
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        return DataFrame.empty(sourceColumnsIndex);
    }

    @Override
    public BooleanSeries locate() {
        return new FalseSeries(source.height());
    }

    @Override
    public IntSeries index() {
        return Series.ofInt();
    }
}
