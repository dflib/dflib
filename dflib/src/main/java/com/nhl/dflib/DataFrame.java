package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.BooleanValueMapper;
import com.nhl.dflib.map.DoubleValueMapper;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.IntValueMapper;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ArraySeries;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An immutable 2D data container with support for a variety of data transformations, queries, joins, etc. Every such
 * transformation returns a new DataFrame object and does not affect the original DataFrame. DataFrame allows to iterate
 * over its contents via {@link RowProxy} instances. You should not attempt to store {@link RowProxy} instances or
 * otherwise rely on their state outside a single iteration.
 */
public interface DataFrame extends Iterable<RowProxy> {

    /**
     * Creates a DataFrame from a column index and an array of {@link Series} representing columns.
     *
     * @since 0.6
     */
    static DataFrame forColumns(Index columnIndex, Series<?>... dataColumns) {
        return new ColumnDataFrame(columnIndex, dataColumns);
    }

    /**
     * Creates a DataFrame by folding the provided stream of objects into rows and columns row by row.
     */
    static <T> DataFrame forStreamFoldByRow(Index columns, Stream<T> stream) {
        return DataFrameFactory.forStreamFoldByRow(columns, stream);
    }

    /**
     * Creates a DataFrame by folding the provided array of objects into rows and columns row by row.
     */
    static DataFrame forSequenceFoldByRow(Index columns, Object... sequence) {
        return DataFrameFactory.forSequenceFoldByRow(columns, sequence);
    }

    static DataFrame forSequenceFoldByColumn(Index columns, Object... sequence) {
        return DataFrameFactory.forSequenceFoldByColumn(columns, sequence);
    }

    static DataFrame forRows(Index columns, Object[]... rows) {
        return DataFrameFactory.forRows(columns, rows);
    }

    static DataFrame forRows(Index columns, Iterable<Object[]> source) {
        return DataFrameFactory.forRows(columns, source);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    static <T> DataFrame forObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return DataFrameFactory.forObjects(columns, rows, rowMapper);
    }

    /**
     * Syntactic sugar for DataFrame row (aka simple Object[]) construction. Equivalent to "new Object[] {x, y, z}".
     *
     * @param values a varargs array of values
     * @return "values" vararg unchanged
     */
    static Object[] row(Object... values) {
        return values;
    }

    /**
     * Returns DataFrame column index.
     *
     * @return DataFrame column index
     */
    Index getColumnsIndex();

    <T> Series<T> getColumn(String name) throws IllegalArgumentException;

    <T> Series<T> getColumn(int pos) throws IllegalArgumentException;

    /**
     * Returns a named DataFrame column as IntSeries. If the column is not in the DataFrame or is not an
     * {@link IntSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as IntSeries.
     * @since 0.6
     */
    IntSeries getColumnAsInt(String name) throws IllegalArgumentException;

    /**
     * Returns a DataFrame column at the specified position as IntSeries. If the column is not in the DataFrame or is
     * not an {@link IntSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as IntSeries.
     * @since 0.6
     */
    IntSeries getColumnAsInt(int pos) throws IllegalArgumentException;

    /**
     * Returns a named DataFrame column as DoubleSeries. If the column is not in the DataFrame or is not an
     * {@link DoubleSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as DoubleSeries.
     * @since 0.6
     */
    DoubleSeries getColumnAsDouble(String name) throws IllegalArgumentException;

    /**
     * Returns a DataFrame column at the specified position as DoubleSeries. If the column is not in the DataFrame or is
     * not an {@link DoubleSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as DoubleSeries.
     * @since 0.6
     */
    DoubleSeries getColumnAsDouble(int pos) throws IllegalArgumentException;

    /**
     * Returns a named DataFrame column as BooleanSeries. If the column is not in the DataFrame or is not an
     * {@link BooleanSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as BooleanSeries.
     * @since 0.6
     */
    BooleanSeries getColumnAsBoolean(String name) throws IllegalArgumentException;

    /**
     * Returns a DataFrame column at the specified position as BooleanSeries. If the column is not in the DataFrame or is
     * not an {@link BooleanSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as BooleanSeries.
     * @since 0.6
     */
    BooleanSeries getColumnAsBoolean(int pos) throws IllegalArgumentException;

