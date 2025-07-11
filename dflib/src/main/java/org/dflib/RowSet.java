package org.dflib;

import org.dflib.exp.Exps;
import org.dflib.sort.Sorters;

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
 */
public interface RowSet {

    /**
     * For the specified column, expands its Iterable or array objects, creating new rows from the existing one.
     * Expansion will be applied after applying all row set conditions. So all expanded rows will be included in the
     * result. If the RowSet terminates in "merge", the new rows are added at the bottom of the returned DataFrame.
     *
     * @since 2.0.0
     */
    RowSet expand(String columnName);

    /**
     * For the specified column, expands its Iterable or array objects, creating new rows from the existing one.
     * Expansion will be applied after applying all row set conditions. So all expanded rows will be included in the
     * result. If the RowSet terminates in "merge", the new rows are added at the bottom of the returned DataFrame.
     *
     * @since 2.0.0
     */
    RowSet expand(int columnPos);

    /**
     * Configures the row set to filter out repeating rows from the row set. Uniqueness is checked across all columns.
     *
     * @since 2.0.0
     */
    RowSet unique();

    /**
     * Configures the row set to filter out repeating rows from the row set. Uniqueness is checked only for the
     * specified columns.
     *
     * @since 2.0.0
     */
    RowSet unique(String... uniqueKeyColumns);

    /**
     * Configures the row set to filter out repeating rows from the row set. Uniqueness is checked only for the
     * specified columns.
     *
     * @since 2.0.0
     */
    RowSet unique(int... uniqueKeyColumns);

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
     * Returns data from the original DataFrame without transformation. This is often a no-op, returning the original
     * DataFrame, but if the RowSet has expansions, the newly produced rows will be included.
     *
     * @since 2.0.0
     */
    DataFrame merge();

    /**
     * Parses String argument into an array of expressions and then applies row merge operation with those expressions
     * used to transform rowset column data.
     *
     * @since 2.0.0
     */
    default DataFrame merge(String exps) {
        return merge(Exp.parseExpArray(exps));
    }

    DataFrame merge(Exp<?>... exps);

    DataFrame merge(RowMapper mapper);

    DataFrame merge(RowToValueMapper<?>... mappers);


    /**
     * A noop sort operation. Useless on its own, and primarily exists to disambiguate {@link #sort(Sorter...)} and
     * {@link #sort(String...)} for no-arg sort call.
     *
     * @since 2.0.0
     */
    default RowSet sort() {
        return this;
    }

    /**
     * Parses provided Strings into Sorters and calls {@link #sort(Sorter...)}.
     *
     * @since 2.0.0
     */
    default RowSet sort(String... sortExps) {
        return sort(Sorters.asSorters(sortExps));
    }

    /**
     * Configures the RowSet to sort rows, applying provided sorters.
     *
     * @since 2.0.0
     */
    RowSet sort(Sorter... sorters);

    /**
     * Configures the RowSet to sort rows based on the specified column.
     *
     * @since 2.0.0
     */
    default RowSet sort(int sortCol, boolean ascending) {
        return sort(new int[]{sortCol}, new boolean[]{ascending});
    }

    /**
     * Configures the RowSet to sort rows based on the specified column.
     *
     * @since 2.0.0
     */
    default RowSet sort(String sortCol, boolean ascending) {
        return sort(new String[]{sortCol}, new boolean[]{ascending});
    }

    /**
     * Configures the RowSet to sort rows based on the specified columns.
     *
     * @since 2.0.0
     */
    default RowSet sort(int[] sortCols, boolean[] ascending) {
        int len = sortCols.length;
        Sorter[] sorters = new Sorter[len];
        for (int i = 0; i < len; i++) {
            sorters[i] = ascending[i] ? $col(sortCols[i]).asc() : $col(sortCols[i]).desc();
        }

        return sort(sorters);
    }

    /**
     * Configures the RowSet to sort rows based on the specified columns.
     *
     * @since 2.0.0
     */
    default RowSet sort(String[] sortCols, boolean[] ascending) {
        int len = sortCols.length;
        Sorter[] sorters = new Sorter[len];
        for (int i = 0; i < len; i++) {
            sorters[i] = ascending[i] ? $col(sortCols[i]).asc() : $col(sortCols[i]).desc();
        }

        return sort(sorters);
    }

    /**
     * Returns a new DataFrame with the RowSet rows only. No transformation is applied to columns. If the RowSet
     * contains rows not present in the source, they are appended in the bottom the result DataFrame.
     */
    DataFrame select();

    /**
     * Parses String argument into an array of expressions and then applies row select operation with those expressions
     * used to transform rowset column data.
     *
     * @since 2.0.0
     */
    default DataFrame select(String exps) {
        return select(Exp.parseExpArray(exps));
    }

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
     *
     * @deprecated in favor of {@link #expand(String)} followed by {@link #select()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DataFrame selectExpand(String columnName) {
        return expand(columnName).select();
    }

    /**
     * Returns a DataFrame with RowSet rows "expanded" based on a column with array or Iterable objects. New rows are
     * created for each collection element in the specified "expansion" column. All other columns are populated with
     * values of the "unexpanded" rows.
     *
     * @deprecated in favor of {@link #expand(String)} followed by {@link #select()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DataFrame selectExpand(int columnPos) {
        return expand(columnPos).select();
    }

    /**
     * @deprecated in favor of {@link #unique()} and then {@link #select()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DataFrame selectUnique() {
        return unique().select();
    }

    /**
     * @deprecated in favor of {@link #unique(String...)} and then {@link #select()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DataFrame selectUnique(String... uniqueKeyColumns) {
        return unique(uniqueKeyColumns).select();
    }

    /**
     * @deprecated in favor of {@link #unique(int...)} and then {@link #select()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DataFrame selectUnique(int... uniqueKeyColumns) {
        return unique(uniqueKeyColumns).select();
    }

    /**
     * Returns a BooleanSeries indicating whether each source DataFrame position is included in the RowSet. Can be
     * utilized as a reusable "selector" of RowSets from other DataFrames.
     */
    BooleanSeries locate();

    /**
     * Returns a IntSeries of the source DataFrame positions that are included in the RowSet. Can be
     * utilized as a reusable "selector" of RowSets from other DataFrames.
     */
    IntSeries index();
}
