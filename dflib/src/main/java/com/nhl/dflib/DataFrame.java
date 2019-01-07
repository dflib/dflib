package com.nhl.dflib;

import com.nhl.dflib.filter.DataRowPredicate;
import com.nhl.dflib.join.IndexedJoiner;
import com.nhl.dflib.join.JoinKeyMapper;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinSemantics;
import com.nhl.dflib.join.Joiner;
import com.nhl.dflib.map.DataRowCombiner;
import com.nhl.dflib.map.DataRowConsumer;
import com.nhl.dflib.map.DataRowMapper;
import com.nhl.dflib.map.DataRowToValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.zip.Zipper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    static DataFrame create(Index columns, Object[]... sources) {
        return new MaterializedDataFrame(columns, asList(sources));
    }

    static DataFrame create(Index columns, List<Object[]> sources) {
        return new MaterializedDataFrame(columns, sources);
    }

    static DataFrame create(Index columns, Iterable<Object[]> source) {
        return new SimpleDataFrame(columns, source);
    }

    static <T> DataFrame create(Index columns, Iterable<T> source, Function<T, Object[]> toArrayMapper) {
        return new SimpleDataFrame(columns, new TransformingIterable<>(source, toArrayMapper));
    }

    Index getColumns();

    /**
     * Returns the number of rows in this DataFrame. Aka the DataFrame "height". Note that depending on the type of
     * the DataFrame this operation may or may not be constant speed. In the worst case it would require a full scan
     * through all rows.
     *
     * @return a long indicating the number of rows in the DataFrame
     */
    default long height() {

        // not a very efficient implementation; implementors should provide faster versions when possible
        long count = 0;
        Iterator<Object[]> it = iterator();
        while (it.hasNext()) {
            count++;
        }

        return count;
    }

    default long width() {
        return getColumns().size();
    }

    /**
     * Resolves this DataFrame to an implementation that does not require evaluation of the internal
     * mapping/zip/filter functions on every iteration. A call to {@link #materialize()} would not necessarily
     * cause an immediate expensive recalculation. Instead it may return a DataFrame that is  evaluated lazily when
     * the first iterator is requested directly or indirectly.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    default DataFrame materialize() {
        return new MaterializedDataFrame(this);
    }

    default DataFrame head(int len) {
        return new HeadDataFrame(this, len);
    }

    default void consume(DataRowConsumer consumer) {
        Index columns = getColumns();
        forEach(r -> consumer.consume(columns, r));
    }

    default DataFrame map(DataRowMapper rowMapper) {
        return map(getColumns().compactIndex(), rowMapper);
    }

    default DataFrame map(Index mappedColumns, DataRowMapper rowMapper) {
        return new TransformingDataFrame(mappedColumns, this, rowMapper).materialize();
    }

    default <V, VR> DataFrame mapColumn(String columnName, ValueMapper<V, VR> m) {
        Index index = getColumns();
        Index compactIndex = index.compactIndex();
        int pos = index.position(columnName).ordinal();
        return map(compactIndex, (c, r) -> c.mapColumn(r, pos, m));
    }

    default <V> DataFrame mapColumn(String columnName, DataRowToValueMapper<V> m) {
        Index index = getColumns();
        Index compactIndex = index.compactIndex();
        int pos = index.position(columnName).ordinal();
        return map(compactIndex, (c, r) -> c.mapColumn(r, pos, m));
    }

    default <V> DataFrame addColumn(String columnName, DataRowToValueMapper<V> columnValueProducer) {
        return addColumns(new String[]{columnName}, columnValueProducer);
    }

    default <V> DataFrame addColumns(String[] columnNames, DataRowToValueMapper<V>... columnValueProducers) {
        Index index = getColumns();
        Index expandedIndex = index.addNames(columnNames);
        return map(expandedIndex, (c, r) -> index.addValues(c, r, columnValueProducers));
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

    default DataFrame filter(DataRowPredicate p) {
        return new FilteredDataFrame(this, p).materialize();
    }

    /**
     * Returns a DataFrame that combines columns from this and another DataFrame. If the lengths of the DataFrames are
     * not the same, the data from the longest DataFrame is truncated. If two DataFrames have have conflicting columns,
     * an underscore suffix ("_") is added to the column names coming from the right-hand side DataFrame.
     *
     * @param df another DataFrame.
     * @return a new DataFrame that is a combination of columns from this DataFrame and a DataFrame argument.
     */
    default DataFrame zip(DataFrame df) {
        Index zipIndex = Zipper.zipIndex(getColumns(), df.getColumns());
        return zip(zipIndex, df, Zipper.rowZipper());
    }

    default DataFrame zip(Index zippedColumns, DataFrame df, DataRowCombiner c) {
        return new ZippingDataFrame(zippedColumns, this, df, c).materialize();
    }

    default DataFrame innerJoin(DataFrame df, JoinPredicate p) {
        return join(df, p, JoinSemantics.inner);
    }

    default DataFrame join(DataFrame df, JoinPredicate predicate, JoinSemantics semantics) {
        Joiner joiner = new Joiner(predicate, semantics);
        Index joinedIndex = joiner.joinIndex(getColumns(), df.getColumns());
        return joiner.joinRows(joinedIndex, this, df);
    }

    default <K> DataFrame innerJoin(
            DataFrame df,
            JoinKeyMapper<K> leftKeyMapper,
            JoinKeyMapper<K> rightKeyMapper) {
        return join(df, leftKeyMapper, rightKeyMapper, JoinSemantics.inner);
    }

    default <K> DataFrame join(
            DataFrame df,
            JoinKeyMapper<K> leftKeyMapper,
            JoinKeyMapper<K> rightKeyMapper,
            JoinSemantics semantics) {
        IndexedJoiner<K> joiner = new IndexedJoiner<>(leftKeyMapper, rightKeyMapper, semantics);
        Index joinedIndex = joiner.joinIndex(getColumns(), df.getColumns());
        return joiner.joinRows(joinedIndex, this, df);
    }

    @Override
    Iterator<Object[]> iterator();
}
