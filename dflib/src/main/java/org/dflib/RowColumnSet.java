package org.dflib;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A column set within a {@link RowSet}.
 *
 * @since 1.0.0-M19
 */
public interface RowColumnSet {

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);

    DataFrame select();

    DataFrame select(Exp<?>... exps);

    DataFrame select(RowMapper mapper);

    DataFrame select(RowToValueMapper<?>... mappers);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided operator.
     */
    DataFrame selectRename(UnaryOperator<String> renamer);

    /**
     * A form of {@link #select()} that also renames the result columns.
     */
    DataFrame selectRename(String... newColumnNames);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided old to new names map.
     */
    DataFrame selectRename(Map<String, String> oldToNewNames);
}
