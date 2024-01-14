package org.dflib;

import java.util.Collections;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Defines a set of columns that can be produced from a DataFrame by applying a transformation, also defines a number
 * of such transformation methods. Those methods are of two kinds. Those named "select[Something]" return a DataFrame
 * built from the ColumnSet columns. All the rest produce a DataFrame by merging the ColumnSet into the source DataFrame,
 * replacing or adding columns as needed.
 *
 * @since 1.0.0-M19
 */
public interface ColumnSet {

    /**
     * Returns the original DataFrame with the ColumnSet columns removed.
     */
    DataFrame drop();

    DataFrame fill(Object... values);

    DataFrame fillNulls(Object value);

    DataFrame fillNullsBackwards();

    DataFrame fillNullsForward();

    DataFrame fillNullsFromSeries(Series<?> series);

    DataFrame fillNullsWithExp(Exp<?> replacementValuesExp);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by the specified expressions. Expressions are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame map(Exp<?>... exps);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * based on the specified Series. Series are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame map(Series<?>... columns);

    /**
     * Returns a DataFrame that contains columns produced by the specified expressions. Expressions are matched with
     * the result columns using the algorithm defined in this specific ColumnSet.
     */
    DataFrame select(Exp<?>... exps);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by the specified mappers. Mappers are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame map(RowToValueMapper<?>... mappers);

    /**
     * Returns a DataFrame that contains columns produced by the specified mappers. Mappers are matched with
     * the result columns using the algorithm defined in this specific ColumnSet.
     */
    DataFrame select(RowToValueMapper<?>... mappers);

    /**
     * Returns a DataFrame that contains columns from the source DataFrame merged with the result of the RowMapper
     * transformation of this ColumnSet. Column names in the RowMapper's RowBuilder will be the same as column names
     * from this ColumnSet.
     */
    DataFrame map(RowMapper mapper);

    /**
     * Returns a DataFrame that contains columns produced by RowMapper transformation of this ColumnSet.
     * Column names in the RowMapper's RowBuilder will be the same as column names from this ColumnSet.
     */
    DataFrame select(RowMapper mapper);

    /**
     * Returns a DataFrame with merged columns produced from an expression that maps each row to an Iterable of values,
     * such as a List or a Set. Each element in each Iterable goes in its own column. For a ColumnSet with a defined
     * width, if the mapper does not generate enough values to fill a given row, the tail of the row will be filled
     * with nulls, and if it generates values in excess of the row width, extra values are ignored. In a dynamic
     * ColumnSet, the total number of produced columns will be equal to the longest row generated by the range mapper.
     */
    DataFrame mapIterables(Exp<? extends Iterable<?>> mapper);

    /**
     * Returns a DataFrame with columns produced from the expression that resolves each row to an iterable
     * of values. For a ColumnSet with defined width, if the mapper does not generate enough values for a given
     * row, the rest will be filled with nulls, and if it generates extra values, they will be ignored. For a dynamic
     * ColumnSet, the total number of produced columns will be equaled to the longest row generated by the range mapper.
     */
    DataFrame selectIterables(Exp<? extends Iterable<?>> mapper);

    /**
     * Returns a DataFrame with merged columns produced from an expression that maps each row to an array
     * of values. Each element in each array goes in its own column. For a ColumnSet with a defined width, if the mapper
     * does not generate enough values to fill a given row, the tail of the row will be filled with nulls, and if it
     * generates values in excess of the row width, extra values are ignored. In a dynamic ColumnSet, the total number of
     * produced columns will be equal to the longest row generated by the range mapper.
     */
    DataFrame mapArrays(Exp<? extends Object[]> mapper);

    /**
     * Returns a DataFrame with columns produced from the expression that resolves each row to an array of values.
     * For a ColumnSet with defined width, if the mapper does not generate enough values for a given row, the rest
     * will be filled with nulls, and if it generates extra values, they will be ignored. For a dynamic ColumnSet,
     * the total number of produced columns will be equaled to the longest row generated by the range mapper.
     */
    DataFrame selectArrays(Exp<? extends Object[]> mapper);

    /**
     * Returns a new DataFrame based on the specified columns from the source DataFrame used without transformation.
     * If the column set contains a column not present in the source, a column with null values will be returned. If
     * column set is dynamic (has no explicit columns defined), the source DataFrame is returned unchanged.
     */
    DataFrame select();

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed by applying the renaming function.
     */
    DataFrame rename(UnaryOperator<String> renameFunction);

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed to the specified names. The new
     * names array argument should match the length of the ColumnSet.
     */
    DataFrame rename(String... newColumnNames);

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed using old to new names map argument.
     */
    DataFrame rename(Map<String, String> oldToNewNames);

    default DataFrame renameOne(String oldLabel, String newLabel) {
        return rename(Collections.singletonMap(oldLabel, newLabel));
    }
}
