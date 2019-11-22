package com.nhl.dflib;

import com.nhl.dflib.join.JoinBuilder;
import com.nhl.dflib.row.RowProxy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An immutable 2D data container with support for a variety of data transformations, queries, joins, etc. Every such
 * transformation returns a new DataFrame object and does not affect the original DataFrame. DataFrame allows to iterate
 * over its contents via {@link RowProxy} instances. You should not attempt to store {@link RowProxy} instances or
 * otherwise rely on their state outside a single iteration.
 */
public interface DataFrame extends Iterable<RowProxy> {

    /**
     * Creates a DataFrame builder with provided column labels. Callers can then pass in-memory data in various forms
     * to the returned builder to create a DataFrame.
     *
     * @since 0.6
     */
    static DataFrameBuilder newFrame(String... columnLabels) {
        return DataFrameBuilder.builder(columnLabels);
    }

    /**
     * Creates a DataFrame builder with provided column index. Callers can then pass in-memory data in various forms
     * to the returned builder to create a DataFrame.
     *
     * @since 0.6
     */
    static DataFrameBuilder newFrame(Index columnIndex) {
        return DataFrameBuilder.builder(columnIndex);
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
     * Returns a named DataFrame column as BooleanSeries. If the column is not in the DataFrame or is not an
     * {@link LongSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as LongSeries.
     * @since 0.6
     */
    LongSeries getColumnAsLong(String name) throws IllegalArgumentException;

    /**
     * Returns a DataFrame column at the specified position as LongSeries. If the column is not in the DataFrame or is
     * not an {@link LongSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as LongSeries.
     * @since 0.6
     */
    LongSeries getColumnAsLong(int pos) throws IllegalArgumentException;

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
     * @param resultColumns column index of the new DataFrame
     * @param rowMapper     a function applied to each row of this DataFrame
     * @return a new DataFrame
     */
    DataFrame map(Index resultColumns, RowMapper rowMapper);

    /**
     * Creates a new Series with values mapped by applying row mapper function to the DataFrame. The returned Series
     * size is the same this DataFrame height.
     *
     * @param rowMapper a function applied to each row of this DataFrame
     * @param <T>
     * @return a new Series.
     * @since 0.6
     */
    <T> Series<T> mapColumn(RowToValueMapper<T> rowMapper);

    /**
     * Creates a new BooleanSeries with values mapped by applying row mapper function to the DataFrame. The returned
     * series size is the same this DataFrame height.
     *
     * @param rowMapper a boolean function applied to each row of this DataFrame
     * @return a new BooleanSeries.
     * @since 0.6
     */
    BooleanSeries mapColumnAsBoolean(RowToBooleanValueMapper rowMapper);

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
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toIntColumn(String columnLabel, int forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return toIntColumn(pos, forNull);
    }

    /**
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toIntColumn(int pos, int forNull) {
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
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toDoubleColumn(String columnLabel, double forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return toDoubleColumn(pos, forNull);
    }

    /**
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toDoubleColumn(int pos, double forNull) {
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
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toBooleanColumn(String columnLabel) {
        int pos = getColumnsIndex().position(columnLabel);
        return toBooleanColumn(pos);
    }

    /**
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toBooleanColumn(int pos) {
        return toBooleanColumn(pos, BooleanValueMapper.fromObject());
    }

    /**
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to longs
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    default <V> DataFrame toLongColumn(String columnLabel, LongValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return toLongColumn(pos, converter);
    }

    /**
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to longs
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.6
     */
    <V> DataFrame toLongColumn(int pos, LongValueMapper<V> converter);

    /**
     * @param columnLabel name of a column to convert
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toLongColumn(String columnLabel, long forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return toLongColumn(pos, forNull);
    }

    /**
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame toLongColumn(int pos, long forNull) {
        return toLongColumn(pos, LongValueMapper.fromObject(forNull));
    }


    /**
     * @param columnLabel name of a column to convert
     * @param <E>         converted column enum type
     * @return a new DataFrame
     * @since 0.6
     */
    default <E extends Enum<E>> DataFrame toEnumFromStringColumn(String columnLabel, Class<E> enumType) {
        int pos = getColumnsIndex().position(columnLabel);
        return toEnumFromStringColumn(pos, enumType);
    }

    /**
     * @param pos position of a column to convert
     * @param <E> converted column enum type
     * @return a new DataFrame
     * @since 0.6
     */
    default <E extends Enum<E>> DataFrame toEnumFromStringColumn(int pos, Class<E> enumType) {
        return convertColumn(pos, ValueMapper.stringToEnum(enumType));
    }

    /**
     * @param columnLabel name of a column to convert
     * @param <E>         converted column enum type
     * @return a new DataFrame
     * @since 0.6
     */
    default <E extends Enum<E>> DataFrame toEnumFromNumColumn(String columnLabel, Class<E> enumType) {
        int pos = getColumnsIndex().position(columnLabel);
        return toEnumFromNumColumn(pos, enumType);
    }

    /**
     * @param pos position of a column to convert
     * @param <E> converted column enum type
     * @return a new DataFrame
     * @since 0.6
     */
    default <E extends Enum<E>> DataFrame toEnumFromNumColumn(int pos, Class<E> enumType) {
        return convertColumn(pos, ValueMapper.numToEnum(enumType));
    }

    /**
     * Adds a column with the specified name to the DataFrame that contains incrementing numbers, starting with zero.
     *
     * @param columnName the name of the row number column
     * @return a new DataFrame with an extra row number column
     */
    default DataFrame addRowNumber(String columnName) {
        return addRowNumber(columnName, 0);
    }

    /**
     * Adds a column with the specified name to the DataFrame that contains incrementing numbers, starting with the
     * specified value.
     *
     * @param columnName the name of the row number column
     * @param startValue the value to start counting from when assigning row numbers
     * @return a new DataFrame with an extra row number column
     * @since 0.7
     */
    DataFrame addRowNumber(String columnName, int startValue);

    default DataFrame addColumn(String columnLabel, RowToValueMapper<?> columnValueProducer) {
        return addColumn(columnLabel, mapColumn(columnValueProducer));
    }

    DataFrame addColumns(String[] columnLabels, RowToValueMapper<?>... columnValueProducers);

    /**
     * Add one more columns to the DataFrame.
     *
     * @param columnLabels the names of the added columns
     * @param rowMapper    a mapper with the "read" part based on this DataFrame index, and "write" part matching the
     *                     newly added columns
     * @return a new DataFrame with extra columns added
     * @since 0.7
     */
    <V> DataFrame addColumns(String[] columnLabels, RowMapper rowMapper);

    <V> DataFrame addColumn(String columnLabel, Series<V> column);

    DataFrame renameColumns(String... columnLabels);

    default DataFrame renameColumn(String oldLabel, String newLabel) {
        return renameColumns(Collections.singletonMap(oldLabel, newLabel));
    }

    /**
     * Renames column index labels by applying the provided function to each label. Useful for name conversions like
     * lowercasing, etc.
     *
     * @param renameFunction a function that is passed each label in turn
     * @return a new DataFrame with renamed columns
     * @since 0.6
     */
    DataFrame renameColumns(UnaryOperator<String> renameFunction);

    DataFrame renameColumns(Map<String, String> oldToNewLabels);

    // TODO: breaking vararg into arg and vararg is a nasty pattern that does not allow to pass whole data structures
    //  built dynamically.. redo this
    DataFrame selectColumns(String label0, String... otherLabels);

    // TODO: breaking vararg into arg and vararg is a nasty pattern that does not allow to pass whole data structures
    //  built dynamically.. redo this
    DataFrame selectColumns(int pos0, int... otherPositions);

    /**
     * @param columnsIndex an index that defines a subset of columns and their ordering in the returned DataFrame.
     * @return a new DataFrame.
     * @since 0.6
     */
    DataFrame selectColumns(Index columnsIndex);

    /**
     * Returns a DataFrame with only columns from this DataFrame whose labels match the specified condition.
     *
     * @param labelCondition a condition evaluated against column labels to determine whether they should be included
     *                       in the resulting DataFrame
     * @return a new DataFrame
     * @since 0.7
     */
    DataFrame selectColumns(Predicate<String> labelCondition);

    DataFrame dropColumns(String... columnLabels);

    /**
     * Returns a DataFrame with columns from this DataFrame, except those columns whose labels match the specified
     * condition.
     *
     * @param labelCondition a condition evaluated against column labels to determine whether they should be removed
     *                       from the resulting DataFrame
     * @return a new DataFrame
     * @since 0.7
     */
    DataFrame dropColumns(Predicate<String> labelCondition);

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

    DataFrame filterRows(RowPredicate p);

    default <V> DataFrame filterRows(String columnName, ValuePredicate<V> p) {
        int pos = getColumnsIndex().position(columnName);
        return filterRows(pos, p);
    }

    <V> DataFrame filterRows(int columnPos, ValuePredicate<V> p);

    /**
     * Returns a DataFrame with subset of rows matching condition.
     *
     * @param condition a {@link BooleanSeries} whose "true" values indicate which
     * @return
     * @since 0.6
     */
    DataFrame filterRows(BooleanSeries condition);

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
     * @return an inner join "hash" join builder
     * @since 0.6
     */
    default JoinBuilder innerJoin() {
        return new JoinBuilder(this).type(JoinType.inner);
    }

    /**
     * @return a left join "hash" join builder
     * @since 0.6
     */
    default JoinBuilder leftJoin() {
        return new JoinBuilder(this).type(JoinType.left);
    }

    /**
     * @return a right join "hash" join builder
     * @since 0.6
     */
    default JoinBuilder rightJoin() {
        return new JoinBuilder(this).type(JoinType.right);
    }

    /**
     * @return a full join "hash" join builder
     * @since 0.6
     */
    default JoinBuilder fullJoin() {
        return new JoinBuilder(this).type(JoinType.full);
    }

    /**
     * Aggregates DataFrame columns into a Series, using provided per-column aggregators. Note that aggregator
     * positions correspond to returned Series value indexes and do not generally match column positions in this
     * DataFrame.
     *
     * @param aggregators an array of aggregators corresponding to the aggregated result columns
     * @return an {@link Series} with aggregated results
     * @see Aggregator for static factory methods of column aggregators
     */
    Series<?> agg(Aggregator<?>... aggregators);

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

    /**
     * Fills nulls in the specified column with values taken from a Series at a given index.
     *
     * @param columnPos a name of the column whose nulls need to be filled
     * @param values    a Series to take null replacement values from
     * @return a new DataFrame
     * @since 0.6
     */
    DataFrame fillNullsFromSeries(int columnPos, Series<?> values);

    /**
     * Fills nulls in the specified column with values taken from a Series at a given index.
     *
     * @param columnName a name of the column whose nulls need to be filled.
     * @param values     a Series to take null replacement values from
     * @return a new DataFrame
     * @since 0.6
     */
    default DataFrame fillNullsFromSeries(String columnName, Series<?> values) {
        return fillNullsFromSeries(getColumnsIndex().position(columnName), values);
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

    /**
     * @param another a DataFrame to compare with.
     * @return a DataFrame with true/false values corresponding to the result of comparision of this DataFrame with
     * another.
     * @since 0.6
     */
    DataFrame eq(DataFrame another);

    /**
     * @param another a DataFrame to compare with.
     * @return a DataFrame with true/false values corresponding to the result of comparision of this DataFrame with
     * another.
     * @since 0.6
     */
    DataFrame ne(DataFrame another);

    /**
     * Returns a new DataFrame with 3 columns "row", "column", "value", that contains values from all columns of
     * this DataFrame. Null values are not included.
     *
     * @return a new DataFrame with columns called "row", "column", "value".
     * @since 0.6
     */
    DataFrame stack();

    /**
     * Returns a new DataFrame with 3 columns "row", "column", "value", that contains values from all columns of
     * this DataFrame. Null values are included.
     *
     * @return a new DataFrame with columns called "row", "column", "value".
     * @since 0.6
     */
    DataFrame stackIncludeNulls();

    /**
     * Returns a DataFrame object that is a random sample of rows from this object, with the specified sample size.
     * If you are doing sampling in a high concurrency application, consider using {@link #sampleRows(int, Random)}, as
     * this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the height of this DataFrame.
     * @return a DataFrame object that is a sample of rows from this object
     * @since 0.7
     */
    DataFrame sampleRows(int size);

    /**
     * Returns a DataFrame object that is a random sample of rows from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the height of this DataFrame.
     * @param random a custom random number generator
     * @return a DataFrame object that is a sample of rows from this object
     * @since 0.7
     */
    DataFrame sampleRows(int size, Random random);

    /**
     * Returns a DataFrame object that is a random sample of columns from this object, with the specified sample size.
     * If you are doing sampling in a high concurrency application, consider using {@link #sampleRows(int, Random)}, as
     * this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the height of this DataFrame.
     * @return a DataFrame object that is a sample of columns from this object
     * @since 0.7
     */
    DataFrame sampleColumns(int size);

    /**
     * Returns a DataFrame object that is a random sample of columns from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the height of this DataFrame.
     * @param random a custom random number generator
     * @return a DataFrame object that is a sample of columns from this object
     * @since 0.7
     */
    DataFrame sampleColumns(int size, Random random);

    @Override
    Iterator<RowProxy> iterator();
}
