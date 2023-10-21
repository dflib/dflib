package com.nhl.dflib;

import com.nhl.dflib.agg.DataFrameAggregator;
import com.nhl.dflib.builder.DataFrameByColumnBuilder;
import com.nhl.dflib.builder.DataFrameByRowBuilder;
import com.nhl.dflib.builder.DataFrameArrayByRowBuilder;
import com.nhl.dflib.builder.DataFrameFoldByColumnBuilder;
import com.nhl.dflib.builder.DataFrameFoldByRowBuilder;
import com.nhl.dflib.join.JoinBuilder;
import com.nhl.dflib.pivot.PivotBuilder;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.RangeSeries;
import com.nhl.dflib.series.RowMappedSeries;
import com.nhl.dflib.series.SingleValueSeries;
import com.nhl.dflib.window.WindowBuilder;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An immutable 2D data container with support for a variety of data transformations, queries, joins, etc. Every such
 * transformation returns a new DataFrame object and does not affect the original DataFrame.
 */
public interface DataFrame extends Iterable<RowProxy> {

    /**
     * Creates an empty DataFrame
     *
     * @since 0.16
     */
    static DataFrame empty(String... columnNames) {
        return empty(Index.forLabels(columnNames));
    }

    /**
     * Creates an empty DataFrame
     *
     * @since 0.16
     */
    static DataFrame empty(Index columnsIndex) {
        return new ColumnDataFrame(columnsIndex);
    }

    /**
     * Starts a DataFrame builder that will build a DataFrame based on some collection of Series. This is one of the
     * most efficient ways to build DataFrames.
     *
     * @since 0.16
     */
    static DataFrameByColumnBuilder byColumn(String... columnLabels) {
        return byColumn(Index.forLabels(columnLabels));
    }

    /**
     * Starts a DataFrame builder that will build a DataFrame based on some collection of Series. This is one of the
     * most efficient ways to build DataFrames.
     *
     * @since 0.16
     */
    static DataFrameByColumnBuilder byColumn(Index columnIndex) {
        return new DataFrameByColumnBuilder(columnIndex);
    }

    /**
     * Starts a DataFrame builder that will extract data from some collection of objects, each object resulting in
     * a row in a DataFrame.
     *
     * @since 0.16
     */
    @SafeVarargs
    static <T> DataFrameByRowBuilder<T, ?> byRow(Extractor<T, ?>... extractors) {
        return new DataFrameByRowBuilder<>(extractors);
    }

    /**
     * Starts a DataFrame builder that will extract data from some collection of arrays. Each array would result in
     * a row in a DataFrame. This is a flavor of {@link #byRow(Extractor[])} that allows to append arrays using
     * vararg methods.
     *
     * @since 0.16
     */
    @SafeVarargs
    static DataFrameArrayByRowBuilder byArrayRow(Extractor<Object[], ?>... extractors) {
        return new DataFrameArrayByRowBuilder(extractors);
    }

    /**
     * Starts a DataFrame builder that will extract data from a collection of arrays. Each array would produce
     * a row in the resulting DataFrame.
     *
     * @since 0.16
     */
    static DataFrameArrayByRowBuilder byArrayRow(String... columnLabels) {
        return byArrayRow(Index.forLabels(columnLabels));
    }

    /**
     * Starts a DataFrame builder that will extract data from some collection of arrays. Each array would produce
     * a row in the resulting DataFrame.
     *
     * @since 0.16
     */
    static DataFrameArrayByRowBuilder byArrayRow(Index columnIndex) {
        int w = columnIndex.size();
        Extractor<Object[], ?>[] extractors = new Extractor[w];
        for (int i = 0; i < w; i++) {
            int pos = i;
            extractors[i] = Extractor.$col(a -> a[pos]);
        }
        return new DataFrameArrayByRowBuilder(extractors).columnIndex(columnIndex);
    }

    /**
     * @since 0.16
     */
    static DataFrameFoldByRowBuilder foldByRow(String... columnLabels) {
        return foldByRow(Index.forLabels(Objects.requireNonNull(columnLabels)));
    }

