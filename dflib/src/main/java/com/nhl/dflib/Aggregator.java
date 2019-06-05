package com.nhl.dflib;

import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.aggregate.MultiColumnAggregator;
import com.nhl.dflib.aggregate.SingleColumnAggregator;
import com.nhl.dflib.map.ColumnLocator;

import java.util.stream.Collector;

/**
 * Defines aggregation operation on a DataFrame. Provides methods for composing an aggregator from individual columns
 * aggregators.
 */
public interface Aggregator {

    static Aggregator forColumns(ColumnAggregator... columnAggregators) {
        return columnAggregators.length == 1
                ? new SingleColumnAggregator(columnAggregators[0])
                : new MultiColumnAggregator(columnAggregators);
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static ColumnAggregator first(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.first()
        );
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static ColumnAggregator first(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.first()
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong(String)}
     */
    @Deprecated
    static ColumnAggregator count(String column) {
        return countLong(column);
    }

    /**
     * Creates an aggregator to calculate a sum of the specified column.
     *
     * @param column
     * @return a new ColumnAggregator
     * @since 0.6
     */
    static ColumnAggregator countLong(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.countLong()
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong(int)}
     */
    @Deprecated
    static ColumnAggregator count(int column) {
        return countLong(column);
    }

    /**
     * Creates an aggregator to calculate a sum of the specified column.
     *
     * @param column
     * @return a new ColumnAggregator
     * @since 0.6
     */
    static ColumnAggregator countLong(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.countLong()
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(String)}
     */
    @Deprecated
    static ColumnAggregator average(String column) {
        return averageDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(int)}
     */
    @Deprecated
    static ColumnAggregator average(int column) {
        return averageDouble(column);
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator averageDouble(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.averageDouble()
        );
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator averageDouble(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.averageDouble()
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(String)}
     */
    @Deprecated
    static ColumnAggregator median(String column) {
        return medianDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(int)}
     */
    @Deprecated
    static ColumnAggregator median(int column) {
        return medianDouble(column);
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator medianDouble(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.medianDouble()
        );
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator medianDouble(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.medianDouble()
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(String)}
     */
    static ColumnAggregator sum(String column) {
        return sumLong(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(int)}
     */
    static ColumnAggregator sum(int column) {
        return sumLong(column);
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator sumLong(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.sumLong()
        );
    }

    /**
     * @since 0.6
     */
    static ColumnAggregator sumLong(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.sumLong()
        );
    }

    static ColumnAggregator sumDouble(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.sumDouble()
        );
    }

    static ColumnAggregator sumDouble(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.sumDouble()
        );
    }

    static ColumnAggregator concat(String column, String delimiter) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.concat(delimiter)
        );
    }

    static ColumnAggregator concat(int column, String delimiter) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.concat(delimiter)
        );
    }

    static ColumnAggregator concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.concat(delimiter, prefix, suffix)
        );
    }

    static ColumnAggregator concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.concat(delimiter, prefix, suffix)
        );
    }

    static ColumnAggregator list(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.list()
        );
    }

    static ColumnAggregator list(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.list()
        );
    }

    static ColumnAggregator set(String column) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.set()
        );
    }

    static ColumnAggregator set(int column) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.set()
        );
    }

    static ColumnAggregator of(String column, Collector<?, ?, ?> aggregator) {
        return new ColumnAggregator(
                ColumnLocator.forName(column),
                SeriesAggregator.of(aggregator)
        );
    }

    static ColumnAggregator of(int column, Collector<?, ?, ?> aggregator) {
        return new ColumnAggregator(
                ColumnLocator.forPosition(column),
                SeriesAggregator.of(aggregator)
        );
    }

    Index aggregateIndex(Index columns);

    Object[] aggregate(DataFrame df);
}
