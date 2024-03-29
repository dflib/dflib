package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowSet;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.series.FalseSeries;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public class EmptyRowSet implements RowSet {

    private final DataFrame source;

    public EmptyRowSet(DataFrame source) {
        this.source = source;
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.cols(),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.cols(columns),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.cols(columnsIndex),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.cols(columns),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.cols(condition),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.colsExcept(columns),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(
                source,
                this,
                df -> df.colsExcept(columns),
                () -> EmptyRowSetMerger.instance);
    }

    @Override
    public DataFrame drop() {
        return source;
    }

    @Override
    public DataFrame expand(String columnName) {

        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().position(columnName);

        return source;
    }

    @Override
    public DataFrame expand(int columnPos) {

        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().get(columnPos);

        return source;
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        return source;
    }

    @Override
    public DataFrame map(RowMapper mapper) {
        return source;
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
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
        return DataFrame.empty(source.getColumnsIndex().rename(renamer));
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
    public DataFrame selectExpand(int columnPos) {

        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().get(columnPos);

        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame selectExpand(String columnName) {
        // validate the argument, even though the operation does nothing
        source.getColumnsIndex().position(columnName);

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