    /**
     * Returns the number of rows in this DataFrame. Aka the DataFrame "height". Note that depending on the type of
     * the DataFrame this operation may or may not be constant speed. In the worst case it would require a full scan
     * through all rows.
     *
     * @return an int indicating the number of rows in the DataFrame
     */
    int height();

    default int width() {
        return getColumnsIndex().size();
    }

    DataFrame head(int len);

    DataFrame tail(int len);

    /**
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/filter functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    DataFrame materialize();

    /**
     * Produces a new DataFrame from this DataFrame, applying {@link RowMapper} to each row of this DataFrame. The
     * result DataFrame will be the same height as this, but can have a different with and set of columns.
     *
     * @param mappedColumns column index of the new DataFrame
     * @param rowMapper     a function applied to each row of this DataFrame
     * @return a new DataFrame
     */
    DataFrame map(Index mappedColumns, RowMapper rowMapper);

    /**
     * Creates a new DataFrame which is the exact copy of this DataFrame, only with a single column values transformed
     * using the provided converter function.
     *
     * @param columnLabel a column to convert
     * @param converter   a function to apply to column values
     * @param <V>         expected input column value
     * @param <VR>        output column value
     * @return a new DataFrame
     */
    default <V, VR> DataFrame convertColumn(String columnLabel, ValueMapper<V, VR> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return convertColumn(pos, converter);
    }

    <V, VR> DataFrame convertColumn(int pos, ValueMapper<V, VR> converter);

    /**
     * Performs column conversion to a compact IntC
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to ints
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toIntColumn(String columnLabel, IntValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return toIntColumn(pos, converter);
    }

    /**
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    <V> DataFrame toIntColumn(int pos, IntValueMapper<V> converter);

    /**
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toIntColumn(String columnLabel, int forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return toIntColumn(pos, forNull);
    }

    /**
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @param <V>     expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toIntColumn(int pos, int forNull) {
        return toIntColumn(pos, IntValueMapper.fromObject(forNull));
    }

    /**
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to doubles
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toDoubleColumn(String columnLabel, DoubleValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return toDoubleColumn(pos, converter);
    }

    /**
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    <V> DataFrame toDoubleColumn(int pos, DoubleValueMapper<V> converter);

    /**
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toDoubleColumn(String columnLabel, double forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return toDoubleColumn(pos, forNull);
    }

    /**
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @param <V>     expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toDoubleColumn(int pos, double forNull) {
        return toDoubleColumn(pos, DoubleValueMapper.fromObject(forNull));
    }

    /**
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to booleans
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toBooleanColumn(String columnLabel, BooleanValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return toBooleanColumn(pos, converter);
    }

    /**
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to booleans
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    <V> DataFrame toBooleanColumn(int pos, BooleanValueMapper<V> converter);

    /**
     * @param columnLabel name of a column to convert
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toBooleanColumn(String columnLabel) {
        int pos = getColumnsIndex().position(columnLabel);
        return toBooleanColumn(pos);
    }

    /**
     * @param pos position of a column to convert
     * @param <V> expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toBooleanColumn(int pos) {
        return toBooleanColumn(pos, BooleanValueMapper.fromObject());
    }

    /**
     * Adds row number column to the DataFrame
     *
     * @param columnName the name of the row number column
     * @return a new DataFrame with an extra row number column
     */
    DataFrame addRowNumber(String columnName);

    default <V> DataFrame addColumn(String columnLabel, RowToValueMapper<V> columnValueProducer) {
        return addColumns(new String[]{columnLabel}, columnValueProducer);
    }

    <V> DataFrame addColumns(String[] columnLabels, RowToValueMapper<V>... columnValueProducers);

    <V> DataFrame addColumn(String columnLabel, Series<V> column);

    DataFrame renameColumns(String... columnLabels);

    default DataFrame renameColumn(String oldLabel, String newLabel) {
        return renameColumns(Collections.singletonMap(oldLabel, newLabel));
    }

    DataFrame renameColumns(Map<String, String> oldToNewLabels);

    DataFrame selectColumns(String label0, String... otherLabels);

    DataFrame selectColumns(int pos0, int... otherPositions);

    DataFrame dropColumns(String... columnLabels);

    /**
     * Selects DataFrame rows based on provided row index. This allows to reorder, filter, duplicate rows of this
     * DataFrame.
     *
     * @return a new DataFrame that matches the selection criteria
     * @since 0.6
     */
    DataFrame selectRows(int... rowPositions);

