package org.dflib;

import org.dflib.builder.DataFrameArrayByRowBuilder;
import org.dflib.builder.DataFrameByColumnBuilder;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.builder.DataFrameFoldByColumnBuilder;
import org.dflib.builder.DataFrameFoldByRowBuilder;
import org.dflib.join.Join;
import org.dflib.pivot.PivotBuilder;
import org.dflib.row.RowProxy;
import org.dflib.sample.Sampler;
import org.dflib.select.RowIndexer;
import org.dflib.window.Window;

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
     * @since 1.0.0-M19
     */
    default Join leftJoin(DataFrame rightFrame) {
        return new Join(JoinType.left, this, rightFrame);
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
     * @since 1.0.0-M19
     */
    default Join fullJoin(DataFrame rightFrame) {
        return new Join(JoinType.full, this, rightFrame);
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
     * Returns a new {@link Window} that allows to assemble a window function over this DataFrame.
     *
     * @return a new {@link Window}
     */
    default Window over() {
        return new Window(this);
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
