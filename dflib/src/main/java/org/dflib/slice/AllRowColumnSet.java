package org.dflib.slice;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public class AllRowColumnSet implements RowColumnSet {

    private final ColumnSet columnSet;

    public AllRowColumnSet(ColumnSet columnSet) {
        this.columnSet = columnSet;
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        return columnSet.map(exps);
    }

    @Override
    public DataFrame map(RowMapper mapper) {
        return columnSet.map(mapper);
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
        return columnSet.map(mappers);
    }

    @Override
    public DataFrame select() {
        return columnSet.select();
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return columnSet.select(exps);
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return columnSet.select(mapper);
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        return columnSet.select(mappers);
    }

    @Override
    public DataFrame selectRename(UnaryOperator<String> renamer) {
        return columnSet.selectAs(renamer);
    }

    @Override
    public DataFrame selectRename(Map<String, String> oldToNewNames) {
        return columnSet.selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectRename(String... newColumnNames) {
        return columnSet.selectAs(newColumnNames);
    }
}
