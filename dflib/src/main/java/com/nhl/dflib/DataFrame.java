package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.concat.HConcatDataFrame;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.filter.FilteredDataFrame;
import com.nhl.dflib.filter.RowPredicate;
import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.groupby.Grouper;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.join.NestedLoopJoiner;
import com.nhl.dflib.join.HashJoiner;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.map.MappedDataFrame;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowConsumer;
import com.nhl.dflib.map.RowMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.sort.SortedDataFrame;
import com.nhl.dflib.sort.Sorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * An immutable 2D data container with support for a variety of data transformations, queries, joins, etc. Every such
 * transformation returns a new DataFrame object and does not affect the original DataFrame.
 * <p>DataFrame allows to iterate over its contents as Object[]. You need to know two things about these arrays:
 * <ul>
 * <li>While Java arrays are mutable by nature, it will violate the DataFrame contract if a user attempts to change them
 * directly. Don't do that!</li>
 * <li>The size and positions of data values in the array may not exactly match the size of the index. So use {@link Index}
 * API to find the right values and the extents of the record.</li>
 * </ul>
 * </p>
 */
public interface DataFrame extends Iterable<Object[]> {

    /**
     * Creates a DataFrame by folding the provided stream of objects into rows and columns.
     */
    static <T> DataFrame fromStream(Index columns, Stream<T> stream) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object[]> folded = new ArrayList<>();
        Iterator<T> it = stream.iterator();

        int i = 0;
        Object[] row = null;

        while (it.hasNext()) {

            // first iteration
            if (row == null) {
                row = new Object[width];
            }
            // previous row finished
            else if (i % width == 0) {
                folded.add(row);
                row = new Object[width];
            }

            row[i % width] = it.next();
            i++;
        }

        // add last row
        folded.add(row);

