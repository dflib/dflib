package org.dflib;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Encapsulates a subset of columns within a DataFrame. The columns can be then transformed in some way and either
 * extracted into a separate DataFrame (see the various "select" methods) or merged to the original DataFrame. A
 * transformation can increase or decrease the number of columns (or leave it unchanged), and may result in a change
 * of values in those columns. A "merge" scenario either replaces the existing columns by name and/or produces new
 * columns.
 */
public interface ColumnSet {

    RowColumnSet rows();

    RowColumnSet rows(Condition condition);

    default RowColumnSet rows(int... positions) {
        return rows(Series.ofInt(positions));
    }

    RowColumnSet rows(IntSeries positions);

    RowColumnSet rows(BooleanSeries condition);

    RowColumnSet rows(RowPredicate condition);

    RowColumnSet rowsRange(int fromInclusive, int toExclusive);

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
     * produced by "compacting" the internal representation of the set columns to {@link BooleanSeries}. For this to
     * work, each column in the ColumnSet must contain Booleans or values that can be converted to a String and parsed
     * as booleans.
     */
    DataFrame compactBool();

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link BooleanSeries} using the provided
     * mapper.
     */
    <V> DataFrame compactBool(BoolValueMapper<V> mapper);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link IntSeries}. For this to
     * work, each column in the ColumnSet must contain Numbers or values that can be converted to a String and parsed
     * as integers.
     */
    DataFrame compactInt(int forNull);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link IntSeries} using the provided
     * mapper.
     */
    <V> DataFrame compactInt(IntValueMapper<V> mapper);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link LongSeries}. For this to
     * work, each column in the ColumnSet must contain Numbers or values that can be converted to a String and parsed
     * as longs.
     */
    DataFrame compactLong(long forNull);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link LongSeries} using the provided
     * mapper.
     */
    <V> DataFrame compactLong(LongValueMapper<V> mapper);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link FloatSeries}. For this to
     * work, each column in the ColumnSet must contain Numbers or values that can be converted to a String and parsed
     * as doubles.
     *
     * @since 2.0.0
     */
    DataFrame compactFloat(float forNull);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link FloatSeries} using the provided
     * mapper.
     *
     * @since 2.0.0
     */
    <V> DataFrame compactFloat(FloatValueMapper<V> mapper);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link DoubleSeries}. For this to
     * work, each column in the ColumnSet must contain Numbers or values that can be converted to a String and parsed
     * as doubles.
     */
    DataFrame compactDouble(double forNull);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by "compacting" the internal representation of the set columns to {@link DoubleSeries} using the provided
     * mapper.
     */
    <V> DataFrame compactDouble(DoubleValueMapper<V> mapper);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by the specified expressions. Expressions are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame merge(Exp<?>... exps);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * based on the specified Series. Series are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame merge(Series<?>... columns);

    /**
     * Returns a transformed DataFrame that contains columns from this DataFrame and added / replaced columns
     * produced by the specified mappers. Mappers are matched with the result columns using the algorithm
     * defined in this specific ColumnSet.
     */
    DataFrame merge(RowToValueMapper<?>... mappers);

    /**
     * Returns a DataFrame that contains columns from the source DataFrame merged with the result of the RowMapper
     * transformation of this ColumnSet. Column names in the RowMapper's RowBuilder will be the same as column names
     * from this ColumnSet.
     */
    DataFrame merge(RowMapper mapper);

    /**
     * A flavor of "merge" that returns a DataFrame with merged columns produced from the expression that maps each row
     * to an Iterable of values, such as a List or a Set. Each element in each Iterable goes in its own column. For a
     * ColumnSet with a defined width, if the splitExp does not generate enough values to fill a given row, the tail of
     * the row will be filled with nulls, and if it generates values in excess of the row width, extra values are ignored.
     * In a dynamic ColumnSet, the total number of produced columns will be equal to the longest row generated by the splitExp.
     */
    DataFrame expand(Exp<? extends Iterable<?>> splitExp);

    /**
     * A flavor of "merge" that returns a DataFrame with merged columns produced from the expression that maps each row to an array
     * of values. Each element in each array goes in its own column. For a ColumnSet with a defined width, if the splitExp
     * does not generate enough values to fill a given row, the tail of the row will be filled with nulls, and if it
     * generates values in excess of the row width, extra values are ignored. In a dynamic ColumnSet, the total number of
     * produced columns will be equal to the longest row generated by the splitExp.
     */
    DataFrame expandArray(Exp<? extends Object[]> splitExp);

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed by applying the renaming function.
     */
    DataFrame as(UnaryOperator<String> renamer);

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed to the specified names. The new
     * names array argument should match the length of the ColumnSet.
     */
    DataFrame as(String... newColumnNames);

    /**
     * Returns a new DataFrame with all the columns from the source DataFrame (and, possibly, some extra all-null columns
     * defined in the ColumnSet), with the columns from the ColumnSet renamed using old to new names map argument.
     */
    DataFrame as(Map<String, String> oldToNewNames);


    /**
     * Returns a new DataFrame based on the specified columns from the source DataFrame used without transformation.
     * If the column set contains a column not present in the source, a column with null values will be returned. If
     * column set is dynamic (has no explicit columns defined), the source DataFrame is returned unchanged.
     */
    DataFrame select();

    DataFrame selectAs(UnaryOperator<String> renamer);

    DataFrame selectAs(String... newColumnNames);

    DataFrame selectAs(Map<String, String> oldToNewNames);


    /**
     * Returns a DataFrame that contains columns produced by the specified expressions. Expressions are matched with
     * the result columns using the algorithm defined in this specific ColumnSet.
     */
    DataFrame select(Exp<?>... exps);

    /**
     * Returns a DataFrame that contains columns produced by the specified mappers. Mappers are matched with
     * the result columns using the algorithm defined in this specific ColumnSet.
     */
    DataFrame select(RowToValueMapper<?>... mappers);

    /**
     * Returns a DataFrame that contains columns produced by RowMapper transformation of this ColumnSet.
     * Column names in the RowMapper's RowBuilder will be the same as column names from this ColumnSet.
     */
    DataFrame select(RowMapper mapper);

    /**
     * Returns a DataFrame with columns produced from the expression that resolves each row to an iterable
     * of values. For a ColumnSet with defined width, if the splitExp does not generate enough values for a given
     * row, the rest will be filled with nulls, and if it generates extra values, they will be ignored. For a dynamic
     * ColumnSet, the total number of produced columns will be equaled to the longest row generated by the splitExp.
     */
    DataFrame selectExpand(Exp<? extends Iterable<?>> splitExp);

    /**
     * Returns a DataFrame with columns produced from the expression that resolves each row to an array of values.
     * For a ColumnSet with defined width, if the splitExp does not generate enough values for a given row, the rest
     * will be filled with nulls, and if it generates extra values, they will be ignored. For a dynamic ColumnSet,
     * the total number of produced columns will be equaled to the longest row generated by the splitExp.
     */
    DataFrame selectExpandArray(Exp<? extends Object[]> splitExp);

    /**
     * Returns a single-row DataFrame with columns from this ColumnSet that are produced by the specified aggregating
     * expressions.
     */
    DataFrame agg(Exp<?>... aggregators);
}
