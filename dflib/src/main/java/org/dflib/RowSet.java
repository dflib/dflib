package org.dflib;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Encapsulates a subset of rows within a DataFrame. The rows can be then transformed in some way and either extracted
 * into a separate DataFrame (see the various "select" methods) or merged to the original DataFrame. Depending on a
 * transformation type, the number of rows can increase or decrease (or remain the same). Also, RowSet can produce
 * another type of DataFrame subset - {@link RowColumnSet} - which defines specific rows and columns and its own
 * transformations.
 *
 * @since 1.0.0-M19
 */
public interface RowSet {

    RowColumnSet cols(Index columnsIndex);

    RowColumnSet cols(String... columns);

    RowColumnSet cols(int... columns);

    RowColumnSet cols(Predicate<String> condition);

    RowColumnSet colsExcept(String... columns);

    RowColumnSet colsExcept(int... columns);


    DataFrame explode(String columnName);

    DataFrame explode(int columnPos);

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);

    DataFrame sort(Sorter... sorters);

    DataFrame unique();

    DataFrame unique(String... uniqueKeyColumns);

    DataFrame unique(int... uniqueKeyColumns);

    /**
     * Returns a new DataFrame with the RowSet rows only and without any transformation. If the RowSet contains rows
     * not present in the source, they are appended in the bottom the result DataFrame.
     */
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
