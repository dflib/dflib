package org.dflib.slice;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;

import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class DefaultRowColumnSet implements RowColumnSet {

    private final BaseRowSet rowSet;
    private final Function<DataFrame, ColumnSet> columnSetMaker;

    public DefaultRowColumnSet(BaseRowSet rowSet, Function<DataFrame, ColumnSet> columnSetMaker) {
        this.rowSet = rowSet;
        this.columnSetMaker = columnSetMaker;
    }

    @Override
    public DataFrame merge() {
        return runMerge(m -> m.mapDf(rowSet -> columnSetMaker.apply(rowSet).merge()));
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return runMerge(m -> m.mapDf(rowSet -> columnSetMaker.apply(rowSet).merge(exps)));
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        return runMerge(m -> m.mapDf(rowSet -> columnSetMaker.apply(rowSet).merge(mapper)));
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        return runMerge(m -> m.mapDf(rowSet -> columnSetMaker.apply(rowSet).merge(mappers)));
    }

    // executes a standard merge sequence with a single customizable step
    private DataFrame runMerge(UnaryOperator<RowSetMerger> columnMapStep) {
        RowSetMerger merger = rowSet
                .createMerger()
                .expand(rowSet.expansionColumn);

        return columnMapStep
                .apply(merger)
                .syncSourceColumnsFromRowSet()
                .unique(rowSet.uniqueKeyColumns)
                .merge();
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
