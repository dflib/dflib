package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.groupby.Grouper;
import com.nhl.dflib.join.HashJoiner;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.row.BaseRowDataFrame;
import com.nhl.dflib.row.RowProxy;

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
     * Creates a DataFrame by folding the provided stream of objects into rows and columns row by row.
     */
    static <T> DataFrame fromStreamFoldByRow(Index columns, Stream<T> stream) {
        return BaseRowDataFrame.fromStreamFoldByRow(columns, stream);
    }

    /**
     * Creates a DataFrame by folding the provided array of objects into rows and columns row by row.
     */
    static DataFrame fromSequenceFoldByRow(Index columns, Object... sequence) {
        return BaseRowDataFrame.fromSequenceFoldByRow(columns, sequence);
    }

    static DataFrame fromRows(Index columns, Object[]... rows) {
        return BaseRowDataFrame.fromRows(columns, rows);
    }

    static DataFrame fromListOfRows(Index columns, List<Object[]> sources) {
        return BaseRowDataFrame.fromListOfRows(columns, sources);
    }

    static DataFrame fromRows(Index columns, Iterable<Object[]> source) {
        return BaseRowDataFrame.fromRows(columns, source);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    static <T> DataFrame fromObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return BaseRowDataFrame.fromObjects(columns, rows, rowMapper);
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
    Index getColumns();

    /**
     * Returns a read-only iterator over DataFrame columnar data.
     *
     * @return a read-only iterator over DataFrame columnar data
     */
    Iterable<Series<?>> getDataColumns();

    /**
     * Returns the number of rows in this DataFrame. Aka the DataFrame "height". Note that depending on the type of
     * the DataFrame this operation may or may not be constant speed. In the worst case it would require a full scan
     * through all rows.
     *
     * @return an int indicating the number of rows in the DataFrame
     */
    int height();

    default int width() {
        return getColumns().size();
    }

    DataFrame head(int len);

    /**
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/filter functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    DataFrame materialize();

    DataFrame map(Index mappedColumns, RowMapper rowMapper);

    <V, VR> DataFrame mapColumnValue(String columnName, ValueMapper<V, VR> m);

    default <V> DataFrame mapColumn(String columnName, RowToValueMapper<V> m) {
        Index index = getColumns();
        Index compactIndex = index.compactIndex();
        int pos = index.position(columnName).ordinal();
        return map(compactIndex, RowMapper.mapColumn(pos, m));
    }

    default <V> DataFrame addColumn(String columnName, RowToValueMapper<V> columnValueProducer) {
        return addColumns(new String[]{columnName}, columnValueProducer);
    }

    <V> DataFrame addColumns(String[] columnNames, RowToValueMapper<V>... columnValueProducers);

    DataFrame renameColumns(String... columnNames);

    default DataFrame renameColumn(String oldName, String newName) {
        return renameColumns(Collections.singletonMap(oldName, newName));
    }

    DataFrame renameColumns(Map<String, String> oldToNewNames);

    DataFrame selectColumns(String... columnNames);

    DataFrame dropColumns(String... columnNames);

    DataFrame filter(RowPredicate p);

    default <V> DataFrame filterByColumn(String columnName, ValuePredicate<V> p) {
        int pos = getColumns().position(columnName).ordinal();
        return filterByColumn(pos, p);
    }

    <V> DataFrame filterByColumn(int columnPos, ValuePredicate<V> p);

    <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor);

    <V extends Comparable<? super V>> DataFrame sortByColumns(String... columns);

    <V extends Comparable<? super V>> DataFrame sortByColumns(int... columns);

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
     * @param <K>
     * @return a DataFrame that is a result of this join
     */
    default <K> DataFrame innerJoin(
            DataFrame df,
            Hasher leftHasher,
            Hasher rightHasher) {
        return join(df, leftHasher, rightHasher, JoinType.inner);
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
    default DataFrame join(
            DataFrame df,
            Hasher leftHasher,
            Hasher rightHasher,
            JoinType how) {
        HashJoiner joiner = new HashJoiner(leftHasher, rightHasher, how);
        Index joinedIndex = joiner.joinIndex(getColumns(), df.getColumns());
        return joiner.joinRows(joinedIndex, this, df);
    }

    /**
     * Aggregates DataFrame contents into an Object[] of values, using provided aggregator.
     *
     * @param aggregator an aggregator function
     * @return an Object[] with aggregated results
     */
    default Object[] agg(Aggregator aggregator) {
        return aggregator.aggregate(this);
    }

    /**
     * Aggregates DataFrame columns into an Object[] using provided per-column aggregators. Note that aggregator
     * positions correspond to resulting array positions and do not necessarily match column positions in the DataFrame.
     *
     * @param aggregators an array of aggregators corresponding to the aggregated result columns
     * @return an Object[] with aggregated results
     */
    default Object[] agg(ColumnAggregator... aggregators) {
        return Aggregator.forColumns(aggregators).aggregate(this);
    }

    default GroupBy groupBy(String... columns) {

        if (columns.length == 0) {
            throw new IllegalArgumentException("No columns for 'groupBy' specified");
        }

        Hasher mapper = Hasher.forColumn(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            mapper = mapper.and(columns[i]);
        }

        return groupBy(mapper);
    }

    default GroupBy groupBy(Hasher by) {
        return new Grouper(by).group(this);
    }

    @Override
    Iterator<RowProxy> iterator();
}