    /**
     * @since 0.16
     */
    static DataFrameFoldByRowBuilder foldByRow(Index columnIndex) {
        return new DataFrameFoldByRowBuilder(columnIndex);
    }


    /**
     * @since 0.16
     */
    static DataFrameFoldByColumnBuilder foldByColumn(String... columnLabels) {
        return foldByColumn(Index.forLabels(Objects.requireNonNull(columnLabels)));
    }

    /**
     * @since 0.16
     */
    static DataFrameFoldByColumnBuilder foldByColumn(Index columnIndex) {
        return new DataFrameFoldByColumnBuilder(columnIndex);
    }


    /**
     * Creates a DataFrame builder with provided column labels. Callers can then pass in-memory data in various forms
     * to the returned builder to create a DataFrame.
     *
     * @since 0.6
     * @deprecated use one of {@link #byColumn(String...)}, {@link #byRow(Extractor[])}, {@link #byArrayRow(String...)},
     * {@link #foldByColumn(String...)}, {@link #foldByRow(String...)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static DataFrameBuilder newFrame(String... columnLabels) {
        return new DataFrameBuilder(Index.forLabels(Objects.requireNonNull(columnLabels)));
    }

    /**
     * Creates a DataFrame builder with provided column index. Callers can then pass in-memory data in various forms
     * to the returned builder to create a DataFrame.
     *
     * @since 0.6
     * @deprecated use one of {@link #byColumn(Index)} , {@link #byRow(Extractor[])}, {@link #byArrayRow(String...)},
     * {@link #foldByColumn(Index)}, {@link #foldByRow(Index)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static DataFrameBuilder newFrame(Index columnIndex) {
        return new DataFrameBuilder(columnIndex);
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
     * @deprecated use <code>getColumn(String).castAsInt()</code> instead
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default IntSeries getColumnAsInt(String name) throws IllegalArgumentException {
        try {
            return getColumn(name).castAsInt();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a DataFrame column at the specified position as IntSeries. If the column is not in the DataFrame or is
     * not an {@link IntSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as IntSeries.
     * @since 0.6
     * @deprecated use <code>getColumn(int).castAsInt()</code> instead
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default IntSeries getColumnAsInt(int pos) throws IllegalArgumentException {
        try {
            return getColumn(pos).castAsInt();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a named DataFrame column as DoubleSeries. If the column is not in the DataFrame or is not an
     * {@link DoubleSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as DoubleSeries.
     * @since 0.6
     * @deprecated in favor of <code>getColumn(int).castAsDouble()</code>
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries getColumnAsDouble(String name) throws IllegalArgumentException {
        try {
            return getColumn(name).castAsDouble();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a DataFrame column at the specified position as DoubleSeries. If the column is not in the DataFrame or is
     * not an {@link DoubleSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as DoubleSeries.
     * @since 0.6
     * @deprecated in favor of <code>getColumn(int).castAsDouble()</code>
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries getColumnAsDouble(int pos) throws IllegalArgumentException {
        try {
            return getColumn(pos).castAsDouble();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a named DataFrame column as BooleanSeries. If the column is not in the DataFrame or is not an
     * {@link BooleanSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as BooleanSeries.
     * @since 0.17
     * @deprecated in favor of <code>getColumn(String).castAsBool()</code>
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries getColumnAsBool(String name) throws IllegalArgumentException {
        try {
            return getColumn(name).castAsBool();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @deprecated in favor of <code>getColumn(String).castAsBool()</code>
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries getColumnAsBoolean(String name) throws IllegalArgumentException {
        try {
            return getColumn(name).castAsBool();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a DataFrame column at the specified position as BooleanSeries. If the column is not in the DataFrame or is
     * not an {@link BooleanSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as BooleanSeries.
     * @since 0.17
     * @deprecated in favor of <code>getColumn(int).castAsBool()</code>
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries getColumnAsBool(int pos) throws IllegalArgumentException {
        try {
            return getColumn(pos).castAsBool();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @deprecated in favor of <code>getColumn(int).castAsBool()</code>
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries getColumnAsBoolean(int pos) throws IllegalArgumentException {
        try {
            return getColumn(pos).castAsBool();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a named DataFrame column as BooleanSeries. If the column is not in the DataFrame or is not an
     * {@link LongSeries}, an exception is thrown.
     *
     * @param name column label
     * @return a named DataFrame column as LongSeries.
     * @since 0.6
     * @deprecated use <code>getColumn(String).castAsLong()</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default LongSeries getColumnAsLong(String name) throws IllegalArgumentException {
        try {
            return getColumn(name).castAsLong();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns a DataFrame column at the specified position as LongSeries. If the column is not in the DataFrame or is
     * not an {@link LongSeries}, an exception is thrown.
     *
     * @param pos column position in the DataFrame
     * @return a named DataFrame column as LongSeries.
     * @since 0.6
     * @deprecated use <code>getColumn(int).castAsLong()</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default LongSeries getColumnAsLong(int pos) throws IllegalArgumentException {
        try {
            return getColumn(pos).castAsLong();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

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
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/select functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    DataFrame materialize();

    /**
     * Applies an operation to the entire DataFrame. This is a convenience shortcut that allows to chain multiple
     * transformation methods for a given DataFrame.
     *
     * @since 0.11
     */
    default DataFrame map(UnaryOperator<DataFrame> op) {
        return op.apply(this);
    }

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
     * @return a new Series.
     * @since 0.6
     * @deprecated use <code>addColumn("name", rowMapper).getColumn("name")</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    <T> Series<T> mapColumn(RowToValueMapper<T> rowMapper);

