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
        super(source, sourceColumns, -1);
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
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
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
    public DataFrame unique() {
        return source;
    }

    @Override
    public DataFrame unique(String... uniqueKeyColumns) {
        return source;
    }

    @Override
    public DataFrame unique(int... uniqueKeyColumns) {
        return source;
    }

    @Override
    public DataFrame select() {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return DataFrame.empty(source.getColumnsIndex().replace(oldToNewNames));
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return DataFrame.empty(source.getColumnsIndex().replace(renamer));
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return DataFrame.empty(Index.of(newColumnNames));
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame selectUnique(String... uniqueKeyColumns) {
        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().positions(uniqueKeyColumns);
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame selectUnique(int... uniqueKeyColumns) {
        // validate the argument, even though the operation does nothing
        for (int p : uniqueKeyColumns) {
            source.getColumnsIndex().get(p);
        }

        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame selectUnique() {
        return selectUnique(source.getColumnsIndex().toArray());
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