    /**
     * Selects DataFrame rows based on provided row index. This allows to reorder, filter, duplicate rows of this
     * DataFrame.
     *
     * @param rowPositions
     * @return a new DataFrame that matches the selection criteria
     * @since 0.6
     */
    DataFrame selectRows(IntSeries rowPositions);

    /**
     * Selects DataFrame rows based on provided row index. This allows to reorder, filter, duplicate rows of this
     * DataFrame.
     *
     * @return a new DataFrame that matches the selection criteria
     * @deprecated since 0.6 in favor of primitive int indexing, e.g. {@link #selectRows(int...)}.
     */
    default DataFrame select(List<Integer> rowPositions) {
        int len = rowPositions.size();
        IntMutableList ml = new IntMutableList(len);

        for (Integer i : rowPositions) {
            ml.add(i);
        }

        return selectRows(ml.toIntSeries());
    }

    /**
     * @param rowPositions
     * @return
     * @deprecated since 0.6 in favor of {@link #selectRows(IntSeries)}.
     */
    @Deprecated
    default DataFrame select(Series<Integer> rowPositions) {
        int len = rowPositions.size();
        IntMutableList ml = new IntMutableList(len);

        for (int i = 0; i < len; i++) {
            ml.add(rowPositions.get(i));
        }

        return selectRows(ml.toIntSeries());
    }

    DataFrame filter(RowPredicate p);

    default <V> DataFrame filter(String columnName, ValuePredicate<V> p) {
        int pos = getColumnsIndex().position(columnName);
        return filter(pos, p);
    }

    <V> DataFrame filter(int columnPos, ValuePredicate<V> p);

