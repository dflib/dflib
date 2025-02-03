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
 * A {@link RowColumnSet} over the entire DataFrame with no filters, expansions or other row changes.
 *
 * @deprecated unused
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class AllRowColumnSet implements RowColumnSet {

    private final ColumnSet columnSet;

    public AllRowColumnSet(ColumnSet columnSet) {
        this.columnSet = columnSet;
    }

    @Override
    public DataFrame merge() {
        return columnSet.merge();
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        return columnSet.merge(exps);
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        return columnSet.merge(mapper);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        return columnSet.merge(mappers);
    }

    @Override
    public DataFrame drop() {
        return DataFrame.empty(columnSet.drop().getColumnsIndex());
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
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return columnSet.selectAs(renamer);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return columnSet.selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return columnSet.selectAs(newColumnNames);
    }
}
