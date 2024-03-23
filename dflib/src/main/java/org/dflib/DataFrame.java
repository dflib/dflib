package org.dflib;

import org.dflib.builder.DataFrameArrayByRowBuilder;
import org.dflib.builder.DataFrameByColumnBuilder;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.builder.DataFrameFoldByColumnBuilder;
import org.dflib.builder.DataFrameFoldByRowBuilder;
import org.dflib.join.Join;
import org.dflib.join.JoinBuilder;
import org.dflib.pivot.PivotBuilder;
import org.dflib.row.RowProxy;
import org.dflib.sample.Sampler;
import org.dflib.select.RowIndexer;
import org.dflib.window.WindowBuilder;

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
        return empty(Index.of(columnNames));
    }

    /**
     * Creates an empty DataFrame
     *
     * @since 0.16
     */
    static DataFrame empty(Index columnsIndex) {
        return new ColumnDataFrame(null, columnsIndex);
    }

    /**
     * Starts a DataFrame builder that will build a DataFrame based on some collection of Series. This is one of the
     * most efficient ways to build DataFrames.
     *
     * @since 0.16
     */
    static DataFrameByColumnBuilder byColumn(String... columnLabels) {
        return byColumn(Index.of(columnLabels));
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
     * Starts a DataFrame builder that will extract data from a collection of arrays. Each array would result in
     * a row in a DataFrame. This is a flavor of {@link #byRow(Extractor[])} that allows to append arrays using
     * vararg methods. Provided extractors will be applied to the array to calculate cell values.
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
        return byArrayRow(Index.of(columnLabels));
    }

    /**
     * Starts a DataFrame builder that will extract data from a collection of arrays. Each array would produce
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
        return foldByRow(Index.of(Objects.requireNonNull(columnLabels)));
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
        return foldByColumn(Index.of(Objects.requireNonNull(columnLabels)));
    }

    /**
     * @since 0.16
     */
    static DataFrameFoldByColumnBuilder foldByColumn(Index columnIndex) {
        return new DataFrameFoldByColumnBuilder(columnIndex);
    }

    /**
     * Returns user-assigned name of the DataFrame. It is null by default, and can be set via {@link #as(String)}.
     * A name is useful in various contexts. E.g. the result of a join may prefix column names with original DataFrame
     * name, helping to identify the origin of each column.
     *
     * @since 1.0.0-M19
     */
    String getName();

    /**
     * Assigns a name to the DataFrame.
     *
     * @since 1.0.0-M19
     */
    DataFrame as(String name);

    /**
     * Returns DataFrame column index.
     *
     * @return DataFrame column index
     */
    Index getColumnsIndex();

    <T> Series<T> getColumn(String name) throws IllegalArgumentException;

    <T> Series<T> getColumn(int pos) throws IllegalArgumentException;

    /**
     * Returns the number of rows in this DataFrame, aka "height". Depending on the type of columns in the DataFrame,
     * this operation may or may not be constant speed. In the worst case it may cause a full scan through at least one
     * of the columns.
     *
     * @return an int indicating the number of rows in the DataFrame
     */
    int height();

    /**
     * Returns the number of columns in this DataFrame.
     */
    default int width() {
        return getColumnsIndex().size();
    }

    /**
     * Returns a DataFrame with the first <code>len</code> rows of this DataFrame. If this DataFrame is shorter
     * than the requested length, then the entire DataFrame is returned. If <code>len</code> is negative, instead of
     * returning the leading rows, they are skipped, and the rest of the DataFrame is returned.
     */
    DataFrame head(int len);

    /**
     * Returns a DataFrame with the last <code>len</code> rows of this DataFrame. If this DataFrame is shorter than the
     * requested length, then the entire DataFrame is returned. If <code>len</code> is negative, instead of returning the
     * trailing rows, they are skipped, and the rest of the DataFrame is returned.
     */
    DataFrame tail(int len);

    /**
     * Resolves each column in the DataFrame executing any lazy calculations. If called more than once, the first
     * evaluation result is reused.
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
     * result DataFrame will be the same height as this, but can have a different width and set of columns.
     *
     * <p><i>This is a fairly low-level and cumbersome API. It may be useful when the application needs to dynamically
     * select a list of columns to operate on.
     *
     * @param resultColumns column index of the new DataFrame
     * @param rowMapper     a function applied to each row of this DataFrame
     * @return a new DataFrame
     * @deprecated use more performant {@link #cols(Index)} and then {@link ColumnSet#select(RowMapper)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame map(Index resultColumns, RowMapper rowMapper) {
        return cols(resultColumns).select(rowMapper);
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
     * Replaces column contents using the expression.
     *
     * @since 0.18
     * @deprecated in favor of {@link #cols(String...)} and then {@link ColumnSet#map(Exp[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame replaceColumn(String name, Exp<?> exp) {
        return cols(name).map(exp);
    }

    /**
     * Replaces column contents using the expression.
     *
     * @since 0.18
     * @deprecated in favor of {@link #cols(int...)}  and then {@link ColumnSet#map(Exp[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame replaceColumn(int position, Exp<?> exp) {
        return cols(position).map(exp);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to ints
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactInt(IntValueMapper)}  instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactInt(String columnLabel, IntValueMapper<V> converter) {
        return cols(columnLabel).compactInt(converter);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactInt(IntValueMapper)}  instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactInt(int pos, IntValueMapper<V> converter) {
        return cols(pos).compactInt(converter);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactInt(int)}  instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactInt(String columnLabel, int forNull) {
        return cols(columnLabel).compactInt(forNull);
    }

    /**
     * "Compacts" the internal representation of the Integer column, converting it to a {@link IntSeries}.
     *
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactInt(IntValueMapper)}  instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactInt(int pos, int forNull) {
        return cols(pos).compactInt(forNull);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to doubles
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactDouble(DoubleValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactDouble(String columnLabel, DoubleValueMapper<V> converter) {
        return cols(columnLabel).compactDouble(converter);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to ints
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactDouble(DoubleValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactDouble(int pos, DoubleValueMapper<V> converter) {
        return cols(pos).compactDouble(converter);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param forNull     a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactDouble(double)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactDouble(String columnLabel, double forNull) {
        return cols(columnLabel).compactDouble(forNull);
    }

    /**
     * "Compacts" the internal representation of the Double column, converting it to an {@link DoubleSeries}.
     *
     * @param pos     position of a column to convert
     * @param forNull a value to use in place of nulls
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactDouble(double)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactDouble(int pos, double forNull) {
        return cols(pos).compactDouble(forNull);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to booleans
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactBool(BoolValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactBool(String columnLabel, BoolValueMapper<V> converter) {
        return cols(columnLabel).compactBool(converter);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to booleans
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactBool(BoolValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactBool(int pos, BoolValueMapper<V> converter) {
        return cols(pos).compactBool(converter);
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param columnLabel name of a column to convert
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactBool()} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactBool(String columnLabel) {
        return cols(columnLabel).compactBool();
    }

    /**
     * "Compacts" the internal representation of the Boolean column, converting it to a {@link BooleanSeries}.
     *
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactBool()} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactBool(int pos) {
        return cols(pos).compactBool();
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param columnLabel name of a column to convert
     * @param converter   a function to apply to column values to covert them to longs
     * @param <V>         expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactLong(LongValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactLong(String columnLabel, LongValueMapper<V> converter) {
        return cols(columnLabel).compactLong(converter);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param pos       position of a column to convert
     * @param converter a function to apply to column values to covert them to longs
     * @param <V>       expected input column value
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactLong(LongValueMapper)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame compactLong(int pos, LongValueMapper<V> converter) {
        return cols(pos).compactLong(converter);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param columnLabel name of a column to convert
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#compactLong(long)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactLong(String columnLabel, long forNull) {
        return cols(columnLabel).compactLong(forNull);
    }

    /**
     * "Compacts" the internal representation of the Long column, converting it to a {@link LongSeries}.
     *
     * @param pos position of a column to convert
     * @return a new DataFrame
     * @since 0.18
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#compactLong(long)} instead
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame compactLong(int pos, long forNull) {
        return cols(pos).compactLong(forNull);
    }

    /**
     * Adds a column with the specified name to the DataFrame that contains incrementing numbers, starting with "1".
     *
     * @param columnName the name of the row number column
     * @return a new DataFrame with an extra row number column
     * @since 0.8
     * @deprecated in favor of {@link #colsAppend(String...)} and then <code>ColumnSet#map(Exp.rowNum())</code>
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addRowNumberColumn(String columnName) {
        return colsAppend(columnName).map(Exp.rowNum());
    }

    /**
     * <p><i>This is a fairly low-level API. It may be useful when the application needs to dynamically select
     * a list of columns to operate on.
     *
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(RowToValueMapper[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addColumn(String columnLabel, RowToValueMapper<?> columnMaker) {
        return colsAppend(columnLabel).map(columnMaker);
    }

    /**
     * Adds specified columns to the DataFrame.
     *
     * <p><i>This is a fairly low-level and cumbersome API. It may be useful when the application needs to dynamically select
     * a list of columns to operate on.</i></p>
     *
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(RowToValueMapper[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addColumns(String[] columnLabels, RowToValueMapper<?>... columnMakers) {
        return colsAppend(columnLabels).map(columnMakers);
    }

    /**
     * Add one more columns to the DataFrame.
     *
     * <p><i>This is a fairly low-level and cumbersome API. It may be useful when the application needs to dynamically select
     * a list of columns to operate on.</i></p>
     *
     * @param columnLabels the names of the added columns
     * @param rowMapper    a mapper with the "read" part based on this DataFrame index, and "write" part matching the
     *                     newly added columns
     * @return a new DataFrame with extra columns added
     * @since 0.7
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(RowMapper)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addColumns(String[] columnLabels, RowMapper rowMapper) {
        return colsAppend(columnLabels).map(rowMapper);
    }

    /**
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(Series[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame addColumn(String columnLabel, Series<V> column) {
        return colsAppend(columnLabel).map(column);
    }

    /**
     * Adds a column with values derived from this DataFrame by applying a given expression.
     *
     * @since 0.11
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(Exp[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addColumn(Exp<?> exp) {
        return colsAppend(exp.getColumnName(this)).map(exp);
    }

    /**
     * Adds multiple columns with values derived from this DataFrame by applying provided expressions. Column names will
     * be taken from {@link Exp} names.
     *
     * @since 0.11
     * @deprecated in favor of {@link #colsAppend(String...)} and then {@link ColumnSet#map(Exp[])}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addColumns(Exp<?>... exps) {
        int w = exps.length;
        String[] names = new String[w];
        for (int i = 0; i < w; i++) {
            names[i] = exps[i].getColumnName(this);
        }

        return colsAppend(names).map(exps);
    }

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
     * @deprecated use {@link #colsAppend(String...)} and then {@link ColumnSet#fill(Object...)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame addSingleValueColumn(String columnLabel, Object value) {
        return colsAppend(columnLabel).fill(value);
    }

    /**
     * @deprecated use {@link #cols()} and then {@link ColumnSet#as(String...)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame renameColumns(String... columnLabels) {
        return cols().as(columnLabels);
    }

    /**
     * @deprecated use {@link #cols()} and then {@link ColumnSet#as(Map)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame renameColumn(String oldLabel, String newLabel) {
        return cols().as(Map.of(oldLabel, newLabel));
    }

    /**
     * Renames column index labels by applying the provided function to each label. Useful for name conversions like
     * lower-casing, etc.
     *
     * @param renamer a function that is passed each label in turn
     * @return a new DataFrame with renamed columns
     * @since 0.6
     * @deprecated use {@link #cols()} and then {@link ColumnSet#as(UnaryOperator)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame renameColumns(UnaryOperator<String> renamer) {
        return cols().as(renamer);
    }

    /**
     * @deprecated use {@link #cols()} and then {@link ColumnSet#as(Map)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame renameColumns(Map<String, String> oldToNewLabels) {
        return cols().as(oldToNewLabels);
    }

    /**
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectColumns(String... labels) {
        return cols(labels).select();
    }

    /**
     * @deprecated use {@link #cols(int...)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectColumns(int... positions) {
        return cols(positions).select();
    }

    /**
     * @param columnsIndex an index that defines a subset of columns and their ordering in the returned DataFrame.
     * @return a new DataFrame.
     * @since 0.6
     * @deprecated use {@link #cols(Index)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectColumns(Index columnsIndex) {
        return cols(columnsIndex).select();
    }

    /**
     * Returns a DataFrame with only columns from this DataFrame whose labels match the specified condition.
     *
     * @param labelCondition a condition evaluated against column labels to determine whether they should be included
     *                       in the resulting DataFrame
     * @return a new DataFrame
     * @since 0.7
     * @deprecated use {@link #cols(Predicate)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectColumns(Predicate<String> labelCondition) {
        return cols(labelCondition).select();
    }

    /**
     * @since 0.11
     * @deprecated use {@link #cols()} and then {@link ColumnSet#select(Exp...)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectColumns(Exp<?>... exps) {
        return cols().select(exps);
    }

    /**
     * Returns a DataFrame with columns from this DataFrame, except the specified column.
     *
     * @since 0.18
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#drop()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame dropColumn(String columnLabel) {
        return dropColumns(columnLabel);
    }


    /**
     * Returns a DataFrame with columns from this DataFrame, except the specified columns.
     *
     * @deprecated use {@link #cols(String...)} and then {@link ColumnSet#drop()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame dropColumns(String... columnLabels) {
        return colsExcept(columnLabels).select();
    }

    /**
     * Returns a DataFrame with columns from this DataFrame, except those columns whose labels match the specified
     * condition.
     *
     * @param labelCondition a condition evaluated against column labels to determine whether they should be removed
     *                       from the resulting DataFrame
     * @return a new DataFrame
     * @since 0.7
     * @deprecated use {@link #cols(Predicate)} and then {@link ColumnSet#drop()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame dropColumns(Predicate<String> labelCondition) {
        return cols(labelCondition.negate()).select();
    }

    /**
     * Selects DataFrame rows based on provided row index. This allows to reorder, filter, duplicate rows of this
     * DataFrame.
     *
     * @return a new DataFrame that matches the selection criteria
     * @since 0.6
     * @deprecated in favor of {@link #rows(int...)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRows(int... rowPositions) {
        return rows(rowPositions).select();
    }

    /**
     * Selects DataFrame rows based on provided row index. This allows to reorder, filter, duplicate rows of this
     * DataFrame.
     *
     * @return a new DataFrame that matches the selection criteria
     * @since 0.6
     * @deprecated in favor of {@link #rows(IntSeries)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRows(IntSeries rowPositions) {
        return rows(rowPositions).select();
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #rows(Condition)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRows(Condition condition) {
        return rows(condition).select();
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #rows(RowPredicate)}  and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRows(RowPredicate condition) {
        return rows(condition).select();
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #rows(Condition)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default <V> DataFrame selectRows(String columnName, ValuePredicate<V> p) {
        return selectRows(getColumnsIndex().position(columnName), p);
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #rows(Condition)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    <V> DataFrame selectRows(int columnPos, ValuePredicate<V> p);

    /**
     * Returns a DataFrame with subset of rows matching condition.
     *
     * @param condition a {@link BooleanSeries} whose "true" values indicate which rows to keep in the result
     * @since 0.11
     * @deprecated in favor of {@link #rows(BooleanSeries)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRows(BooleanSeries condition) {
        return rows(condition).select();
    }

    /**
     * Returns a DataFrame that contains a range of rows from this DataFrame.
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @since 0.14
     * @deprecated in favor of {@link #rowsRange(int, int)} and then {@link RowSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame selectRowRangeOpenClosed(int fromInclusive, int toExclusive) {
        return rowsRange(fromInclusive, toExclusive).select();
    }

    /**
     * Returns a DataFrame with non-repeating rows.
     *
     * @since 0.18
     * @deprecated in favor of {@link #rows()} and then {@link RowSet#unique()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame uniqueRows() {
        return rows().unique();
    }

    /**
     * Returns a DataFrame with non-repeating rows. Uniqueness check is done based on the specified subset of columns,
     * ignoring all others.
     *
     * @since 0.18
     * @deprecated in favor of {@link #rows()} and then {@link RowSet#unique(String...)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame uniqueRows(String... columnNamesToCompare) {
        return rows().unique(columnNamesToCompare);
    }

    /**
     * Returns a DataFrame with non-repeating rows. Uniqueness check is only done based on the specified subset of
     * columns, ignoring all others.
     *
     * @since 0.18
     * @deprecated in favor of {@link #rows()} and then {@link RowSet#unique(int...)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame uniqueRows(int... columnNamesToCompare) {
        return rows().unique(columnNamesToCompare);
    }

    /**
     * @since 0.11
     */
    default DataFrame sort(Sorter... sorters) {
        return rows().sort(sorters);
    }

    default DataFrame sort(String column, boolean ascending) {
        return rows().sort(column, ascending);
    }

    default DataFrame sort(int column, boolean ascending) {
        return rows().sort(column, ascending);
    }

    default DataFrame sort(String[] columns, boolean[] ascending) {
        return rows().sort(columns, ascending);
    }

    default DataFrame sort(int[] columns, boolean[] ascending) {
        return rows().sort(columns, ascending);
    }

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
     * @return an inner join builder
     * @since 0.6
     * @deprecated in favor of {@link #innerJoin(DataFrame)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default JoinBuilder innerJoin() {
        return new JoinBuilder(this).type(JoinType.inner);
    }

    /**
     * A shorter-named equivalent of {@link #innerJoin(DataFrame)}
     *
     * @return an inner join builder
     * @since 1.0.0-M19
     */
    default Join join(DataFrame rightFrame) {
        return innerJoin(rightFrame);
    }

    /**
     * @return an inner join builder
     * @since 1.0.0-M19
     */
    default Join innerJoin(DataFrame rightFrame) {
        return new Join(JoinType.inner, this, rightFrame);
    }

    /**
     * @return a left join builder
     * @since 0.6
     * @deprecated in favor of {@link #leftJoin(DataFrame)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default JoinBuilder leftJoin() {
        return new JoinBuilder(this).type(JoinType.left);
    }

    /**
     * @return a left join builder
     * @since 1.0.0-M19
     */
    default Join leftJoin(DataFrame rightFrame) {
        return new Join(JoinType.left, this, rightFrame);
    }

    /**
     * @return a right join builder
     * @since 0.6
     * @deprecated in favor of {@link #rightJoin(DataFrame)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default JoinBuilder rightJoin() {
        return new JoinBuilder(this).type(JoinType.right);
    }

    /**
     * @return a right join builder
     * @since 1.0.0-M19
     */
    default Join rightJoin(DataFrame rightFrame) {
        return new Join(JoinType.right, this, rightFrame);
    }

    /**
     * @return a full join builder
     * @since 0.6
     * @deprecated in favor of {@link #fullJoin(DataFrame)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default JoinBuilder fullJoin() {
        return new JoinBuilder(this).type(JoinType.full);
    }

    /**
     * @return a full join builder
     * @since 1.0.0-M19
     */
    default Join fullJoin(DataFrame rightFrame) {
        return new Join(JoinType.full, this, rightFrame);
    }

    /**
     * Aggregates DataFrame columns into a single-row DataFrame, using provided per-column aggregators. Note that
     * aggregator positions correspond to returned DataFrame columns and do not generally match column positions
     * in this DataFrame.
     *
     * @param aggregators an array of aggregators corresponding to the aggregated result columns
     * @return a DataFrame with a single row
     * @see Exp for static factory methods of column aggregators
     * @deprecated in favor of {@link #cols()} and then {@link ColumnSet#agg(Exp[])}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    default DataFrame agg(Exp<?>... aggregators) {
        return cols().agg(aggregators);
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

        Hasher hasher = Hasher.of(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            hasher = hasher.and(columns[i]);
        }

        return group(hasher);
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

        Hasher hasher = Hasher.of(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            hasher = hasher.and(columns[i]);
        }

        return group(hasher);
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
     * @deprecated in favor of {@link #rows()} and then {@link RowSet#expand(String)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame vExplode(String columnName) {
        return vExplode(getColumnsIndex().position(columnName));
    }

    /**
     * "Explodes" the specified column vertically, adding new rows as needed. This operation has any affect only
     * if the column that is being "exploded" contains Iterables or array elements.
     *
     * @since 0.16
     * @deprecated in favor of {@link #rows()} and then {@link RowSet#expand(int)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame vExplode(int columnPos) {
        return rows().expand(columnPos);
    }

    /**
     * @deprecated in favor of {@link #cols()} and then {@link ColumnSet#fillNulls(Object)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNulls(Object value) {
        return cols().fillNulls(value);
    }

    /**
     * @deprecated in favor of {@link #cols(int...)} and then {@link ColumnSet#fillNulls(Object)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNulls(int columnPos, Object value) {
        return cols(columnPos).fillNulls(value);
    }

    /**
     * @deprecated in favor of {@link #cols(String...)} and then {@link ColumnSet#fillNulls(Object)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNulls(String columnName, Object value) {
        return cols(columnName).fillNulls(value);
    }

    /**
     * Fills nulls in the specified column with values taken from a Series at a given index.
     *
     * @param columnPos a name of the column whose nulls need to be filled
     * @param values    a Series to take null replacement values from
     * @return a new DataFrame
     * @since 0.6
     * @deprecated in favor of {@link #cols(int...)} and then {@link ColumnSet#fillNullsFromSeries(Series)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsFromSeries(int columnPos, Series<?> values) {
        return cols(columnPos).fillNullsFromSeries(values);
    }

    /**
     * Fills nulls in the specified column with values taken from a Series at a given index.
     *
     * @param columnName a name of the column whose nulls need to be filled.
     * @param values     a Series to take null replacement values from
     * @return a new DataFrame
     * @since 0.6
     * @deprecated in favor of {@link #cols(String...)} and then {@link ColumnSet#fillNullsFromSeries(Series)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsFromSeries(String columnName, Series<?> values) {
        return cols(columnName).fillNullsFromSeries(values);
    }

    /**
     * @deprecated in favor of {@link #cols(int...)} and then {@link ColumnSet#fillNullsBackwards()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsBackwards(int columnPos) {
        return cols(columnPos).fillNullsBackwards();
    }

    /**
     * @deprecated in favor of {@link #cols(String...)} and then {@link ColumnSet#fillNullsBackwards()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsBackwards(String columnName) {
        return cols(columnName).fillNullsBackwards();
    }

    /**
     * @deprecated in favor of {@link #cols(int...)} and then {@link ColumnSet#fillNullsForward()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsForward(int columnPos) {
        return cols(columnPos).fillNullsForward();
    }

    /**
     * @deprecated in favor of {@link #cols(String...)} and then {@link ColumnSet#fillNullsForward()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame fillNullsForward(String columnName) {
        return cols(columnName).fillNullsForward();
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
     * @deprecated in favor of {@link #rowsSample(int)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    DataFrame sampleRows(int size);

    /**
     * Returns a DataFrame object that is a random sample of rows from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the height of this DataFrame.
     * @param random a custom random number generator
     * @return a DataFrame object that is a sample of rows from this object
     * @since 0.7
     * @deprecated in favor of {@link #rowsSample(int, Random)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    DataFrame sampleRows(int size, Random random);

    /**
     * Returns a DataFrame object that is a random sample of columns from this object, with the specified sample size.
     * If you are doing sampling in a high concurrency application, consider using {@link #sampleRows(int, Random)}, as
     * this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the height of this DataFrame.
     * @return a DataFrame object that is a sample of columns from this object
     * @since 0.7
     * @deprecated in favor of {@link #colsSample(int)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame sampleColumns(int size) {
        return colsSample(size).select();
    }

    /**
     * Returns a DataFrame object that is a random sample of columns from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the height of this DataFrame.
     * @param random a custom random number generator
     * @return a DataFrame object that is a sample of columns from this object
     * @since 0.7
     * @deprecated in favor of {@link #colsSample(int, Random)} and then {@link ColumnSet#select()}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default DataFrame sampleColumns(int size, Random random) {
        return colsSample(size, random).select();
    }

    /**
     * Returns a new {@link WindowBuilder} that allows to assemble a window function over this DataFrame.
     *
     * @return a new {@link WindowBuilder}
     */
    default WindowBuilder over() {
        return new WindowBuilder(this);
    }

    /**
     * Creates a ColumnSet with column definitions deferred until some operation is applied to it. Columns will be
     * resolved dynamically based on the semantics of the operation.
     *
     * @since 1.0.0-M19
     */
    ColumnSet cols();

    /**
     * Creates a ColumnSet for the columns in the Index. Columns may or may not already exist in the DataFrame.
     * Non-existent columns will be added to the result of the column set operation, while the ones already in the
     * DataFrame will be replaced by columns calculated by the operation.
     *
     * @since 1.0.0-M19
     */
    ColumnSet cols(Index columnsIndex);

    /**
     * Creates a ColumnSet for the provided column names. Columns may or may not already exist in the DataFrame.
     * Non-existent columns will be added to the result of the column set operation, while the ones already in the
     * DataFrame will be replaced by columns calculated by the operation.
     *
     * @since 1.0.0-M19
     */
    ColumnSet cols(String... columns);

    /**
     * Creates a ColumnSet for the provided column names, ensuring that none of the names match any of the DataFrame
     * existing columns. This results in the columns being appended to the DataFrame. For any duplicate name, the
     * ColumnSet labels are renamed to ensure uniqueness, using the common DFLib approach of adding a "_" suffix.
     *
     * @since 1.0.0-M19
     */
    ColumnSet colsAppend(String... columns);

    /**
     * Creates a ColumnSet with columns from this DataFrame excluding specified columns.
     *
     * @since 1.0.0-M19
     */
    default ColumnSet colsExcept(String... columns) {
        // all positions here will be within the existing DataFrame, so we are not losing any labels by delegating to
        // an index-based method
        return cols(getColumnsIndex().positionsExcept(columns));
    }

    /**
     * Creates a ColumnSet with columns from this DataFrame that match the specified condition.
     *
     * @since 1.0.0-M19
     */
    default ColumnSet cols(Predicate<String> condition) {
        // all positions here will be within the existing DataFrame, so we are not losing any labels by delegating to
        // an index-based method
        return cols(getColumnsIndex().positions(condition));
    }

    /**
     * Creates a ColumnSet with columns from this DataFrame that do not match the specified condition.
     *
     * @since 1.0.0-M19
     */
    default ColumnSet colsExcept(Predicate<String> condition) {
        return cols(condition.negate());
    }

    /**
     * Creates a ColumnSet for the provided column positions. Columns may or may not already exist in the DataFrame.
     * Non-existent columns will be added to the result of the column set operation, while the ones already in the
     * DataFrame will be replaced by columns calculated by the operation.
     *
     * @since 1.0.0-M19
     */
    ColumnSet cols(int... columns);

    /**
     * Creates a ColumnSet with columns from this DataFrame excluding specified column positions.
     *
     * @since 1.0.0-M19
     */
    default ColumnSet colsExcept(int... columns) {
        return cols(getColumnsIndex().positionsExcept(columns));
    }

    /**
     * @since 1.0.0-M19
     */
    default ColumnSet colsSample(int size) {
        return cols(getColumnsIndex().sample(size));
    }

    /**
     * @since 1.0.0-M19
     */
    default ColumnSet colsSample(int size, Random random) {
        return cols(getColumnsIndex().sample(size, random));
    }

    /**
     * Returns a {@link RowSet} with all rows from this DataFrame included.
     *
     * @since 1.0.0-M19
     */
    RowSet rows();

    /**
     * @since 1.0.0-M19
     */
    default RowSet rows(Condition rowCondition) {
        IntSeries index = rowCondition.eval(this).indexTrue();

        // there's no reordering or index duplication when applying a Condition,
        // so we can compare the sizes to detect changes
        return index.size() == height()
                ? rows()
                : rows(index);
    }

    /**
     * @since 1.0.0-M19
     */
    default RowSet rows(int... positions) {
        return rows(Series.ofInt(positions));
    }

    /**
     * @since 1.0.0-M19
     */
    default RowSet rowsExcept(int... positions) {
        return rowsExcept(Series.ofInt(positions));
    }

    /**
     * @since 1.0.0-M19
     */
    RowSet rows(IntSeries positions);

    /**
     * @since 1.0.0-M19
     */
    RowSet rowsExcept(IntSeries positions);

    /**
     * @since 1.0.0-M19
     */
    RowSet rows(BooleanSeries condition);

    /**
     * @since 1.0.0-M19
     */
    default RowSet rows(RowPredicate condition) {
        IntSeries index = RowIndexer.index(this, condition);

        // there's no reordering or index duplication when applying RowPredicate,
        // so we can compare the sizes to detect changes
        return index.size() == height()
                ? rows()
                : rows(index);
    }

    /**
     * @since 1.0.0-M19
     */
    default RowSet rowsExcept(RowPredicate condition) {
        return rows(condition.negate());
    }

    /**
     * @since 1.0.0-M19
     */
    default RowSet rowsExcept(Condition condition) {
        return rows(condition.not());
    }

    /**
     * @since 1.0.0-M19
     */
    RowSet rowsRange(int fromInclusive, int toExclusive);

    /**
     * Returns a RowSet that is a random sample of rows from this DataFrame, with the specified sample size and
     * a default random number generator.
     *
     * @since 1.0.0-M19
     */
    default RowSet rowsSample(int size) {
        return rows(Sampler.sampleIndex(size, height()));
    }

    /**
     * Returns a RowSet that is a random sample of rows from this DataFrame, with the specified sample size and
     * a user-provided random number generator.
     *
     * @since 1.0.0-M19
     */
    default RowSet rowsSample(int size, Random random) {
        return rows(Sampler.sampleIndex(size, height(), random));
    }

    @Override
    Iterator<RowProxy> iterator();
}