        return new MaterializedDataFrame(columns, folded);
    }

    /**
     * Creates a DataFrame by folding the provided array of objects into rows and columns.
     */
    static DataFrame fromSequence(Index columns, Object... sequence) {

        int width = columns.size();
        int rows = sequence.length / width;

        List<Object[]> folded = new ArrayList<>(rows + 1);
        for (int i = 0; i < rows; i++) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, i * width, row, 0, width);
            folded.add(row);
        }

        // copy partial last row
        int leftover = sequence.length % width;
        if (leftover > 0) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, rows * width, row, 0, leftover);
            folded.add(row);
        }

        return new MaterializedDataFrame(columns, folded);
    }

    static DataFrame fromRows(Index columns, Object[]... sources) {
        return new MaterializedDataFrame(columns, asList(sources));
    }

    static DataFrame fromRowsList(Index columns, List<Object[]> sources) {
        return new MaterializedDataFrame(columns, sources);
    }

    static DataFrame fromRows(Index columns, Iterable<Object[]> source) {
        return new SimpleDataFrame(columns, source);
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. The last argument is a function that converts an
     * object to a row (i.e. Object[]).
     */
    static <T> DataFrame fromObjects(Index columns, Iterable<T> source, Function<T, Object[]> rowMapper) {
        return new SimpleDataFrame(columns, new TransformingIterable<>(source, rowMapper)).materialize();
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
     * Returns the number of rows in this DataFrame. Aka the DataFrame "height". Note that depending on the type of
     * the DataFrame this operation may or may not be constant speed. In the worst case it would require a full scan
     * through all rows.
     *
     * @return an int indicating the number of rows in the DataFrame
     */
    default int height() {

        // not a very efficient implementation; implementors should provide faster versions when possible
        int count = 0;
        Iterator<Object[]> it = iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }

        return count;
    }

    default int width() {
        return getColumns().size();
    }

    /**
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/filter functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    default DataFrame materialize() {
        return new MaterializedDataFrame(this);
    }

    default DataFrame head(int len) {
        return new HeadDataFrame(this, len);
    }

    default void consume(RowConsumer consumer) {
        Index columns = getColumns();
        forEach(r -> consumer.consume(columns, r));
    }

    default DataFrame map(RowMapper rowMapper) {
        return map(getColumns().compactIndex(), rowMapper);
    }

    default DataFrame map(Index mappedColumns, RowMapper rowMapper) {
        return new MappedDataFrame(mappedColumns, this, rowMapper).materialize();
    }

    default <V, VR> DataFrame mapColumnValue(String columnName, ValueMapper<V, VR> m) {
        Index index = getColumns();
        Index compactIndex = index.compactIndex();
        int pos = index.position(columnName).ordinal();
        return map(compactIndex, RowMapper.mapColumnValue(pos, m));
    }

    default <V> DataFrame mapColumn(String columnName, RowToValueMapper<V> m) {
        Index index = getColumns();
        Index compactIndex = index.compactIndex();
        int pos = index.position(columnName).ordinal();
        return map(compactIndex, RowMapper.mapColumn(pos, m));
    }

    default <V> DataFrame addColumn(String columnName, RowToValueMapper<V> columnValueProducer) {
        return addColumns(new String[]{columnName}, columnValueProducer);
    }

    default <V> DataFrame addColumns(String[] columnNames, RowToValueMapper<V>... columnValueProducers) {
        Index index = getColumns();
        Index expandedIndex = index.addNames(columnNames);
        return map(expandedIndex, RowMapper.addColumns(columnValueProducers));
    }

    default DataFrame renameColumns(String... columnNames) {
        Index renamed = getColumns().rename(columnNames);
        return new SimpleDataFrame(renamed, this);
    }

    default DataFrame renameColumn(String oldName, String newName) {
        return renameColumns(Collections.singletonMap(oldName, newName));
    }

    default DataFrame renameColumns(Map<String, String> oldToNewNames) {
        Index renamed = getColumns().rename(oldToNewNames);
        return new SimpleDataFrame(renamed, this);
    }

    default DataFrame selectColumns(String... columnNames) {
        Index select = getColumns().selectNames(columnNames);
        return new SimpleDataFrame(select, this);
    }

    default DataFrame dropColumns(String... columnNames) {
        Index index = getColumns();
        Index newIndex = index.dropNames(columnNames);
        return newIndex.size() == index.size()
                ? this
                : new SimpleDataFrame(newIndex, this);
    }

    default DataFrame filter(RowPredicate p) {
        return new FilteredDataFrame(this, p).materialize();
    }

    default <V> DataFrame filterByColumn(String columnName, ValuePredicate<V> p) {
        int pos = getColumns().position(columnName).ordinal();
        return filterByColumn(pos, p);
    }

    default <V> DataFrame filterByColumn(int columnPos, ValuePredicate<V> p) {
        RowPredicate drp = (c, r) -> p.test((V) c.get(r, columnPos));
        return new FilteredDataFrame(this, drp).materialize();
    }

    default <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        return new SortedDataFrame(this, Sorters.sorter(getColumns(), sortKeyExtractor));
    }

    default <V extends Comparable<? super V>> DataFrame sortByColumns(String... columns) {
        if (columns.length == 0) {
            return this;
        }

        return new SortedDataFrame(this, Sorters.sorter(getColumns(), columns));
    }

    default <V extends Comparable<? super V>> DataFrame sortByColumns(int... columns) {
        if (columns.length == 0) {
            return this;
        }

        return new SortedDataFrame(this, Sorters.sorter(getColumns(), columns));
    }

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
     * @param how semantics of zip operation
     * @return a new "wider" DataFrame that is a combination of columns from this DataFrame and a DataFrame argument
     */
    default DataFrame hConcat(JoinType how, DataFrame df) {
        Index zipIndex = HConcat.zipIndex(getColumns(), df.getColumns());
        return hConcat(zipIndex, how, df, RowCombiner.zip(width()));
    }

    default DataFrame hConcat(Index zippedColumns, JoinType how, DataFrame df, RowCombiner c) {
        return new HConcatDataFrame(zippedColumns, how, this, df, c).materialize();
    }

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
    default DataFrame vConcat(JoinType how, DataFrame... dfs) {

        if (dfs.length == 0) {
            return this;
        }

        DataFrame[] combined = new DataFrame[dfs.length + 1];
        combined[0] = this;
        System.arraycopy(dfs, 0, combined, 1, dfs.length);

        return VConcat.concat(how, combined);
    }

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
    default DataFrame join(DataFrame df, JoinPredicate p, JoinType how) {
        NestedLoopJoiner joiner = new NestedLoopJoiner(p, how);
        Index joinedIndex = joiner.joinIndex(getColumns(), df.getColumns());
        return joiner.joinRows(joinedIndex, this, df);
    }

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
    Iterator<Object[]> iterator();
}