    /**
     * Creates a new BooleanSeries with values mapped by applying row mapper function to the DataFrame. The returned
     * series size is the same this DataFrame height.
     *
     * @param rowMapper a boolean function applied to each row of this DataFrame
     * @return a new BooleanSeries.
     * @since 0.17
     * @deprecated use <code>addColumn("name", rowMapper).getColumn("name").castAsBool()</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    BooleanSeries mapColumnAsBool(RowToBooleanValueMapper rowMapper);

    /**
     * @deprecated use <code>addColumn("name", rowMapper).getColumn("name").castAsBool()</code> instead
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default BooleanSeries mapColumnAsBoolean(RowToBooleanValueMapper rowMapper) {
        return mapColumnAsBool(rowMapper);
    }

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
     * Converts column contents using the expression. Unlike {@link #addColumn(Exp)} ignores the name of the Exp,
     * and uses the "name" argument to identify the column.
     *
     * @since 0.11
     */
    DataFrame convertColumn(String name, Exp<?> exp);

    /**
     * Converts column contents using the expression. Unlike {@link #addColumn(Exp)} ignores the name of the Exp,
     * preserving the existing name at the specified DataFrame position.
     *
     * @since 0.11
     */
    DataFrame convertColumn(int position, Exp<?> exp);

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to ints
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    default <V> DataFrame compactInt(String columnLabel, IntValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactInt(pos, converter);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    <V> DataFrame compactInt(int pos, IntValueMapper<V> converter);

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactInt(String columnLabel, int forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactInt(pos, forNull);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactInt(int pos, int forNull) {
        return compactInt(pos, IntValueMapper.fromObject(forNull));
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactInt(String, IntValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toIntColumn(String columnLabel, IntValueMapper<V> converter) {
        return compactInt(columnLabel, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactInt(int, IntValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toIntColumn(int pos, IntValueMapper<V> converter) {
        return compactInt(pos, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactInt(String, int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toIntColumn(String columnLabel, int forNull) {
        return compactInt(columnLabel, forNull);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactInt(int, int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toIntColumn(int pos, int forNull) {
        return compactInt(pos, forNull);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to doubles
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    default <V> DataFrame compactDouble(String columnLabel, DoubleValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactDouble(pos, converter);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    <V> DataFrame compactDouble(int pos, DoubleValueMapper<V> converter);

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactDouble(String columnLabel, double forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactDouble(pos, forNull);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactDouble(int pos, double forNull) {
        return compactDouble(pos, DoubleValueMapper.fromObject(forNull));
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactDouble(String, DoubleValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toDoubleColumn(String columnLabel, DoubleValueMapper<V> converter) {
        return compactDouble(columnLabel, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactDouble(int, DoubleValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toDoubleColumn(int pos, DoubleValueMapper<V> converter) {
        return compactDouble(pos, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactDouble(String, double)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toDoubleColumn(String columnLabel, double forNull) {
        return compactDouble(columnLabel, forNull);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactDouble(int, double)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toDoubleColumn(int pos, double forNull) {
        return compactDouble(pos, forNull);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to booleans
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    default <V> DataFrame compactBool(String columnLabel, BoolValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactBool(pos, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactBool(String, BoolValueMapper)}
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default <V> DataFrame toBooleanColumn(String columnLabel, BoolValueMapper<V> converter) {
        return compactBool(columnLabel, converter);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to booleans
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    <V> DataFrame compactBool(int pos, BoolValueMapper<V> converter);

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactBool(int, BoolValueMapper)}
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default <V> DataFrame toBooleanColumn(int pos, BoolValueMapper<V> converter) {
        return compactBool(pos, converter);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param columnLabel name of a column to convert
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactBool(String columnLabel) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactBool(pos);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactBool(String)}
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default DataFrame toBooleanColumn(String columnLabel) {
        return compactBool(columnLabel);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactBool(int pos) {
        return compactBool(pos, BoolValueMapper.fromObject());
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactBool(int)}
     */
    @Deprecated(since = "0.17", forRemoval = true)
    default DataFrame toBooleanColumn(int pos) {
        return compactBool(pos);
    }

    /**
     * @since 0.17
     * @deprecated use #convertToBoolColumn(String, BoolValueMapper)
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toBoolColumn(String columnLabel, BoolValueMapper<V> converter) {
        return compactBool(columnLabel, converter);
    }

    /**
     * @since 0.17
     * @deprecated use #convertToBoolColumn(int, BoolValueMapper)
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toBoolColumn(int pos, BoolValueMapper<V> converter) {
        return compactBool(pos, converter);
    }

    /**
     * @since 0.17
     * @deprecated use #convertToBoolColumn(String)
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toBoolColumn(String columnLabel) {
        return compactBool(columnLabel);
    }

    /**
     * @since 0.17
     * @deprecated use #convertToBoolColumn(int)
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toBoolColumn(int pos) {
        return compactBool(pos);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to longs
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    default <V> DataFrame compactLong(String columnLabel, LongValueMapper<V> converter) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactLong(pos, converter);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to longs
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     */
    <V> DataFrame compactLong(int pos, LongValueMapper<V> converter);

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param columnLabel name of a column to convert
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactLong(String columnLabel, long forNull) {
        int pos = getColumnsIndex().position(columnLabel);
        return compactLong(pos, forNull);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.18
     */
    default DataFrame compactLong(int pos, long forNull) {
        return compactLong(pos, LongValueMapper.fromObject(forNull));
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactLong(String, LongValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toLongColumn(String columnLabel, LongValueMapper<V> converter) {
        return compactLong(columnLabel, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactLong(int, LongValueMapper)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <V> DataFrame toLongColumn(int pos, LongValueMapper<V> converter) {
        return compactLong(pos, converter);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactLong(String, long)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toLongColumn(String columnLabel, long forNull) {
        return compactLong(columnLabel, forNull);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #compactLong(int, long)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DataFrame toLongColumn(int pos, long forNull) {
        return compactLong(pos, forNull);
    }

    /**
     * @param columnLabel name of a column to convert
     * @param <E>         converted column enum type
     * @return a new DataFrame
     * @since 0.6
     * @deprecated use <code>convertColumn(name, $str(name).castAsEnum(type))</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <E extends Enum<E>> DataFrame toEnumFromStringColumn(String columnLabel, Class<E> enumType) {
        int pos = getColumnsIndex().position(columnLabel);
        return toEnumFromStringColumn(pos, enumType);
    }

    /**
     * @param pos position of a column to convert
     * @param <E> converted column enum type
     * @return a new DataFrame
     * @since 0.6
     * @deprecated use <code>convertColumn(pos, $str(pos).castAsEnum(type))</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <E extends Enum<E>> DataFrame toEnumFromStringColumn(int pos, Class<E> enumType) {
        return convertColumn(pos, Exp.$col(pos).castAsStr().castAsEnum(enumType));
    }

    /**
     * @param columnLabel name of a column to convert
     * @param <E>         converted column enum type
     * @return a new DataFrame
     * @since 0.6
     * @deprecated use <code>convertColumn(name, $int(name).castAsEnum(type))</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <E extends Enum<E>> DataFrame toEnumFromNumColumn(String columnLabel, Class<E> enumType) {
        return convertColumn(columnLabel, Exp.$col(columnLabel).castAsInt().castAsEnum(enumType));
    }

    /**
     * @param pos position of a column to convert
     * @param <E> converted column enum type
     * @return a new DataFrame
     * @since 0.6
     * @deprecated use <code>convertColumn(pos, $int(pos).castAsEnum(type))</code> instead
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default <E extends Enum<E>> DataFrame toEnumFromNumColumn(int pos, Class<E> enumType) {
        return convertColumn(pos, Exp.$col(pos).castAsInt().castAsEnum(enumType));
    }

    /**
     * Adds a column with the specified name to the DataFrame that contains incrementing numbers, starting with "1".
     *
     * @param columnName the name of the row number column
     * @return a new DataFrame with an extra row number column
     * @since 0.8
     */
    DataFrame addRowNumberColumn(String columnName);

    default DataFrame addColumn(String columnLabel, RowToValueMapper<?> columnMaker) {
        return addColumn(columnLabel, new RowMappedSeries<>(this, columnMaker));
    }

    DataFrame addColumns(String[] columnLabels, RowToValueMapper<?>... columnMakers);

    /**
     * Add one more columns to the DataFrame.
     *
     * @param columnLabels the names of the added columns
     * @param rowMapper    a mapper with the "read" part based on this DataFrame index, and "write" part matching the
     *                     newly added columns
     * @return a new DataFrame with extra columns added
     * @since 0.7
     */
    DataFrame addColumns(String[] columnLabels, RowMapper rowMapper);

    <V> DataFrame addColumn(String columnLabel, Series<V> column);

    /**
     * Adds a column with values derived from this DataFrame by applying a given expression.
     *
     * @since 0.11
     */
    default DataFrame addColumn(Exp<?> exp) {
        return addColumns(exp);
    }

    /**
     * Adds multiple columns with values derived from this DataFrame by applying provided expressions. Column names will
     * be taken from {@link Exp} names.
     *
     * @since 0.11
     */
    DataFrame addColumns(Exp<?>... exps);

    /**
     * Appends a single row in the bottom of the DataFrame. The row is specified as a map of column names to values.
     * Missing values will be represented as nulls, and extra values with no matching DataFrame columns will be ignored.
     * Be aware that this operation can be really slow, as DataFrame is optimized for columnar operations, not row appends.
     * If you are appending more than one row, consider creating a new DataFrame and then concatenating it with this one
     * using {@link #vConcat(DataFrame...)}.
     *
     * @since 0.18
     */
    DataFrame addRow(Map<String, Object> row);

    /**
     * @return a new DataFrame with extra columns added
     * @since 0.8
     */
    default DataFrame addSingleValueColumn(String columnLabel, Object value) {
        return addColumn(columnLabel, new SingleValueSeries<>(value, height()));
    }

    DataFrame renameColumns(String... columnLabels);

    default DataFrame renameColumn(String oldLabel, String newLabel) {
        return renameColumns(Collections.singletonMap(oldLabel, newLabel));
    }

    /**
     * Renames column index labels by applying the provided function to each label. Useful for name conversions like
     * lower-casing, etc.
     *
     * @param renameFunction a function that is passed each label in turn
     * @return a new DataFrame with renamed columns
     * @since 0.6
     */
    DataFrame renameColumns(UnaryOperator<String> renameFunction);

    DataFrame renameColumns(Map<String, String> oldToNewLabels);

    DataFrame selectColumns(String... labels);

    DataFrame selectColumns(int... positions);

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
    default DataFrame selectColumns(Predicate<String> labelCondition) {
        Index columnsIndex = getColumnsIndex();
        Index newIndex = columnsIndex.selectLabels(labelCondition);
        return newIndex != columnsIndex ? selectColumns(newIndex) : this;
    }

    /**
     * @since 0.11
     */
    // TODO: this is different from any of the other "selectColumns":
    //  1. it may generate a DataFrame with a new set of columns not found in this DataFrame
    //  2. It transforms the original columns via expressions
    //  So, maybe rename to "map"?
    default DataFrame selectColumns(Exp<?>... exps) {
        int w = exps.length;
        if (w == 0) {
            throw new IllegalArgumentException("No expressions provided to select columns");
        }

        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = exps[i].getColumnName(this);
        }

        Series[] data = new Series[w];
        data[0] = exps[0].eval(this);

        int h = data[0].size();
        for (int i = 1; i < w; i++) {
            data[i] = exps[i].eval(this);

            // sanity check - all columns must be of the same size
            // TODO: move this check to the DataFrame builder?
            if (data[i].size() != h) {
                throw new IllegalStateException("Unexpected column size for '" + labels[i] + "': " + data[i].size() + ", (expected " + h + ")");
            }
        }

        return DataFrame.byColumn(labels).of(data);
    }

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
     * @return a new DataFrame that matches the selection criteria
     * @since 0.6
     */
    DataFrame selectRows(IntSeries rowPositions);

    /**
     * @since 0.11
     */
    DataFrame selectRows(Condition condition);


    /**
     * @since 0.11
     */
    DataFrame selectRows(RowPredicate p);

    /**
     * @since 0.11
     */
    default <V> DataFrame selectRows(String columnName, ValuePredicate<V> p) {
        int pos = getColumnsIndex().position(columnName);
        return selectRows(pos, p);
    }

    /**
     * @since 0.11
     */
    <V> DataFrame selectRows(int columnPos, ValuePredicate<V> p);

    /**
     * Returns a DataFrame with subset of rows matching condition.
     *
     * @param condition a {@link BooleanSeries} whose "true" values indicate which rows to keep in the result
     * @since 0.11
     */
    DataFrame selectRows(BooleanSeries condition);

    /**
     * Returns a DataFrame that contains a range of rows from this DataFrame.
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @since 0.14
     */
    default DataFrame selectRowRangeOpenClosed(int fromInclusive, int toExclusive) {
        int w = width();
        Series[] rangeColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            // RangeSeries constructor does range checking
            rangeColumns[i] = new RangeSeries(getColumn(i), fromInclusive, toExclusive - fromInclusive);
        }

        return new ColumnDataFrame(getColumnsIndex(), rangeColumns);
    }

    /**
     * Returns a DataFrame with non-repeating rows.
     *
     * @since 0.18
     */
    default DataFrame uniqueRows() {
        return uniqueRows(getColumnsIndex().getLabels());
    }

    /**
     * Returns a DataFrame with non-repeating rows. Uniqueness check is done based on the specified subset of columns,
     * ignoring all others.
     *
     * @since 0.18
     */
    DataFrame uniqueRows(String... columnNamesToCompare);

    /**
     * Returns a DataFrame with non-repeating rows. Uniqueness check is only done based on the specified subset of
     * columns, ignoring all others.
     *
     * @since 0.18
     */
    DataFrame uniqueRows(int... columnNamesToCompare);

    /**
     * @since 0.11
     */
    DataFrame sort(Sorter... sorters);

    DataFrame sort(String column, boolean ascending);

    DataFrame sort(int column, boolean ascending);

    DataFrame sort(String[] columns, boolean[] ascending);

    DataFrame sort(int[] columns, boolean[] ascending);

    /**
     * Horizontally concatenates a DataFrame with another DataFrame, producing a "wider" DataFrame. If the heights of
     * the DataFrames are not the same, the behavior is governed by the "how" parameter. Rows on the left or right sides
     * can be either truncated or padded. If two DataFrames have conflicting columns, an underscore suffix ("_")
     * is added to the column names coming from the right-hand side DataFrame.
     *
     * @param df another DataFrame.
     * @return a new "wider" DataFrame that is a combination of columns from this DataFrame and a DataFrame argument.
     */
    default DataFrame hConcat(DataFrame df) {
        return hConcat(JoinType.inner, df);
    }

    /**
     * Returns a DataFrame that combines columns from this and another DataFrame. If two DataFrames have
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
     * Aggregates DataFrame columns into a single-row DataFrame, using provided per-column aggregators. Note that
     * aggregator positions correspond to returned DataFrame columns and do not generally match column positions
     * in this DataFrame.
     *
     * @param aggregators an array of aggregators corresponding to the aggregated result columns
     * @return a DataFrame with a single row
     * @see Exp for static factory methods of column aggregators
     */
    default DataFrame agg(Exp<?>... aggregators) {
        return DataFrameAggregator.agg(this, aggregators);
    }

    /**
     * An operation similar to SQL "GROUP BY" that partitions this DataFrame into a number of groups based on the values
     * of the specified columns.
     *
     * @param columns columns to group by
     * @return a new GroupBy instance that contains row groupings
     */
    default GroupBy group(String... columns) {

        int w = columns.length;
        if (w == 0) {
            throw new IllegalArgumentException("No columns provided to group by");
        }

        Hasher mapper = Hasher.of(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            mapper = mapper.and(columns[i]);
        }

        return group(mapper);
    }

    /**
     * An operation similar to SQL "GROUP BY" that partitions this DataFrame into a number of groups based on the values
     * of the specified columns.
     *
     * @param columns columns to group by
     * @return a new GroupBy instance that contains row groupings
     */
    default GroupBy group(int... columns) {

        int w = columns.length;
        if (w == 0) {
            throw new IllegalArgumentException("No columns provided to group by");
        }

        Hasher mapper = Hasher.of(columns[0]);
        for (int i = 1; i < columns.length; i++) {
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

    /**
     * "Explodes" the specified column vertically, adding new rows as needed. This operation has any affect only
     * if the column that is being "exploded" contains Iterables or array elements.
     *
     * @since 0.16
     */
    default DataFrame vExplode(String columnName) {
        return vExplode(getColumnsIndex().position(columnName));
    }

    /**
     * "Explodes" the specified column vertically, adding new rows as needed. This operation has any affect only
     * if the column that is being "exploded" contains Iterables or array elements.
     *
     * @since 0.16
     */
    DataFrame vExplode(int columnPos);

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
     * @return a DataFrame with true/false values corresponding to the result of comparison of this DataFrame with
     * another.
     * @since 0.6
     */
    DataFrame eq(DataFrame another);

    /**
     * @param another a DataFrame to compare with.
     * @return a DataFrame with true/false values corresponding to the result of comparison of this DataFrame with
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
     * Returns a mutable builder of a "pivot" transformation.
     *
     * @since 0.11
     */
    default PivotBuilder pivot() {
        return new PivotBuilder(this);
    }

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

    /**
     * Returns a new {@link WindowBuilder} that allows to assemble a window function over this DataFrame.
     *
     * @return a new {@link WindowBuilder}
     */
    default WindowBuilder over() {
        return new WindowBuilder(this);
    }

    @Override
    Iterator<RowProxy> iterator();
}
