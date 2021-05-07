package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.seriesexp.compat.RowPredicateCondition;
import com.nhl.dflib.seriesexp.compat.ValuePredicateCondition;

/**
 * A builder of aggregators that can perform prefiltering and potentially other operations on the DataFrame before
 * applying aggregation function.
 *
 * @since 0.7
 */
public class AggregatorBuilder {

    private SeriesCondition filter;

    public AggregatorBuilder filterRows(SeriesCondition filter) {
        appendRowFilter(filter);
        return this;
    }

    public AggregatorBuilder filterRows(RowPredicate filter) {
        appendRowFilter(new RowPredicateCondition(filter));
        return this;
    }

    public <V> AggregatorBuilder filterRows(int column, ValuePredicate<V> filter) {
        appendRowFilter(new ValuePredicateCondition<>((SeriesExp<V>) Exp.$col(column), filter));
        return this;
    }

    public <V> AggregatorBuilder filterRows(String column, ValuePredicate<V> filter) {
        appendRowFilter(new ValuePredicateCondition<>((SeriesExp<V>) Exp.$col(column), filter));
        return this;
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(String column) {
        return filter != null
                // TODO: once the performance TODO in FilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FilteredFirstAggregator
                ? new FilteredFirstAggregator<>(filter, (SeriesExp<T>) Exp.$col(column), index -> column)
                : Aggregator.first(column);
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(int column) {
        return filter != null
                // TODO: once the performance TODO in FilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FilteredFirstAggregator
                ? new FilteredFirstAggregator<>(filter, (SeriesExp<T>) Exp.$col(column), index -> index.getLabel(column))
                : Aggregator.first(column);
    }

    /**
     * Creates an aggregator to count rows
     */
    public Aggregator<Long> countLong() {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.countLong())
                : Aggregator.countLong();
    }

    /**
     * Creates an aggregator to count rows.
     */
    public Aggregator<Integer> countInt() {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.countInt())
                : Aggregator.countInt();
    }

    public Aggregator<Double> averageDouble(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Aggregator<Double> averageDouble(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Aggregator<Long> sumLong(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Aggregator<Long> sumLong(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Aggregator<Integer> sumInt(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Aggregator<Integer> sumInt(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Aggregator<Double> sumDouble(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumDouble(column))
                : Aggregator.sumDouble(column);
    }

    public Aggregator<Double> sumDouble(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumDouble(column))
                : Aggregator.sumDouble(column);
    }

    public <T extends Comparable<T>> Aggregator<T> max(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>max(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Aggregator<T> max(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>max(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Aggregator<T> min(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>min(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Aggregator<T> min(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>min(column))
                : Aggregator.max(column);
    }

    protected void appendRowFilter(SeriesCondition filter) {
        this.filter = this.filter != null ? this.filter.and(filter) : filter;
    }
}
