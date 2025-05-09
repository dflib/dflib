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

import java.util.Map;
import java.util.function.UnaryOperator;

public class EmptyRowSet extends BaseRowSet {

    public EmptyRowSet(DataFrame source) {
        super(source, -1, null);
    }

    @Override
    protected RowSetMerger createMerger() {
        return RowSetMerger.ofNone(source);
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
        Index index = source.getColumnsIndex();
        for (int p : uniqueKeyColumns) {
            index.get(p);
        }

        return this;
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
    public BooleanSeries locate() {
        return new FalseSeries(source.height());
    }

    @Override
    public IntSeries index() {
        return Series.ofInt();
    }
}