    <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor);

    DataFrame sort(String column, boolean ascending);

    DataFrame sort(int column, boolean ascending);

    DataFrame sort(String[] columns, boolean[] ascending);

    DataFrame sort(int[] columns, boolean[] ascending);

    /**
     * Horizontally concatenates a DataFrame with another DataFrame, producing a "wider" DataFrame. If the heights of
     * the DataFrames are not the same, the behavior is governed by the "how" parameter. Rows on the left or right sides
     * can be either truncated or padded. If two DataFrames have have conflicting columns, an underscore suffix ("_")
     * is added to the column names coming from the right-hand side DataFrame.
     *
     * @param df another DataFrame.
     * @return a new "wider" DataFrame that is a combination of columns from this DataFrame and a DataFrame argument.
     */
    default DataFrame hConcat(DataFrame df) {
        return hConcat(JoinType.inner, df);
    }

    /**
     * Returns a DataFrame that combines columns from this and another DataFrame. If two DataFrames have have
     * conflicting columns, an underscore suffix ("_") is added to the column names coming from the right-hand side
     * DataFrame. If the heights of the DataFrames are not the same, the behavior is governed by the "how" parameter.
     * Rows on the left or right sides can be either truncated or padded.
     *
     * @param df  another DataFrame
     * @param how semantics of concat operation
     * @return a new "wider" DataFrame that is a combination of columns from this DataFrame and a DataFrame argument
     */
    DataFrame hConcat(JoinType how, DataFrame df);

    DataFrame hConcat(Index zippedColumns, JoinType how, DataFrame df, RowCombiner c);

    /**
     * A form of {@link #vConcat(JoinType, DataFrame...)} with "left" join semantics. I.e. all columns
     * of this DataFrame will be preserved and extended with data from matching columns of other DataFrames.
     *
     * @param dfs DataFrames to concatenate with this one
     * @return a new "taller" DataFrame
     */
    default DataFrame vConcat(DataFrame... dfs) {
        return vConcat(JoinType.left, dfs);
    }

    /**
     * Vertically concatenates columns of this DataFrame with those of one or more other DataFrames, producing a "taller"
     * DataFrame. Column data is aligned by name. Which columns are included in the final result is determined by the
     * "how" parameter.
     *
     * @param how determines which columns are included in concatenated result
     * @param dfs DataFrames to concatenate with this one
     * @return a new "taller" DataFrame
     */
    DataFrame vConcat(JoinType how, DataFrame... dfs);

    /**
     * Joins this DataFrame with another DataFrame based on a row predicate and specified join semantics. Uses
     * <a href="https://en.wikipedia.org/wiki/Nested_loop_join">"nested loop join"</a> algorithm, which is rather slow,
     * exhibiting O(N*M) performance. Most of the time you should consider using faster "hash" joins instead, e.g.
     * {@link #innerJoin(DataFrame, Hasher, Hasher)}. Use this type of join only when a predicate can not be reduced
     * to a simple equality of hashes.
     *
     * @param df a DataFrame to join with this one
     * @param p  join condition of a pair of rows
     * @return a DataFrame that is a result of this join
     */
    default DataFrame innerJoin(DataFrame df, JoinPredicate p) {
        return join(df, p, JoinType.inner);
    }

    /**
     * Joins this DataFrame with another DataFrame based on a row predicate and specified join semantics. Uses
     * <a href="https://en.wikipedia.org/wiki/Nested_loop_join">"nested loop join"</a> algorithm, which is rather slow,
     * exhibiting O(N*M) performance. Most of the time you should consider using faster "hash" joins instead, e.g.
     * {@link #join(DataFrame, Hasher, Hasher, JoinType)}. Use this type of join only when a predicate can not be reduced
     * to a simple equality of hashes.
     *
     * @param df  a DataFrame to join with this one
     * @param p   join condition of a pair of rows
     * @param how join semantics (inner, left, right, full)
     * @return a DataFrame that is a result of this join
     */
    DataFrame join(DataFrame df, JoinPredicate p, JoinType how);

    /**
     * Calculates a join using <a href="https://en.wikipedia.org/wiki/Hash_join">"hash join"</a> algorithm. It requires
     * two custom "hash" functions for the rows on the left and the right sides of the join, each producing
     * values, whose equality can be used as a join condition. This is the fastest known way to join two data sets.
     * See the {@link Hasher} class for common hashers, such as those comparing column values.
     *
     * @param df          a DataFrame to join with this one
     * @param leftHasher  a hash function for the left-side rows
     * @param rightHasher a hash function for the right-side rows
     * @return a DataFrame that is a result of this join
     */
    default DataFrame innerJoin(DataFrame df, Hasher leftHasher, Hasher rightHasher) {
        return join(df, leftHasher, rightHasher, JoinType.inner);
    }

    /**
     * @param df          the DataFrame to join with this one
     * @param leftColumn  the name of the join column for left-side DataFrame
     * @param rightColumn the name of the join column for right-side DataFrame
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame innerJoin(DataFrame df, String leftColumn, String rightColumn) {
        return join(df, leftColumn, rightColumn, JoinType.inner);
    }

    /**
     * @param df     the DataFrame to join with this one
     * @param column the name of the join column for both left and right side DataFrames
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame innerJoin(DataFrame df, String column) {
        return join(df, column, JoinType.inner);
    }

    /**
     * @param df          the DataFrame to join with this one
     * @param leftColumn  the position of the join column for left-side DataFrame
     * @param rightColumn the position of the join column for right-side DataFrame
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame innerJoin(DataFrame df, int leftColumn, int rightColumn) {
        return join(df, leftColumn, rightColumn, JoinType.inner);
    }

    /**
     * Calculates a join using <a href="https://en.wikipedia.org/wiki/Hash_join">hash join</a> algorithm. It requires
     * two custom "hash" functions for the rows on the left and the right sides of the join, each producing
     * values, whose equality can be used as a join condition. This is the fastest known way to join two data sets.
     * See the {@link Hasher} class for common hashers, such as those comparing column values.
     *
     * @param df          a DataFrame to join with this one
     * @param leftHasher  a hash function for the left-side rows
     * @param rightHasher a hash function for the right-side rows
     * @param how         join semantics
     * @return a DataFrame that is a result of this join
     */
    DataFrame join(DataFrame df, Hasher leftHasher, Hasher rightHasher, JoinType how);

    /**
     * @param df          the DataFrame to join with this one
     * @param leftColumn  the name of the join column for left-side DataFrame
     * @param rightColumn the name of the join column for right-side DataFrame
     * @param how         join semantics
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame join(DataFrame df, String leftColumn, String rightColumn, JoinType how) {
        return join(df, Hasher.forColumn(leftColumn), Hasher.forColumn(rightColumn), how);
    }

    /**
     * @param df     the DataFrame to join with this one
     * @param column the name of the join column for both left and right side DataFrames
     * @param how    join semantics
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame join(DataFrame df, String column, JoinType how) {
        Hasher hasher = Hasher.forColumn(column);
        return join(df, hasher, hasher, how);
    }

    /**
     * @param df          the DataFrame to join with this one
     * @param leftColumn  the position of the join column for left-side DataFrame
     * @param rightColumn the position of the join column for right-side DataFrame
     * @param how         join semantics
     * @return a DataFrame that is a result of this join
     * @since 0.6
     */
    default DataFrame join(DataFrame df, int leftColumn, int rightColumn, JoinType how) {
        return join(df, Hasher.forColumn(leftColumn), Hasher.forColumn(rightColumn), how);
    }

    /**
     * Aggregates DataFrame contents into an Object[] of values, using provided aggregator.
     *
     * @param aggregator an aggregator function
     * @return an Object[] with aggregated results
     */
    default Series<?> agg(Aggregator aggregator) {
        return new ArraySeries<>(aggregator.aggregate(this));
    }

    /**
     * Aggregates DataFrame columns into an Object[] using provided per-column aggregators. Note that aggregator
     * positions correspond to resulting array positions and do not necessarily match column positions in the DataFrame.
     *
     * @param aggregators an array of aggregators corresponding to the aggregated result columns
     * @return an Object[] with aggregated results
     */
    default Series<?> agg(ColumnAggregator... aggregators) {
        return new ArraySeries<>(Aggregator.forColumns(aggregators).aggregate(this));
    }

    /**
     * An operation similar to SQL "GROUP BY" that partitions this DataFrame into a number of groups based on the values
     * of the specified columns.
     *
     * @param column0 the first column to use in grouping.
     * @param columns additional columns to group by
     * @return a new GroupBy instance that contains row groupings
     */
    default GroupBy group(String column0, String... columns) {

        Hasher mapper = Hasher.forColumn(column0);
        for (int i = 0; i < columns.length; i++) {
            mapper = mapper.and(columns[i]);
        }

        return group(mapper);
    }

    /**
     * An operation similar to SQL "GROUP BY" that partitions this DataFrame into a number of groups based on the values
     * of the specified columns.
     *
     * @param column0 the first column to use in grouping.
     * @param columns additional columns to group by
     * @return a new GroupBy instance that contains row groupings
     */
    default GroupBy group(int column0, int... columns) {

        Hasher mapper = Hasher.forColumn(column0);
        for (int i = 0; i < columns.length; i++) {
            mapper = mapper.and(columns[i]);
        }

        return group(mapper);
    }


    /**
     * An operation similar to SQL "GROUP BY" that partitions this DataFrame into a number of groups using the specified
     * row hash function.
     *
     * @param by a hash function to calculate group values
     * @return a new GroupBy instance that contains row groupings
     */
    GroupBy group(Hasher by);

    DataFrame fillNulls(Object value);

    DataFrame fillNulls(int columnPos, Object value);

    default DataFrame fillNulls(String columnName, Object value) {
        return fillNulls(getColumnsIndex().position(columnName), value);
    }

    DataFrame fillNullsBackwards(int columnPos);

    default DataFrame fillNullsBackwards(String columnName) {
        return fillNullsBackwards(getColumnsIndex().position(columnName));
    }

    DataFrame fillNullsForward(int columnPos);

    default DataFrame fillNullsForward(String columnName) {
        return fillNullsForward(getColumnsIndex().position(columnName));
    }

    /**
     * Overlays this DataFrame with a boolean condition DataFrame, returning a new DataFrame that has the same
     * dimensions as this, but with positions matching those of "true" values in the condition replaced with nulls.
     *
     * @param condition a DataFrame that contains only boolean values. It doesn't have to match the shape of this
     *                  DataFrame. Mask operation matches the columns by name and rows by number.
     * @return a new DataFrame of the same shape as this
     * @since 0.6
     */
    DataFrame nullify(DataFrame condition);

    /**
     * The opposite of {@link #nullify(DataFrame)}, doing the replacement of cells not matching the condition. I.e.
     * matching the condition "false" values or those outside the condition shape.
     *
     * @param condition a DataFrame that contains only boolean values. It doesn't have to match the shape of this
     *                  DataFrame. Mask operation matches the columns by name and rows by number.
     * @return a new DataFrame of the same shape as this
     * @since 0.6
     */
    DataFrame nullifyNoMatch(DataFrame condition);

    @Override
    Iterator<RowProxy> iterator();
}
