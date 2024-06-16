package org.dflib;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.dflib.Exp.$col;

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

    RowColumnSet cols();

    RowColumnSet cols(Index columnsIndex);

    RowColumnSet cols(String... columns);

    RowColumnSet cols(int... columns);

    RowColumnSet cols(Predicate<String> condition);

    RowColumnSet colsExcept(String... columns);

    RowColumnSet colsExcept(int... columns);

    /**
     * Returns the original DataFrame with the RowSet rows removed.
     */
    DataFrame drop();

    /**
     * A flavor of "merge" that for the specified column, expands its Iterable or array objects, creating new rows for
     * each collection element. All other columns in the newly produced rows will be populated with values of the source
     * rows. The new rows are added at the bottom of the returned DataFrame.
     */
    DataFrame expand(String columnName);

    /**
     * A flavor of "merge" that for the specified column, expands its Iterable or array objects, creating new rows for
     * each collection element. All other columns in the newly produced rows will be populated with values of the
     * source rows. The new rows are added at the bottom of the returned DataFrame.
     */
    DataFrame expand(int columnPos);

    /**
     * @since 1.0.0-M22
     */
    DataFrame merge(Exp<?>... exps);

    /**
     * @since 1.0.0-M22
     */
    DataFrame merge(RowMapper mapper);

    /**
     * @since 1.0.0-M22
     */
    DataFrame merge(RowToValueMapper<?>... mappers);

    /**
     * @deprecated in favor of {@link #merge(Exp[])}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    default DataFrame map(Exp<?>... exps) {
        return merge(exps);
    }

    /**
     * @deprecated in favor of {@link #merge(RowMapper)}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    default DataFrame map(RowMapper mapper) {
        return merge(mapper);
    }

    /**
     * @deprecated in favor of {@link #merge(RowToValueMapper[])}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    default DataFrame map(RowToValueMapper<?>... mappers) {
        return merge(mappers);
    }

    /**
     * Sorts the RowSet, applying provided sorters and returns a DataFrame with the sorted rows from the RowSet merged
     * into the original DataFrame.
     */
    DataFrame sort(Sorter... sorters);

    /**
     * Sorts the RowSet based on the specified column and returns a DataFrame with the sorted rows from the RowSet merged
     * into the original DataFrame.
     *
     * @since 1.0.0-M21
     */
    default DataFrame sort(int sortCol, boolean ascending) {
        return sort(new int[]{sortCol}, new boolean[]{ascending});
    }

    /**
     * Sorts the RowSet based on the specified column and returns a DataFrame with the sorted rows from the RowSet merged
     * into the original DataFrame.
     *
     * @since 1.0.0-M21
     */
    default DataFrame sort(String sortCol, boolean ascending) {
        return sort(new String[]{sortCol}, new boolean[]{ascending});
    }

    /**
     * @since 1.0.0-M21
     */
    default DataFrame sort(int[] sortCols, boolean[] ascending) {
        int len = sortCols.length;
        Sorter[] sorters = new Sorter[len];
        for (int i = 0; i < len; i++) {
            sorters[i] = ascending[i] ? $col(sortCols[i]).asc() : $col(sortCols[i]).desc();
        }

        return sort(sorters);
    }

    /**
     * @since 1.0.0-M21
     */
    default DataFrame sort(String[] sortCols, boolean[] ascending) {
        int len = sortCols.length;
        Sorter[] sorters = new Sorter[len];
        for (int i = 0; i < len; i++) {
            sorters[i] = ascending[i] ? $col(sortCols[i]).asc() : $col(sortCols[i]).desc();
        }

        return sort(sorters);
    }

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
     * A form of {@link #select()} that renames the result columns using the provided operator.
     */
    DataFrame selectAs(UnaryOperator<String> renamer);

    /**
     * A form of {@link #select()} that renames the result columns.
     */
    DataFrame selectAs(String... newColumnNames);

    /**
     * A form of {@link #select()} that renames the result columns using the provided old to new names map.
     */
    DataFrame selectAs(Map<String, String> oldToNewNames);

    /**
     * Returns a DataFrame with RowSet rows "expanded" based on a column with array or Iterable objects. New rows are
     * created for each collection element in the specified "expansion" column. All other columns are populated with
     * values of the "unexpanded" rows.
     */
    DataFrame selectExpand(String columnName);

    /**
     * Returns a DataFrame with RowSet rows "expanded" based on a column with array or Iterable objects. New rows are
     * created for each collection element in the specified "expansion" column. All other columns are populated with
     * values of the "unexpanded" rows.
     */
    DataFrame selectExpand(int columnPos);

    DataFrame selectUnique();

    DataFrame selectUnique(String... uniqueKeyColumns);

    DataFrame selectUnique(int... uniqueKeyColumns);

    /**
     * Returns a BooleanSeries indicating whether each source DataFrame position is included in the RowSet. Can be
     * utilized as a reusable "selector" of RowSets from other DataFrames.
     */
    BooleanSeries locate();

    /**
     * Returns a IntSeries of the source DataFrame positions thta re included in the RowSet. Can be
     * utilized as a reusable "selector" of RowSets from other DataFrames.
     */
    IntSeries index();
}
