package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * A builder of aggregators that can perform prefiltering and potentially other operations on the DataFrame before
 * applying aggregation function.
 *
 * @since 0.7
 * @deprecated since 0.11 in favor of expression API
 */
@Deprecated
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
        appendRowFilter(new ValuePredicateCondition<>((Exp<V>) Exp.$col(column), filter));
        return this;
    }

    public <V> AggregatorBuilder filterRows(String column, ValuePredicate<V> filter) {
        appendRowFilter(new ValuePredicateCondition<>((Exp<V>) Exp.$col(column), filter));
        return this;
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Exp<T> first(String column) {
        return filter != null
                // TODO: once the performance TODO in FilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FilteredFirstAggregator
                ? new FilteredFirstAggregator<>(filter, (Exp<T>) Exp.$col(column))
                : Aggregator.first(column);
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Exp<T> first(int column) {
        return filter != null
                // TODO: once the performance TODO in FilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FilteredFirstAggregator
                ? new FilteredFirstAggregator<>(filter, (Exp<T>) Exp.$col(column))
                : Aggregator.first(column);
    }

    /**
     * Creates an aggregator to count rows
     */
    public Exp<Long> countLong() {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.countLong())
                : Aggregator.countLong();
    }

    /**
     * Creates an aggregator to count rows.
     */
    public Exp<Integer> countInt() {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.countInt())
                : Aggregator.countInt();
    }

    public Exp<Double> averageDouble(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Exp<Double> averageDouble(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Exp<Long> sumLong(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Exp<Long> sumLong(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Exp<Integer> sumInt(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Exp<Integer> sumInt(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Exp<Double> sumDouble(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumDouble(column))
                : Aggregator.sumDouble(column);
    }

    public Exp<Double> sumDouble(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.sumDouble(column))
                : Aggregator.sumDouble(column);
    }

    public <T extends Comparable<T>> Exp<T> max(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>max(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Exp<T> max(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>max(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Exp<T> min(String column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>min(column))
                : Aggregator.max(column);
    }

    public <T extends Comparable<T>> Exp<T> min(int column) {
        return filter != null
                ? new FilteredAggregator<>(filter, Aggregator.<T>min(column))
                : Aggregator.max(column);
    }

    protected void appendRowFilter(SeriesCondition filter) {
        this.filter = this.filter != null ? this.filter.and(filter) : filter;
    }
}
