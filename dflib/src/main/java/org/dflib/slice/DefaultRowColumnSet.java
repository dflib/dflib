package org.dflib.slice;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DefaultRowColumnSet implements RowColumnSet {

    private final DataFrame source;
    private final BaseRowSet rowSet;
    private final Function<DataFrame, ColumnSet> columnSetMaker;
    private final Supplier<RowSetMerger> rowSetMergerMaker;

    public DefaultRowColumnSet(
            DataFrame source,
            BaseRowSet rowSet,
            Function<DataFrame, ColumnSet> columnSetMaker,
            Supplier<RowSetMerger> rowSetMergerMaker) {
        this.source = source;
        this.rowSet = rowSet;
        this.columnSetMaker = columnSetMaker;
        this.rowSetMergerMaker = rowSetMergerMaker;
    }

    @Override
    public DataFrame merge() {
        return rowSet
                .mergeProcessor(rowSetMergerMaker.get())
                .mapper(df -> columnSetMaker.apply(df).merge())
                .merge(source, true);
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return rowSet
                .mergeProcessor(rowSetMergerMaker.get())
                .mapper(df -> columnSetMaker.apply(df).merge(exps))
                .merge(source, true);
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        return rowSet
                .mergeProcessor(rowSetMergerMaker.get())
                .mapper(df -> columnSetMaker.apply(df).merge(mapper))
                .merge(source, true);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        return rowSet
                .mergeProcessor(rowSetMergerMaker.get())
                .mapper(df -> columnSetMaker.apply(df).merge(mappers))
                .merge(source, true);
    }

    @Override
    public DataFrame drop() {
        return columnSetMaker.apply(rowSet.drop()).drop();
    }

    @Override
    public DataFrame select() {
        return columnSetMaker.apply(rowSet.select()).select();
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return columnSetMaker.apply(rowSet.select()).select(exps);
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return columnSetMaker.apply(rowSet.select()).select(mapper);
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        return columnSetMaker.apply(rowSet.select()).select(mappers);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return columnSetMaker.apply(rowSet.select()).selectAs(renamer);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(newColumnNames);
    }
}
