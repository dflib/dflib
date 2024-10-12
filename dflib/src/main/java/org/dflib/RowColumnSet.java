package org.dflib;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A column set within a {@link RowSet}.
 */
public interface RowColumnSet {

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);

    /**
     * Returns the original DataFrame with rows and columns matched by this RowColumnSet removed.
     */
    DataFrame drop();

    DataFrame select();

    DataFrame select(Exp<?>... exps);

    DataFrame select(RowMapper mapper);

    DataFrame select(RowToValueMapper<?>... mappers);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided operator.
     */
    DataFrame selectAs(UnaryOperator<String> renamer);

    /**
     * A form of {@link #select()} that also renames the result columns.
     */
    DataFrame selectAs(String... newColumnNames);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided old to new names map.
     */
    DataFrame selectAs(Map<String, String> oldToNewNames);
}
