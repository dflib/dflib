package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.ValuePredicate;

/**
 * A builder of aggregators that can perform prefiltering and potentially other operations on the DataFrame before
 * applying aggregation function.
 *
 * @since 0.7
 */
public class AggregatorBuilder {

    private RowPredicate rowFilter;

    public AggregatorBuilder filterRows(RowPredicate filter) {
        appendRowFilter(filter);
        return this;
    }

    public <V> AggregatorBuilder filterRows(int columnPos, ValuePredicate<V> filter) {
        appendRowFilter(r -> filter.test((V) r.get(columnPos)));
        return this;
    }

    public <V> AggregatorBuilder filterRows(String columnLabel, ValuePredicate<V> filter) {
        appendRowFilter(r -> filter.test((V) r.get(columnLabel)));
        return this;
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(String column) {
        return rowFilter != null
                // TODO: once the performance TODO in CollectorFilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FirstFilteredAggregator
                ? new FirstFilteredAggregator<>(rowFilter, index -> index.position(column), index -> column)
                : Aggregator.first(column);
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(int column) {
        return rowFilter != null
                // TODO: once the performance TODO in CollectorFilteredAggregator is resolved, perhaps we won't need
                //  a dedicated FirstFilteredAggregator
                ? new FirstFilteredAggregator<>(rowFilter, index -> column, index -> index.getLabel(column))
                : Aggregator.first(column);
    }

    /**
     * Creates an aggregator to count rows
     */
    public Aggregator<Long> countLong() {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.countLong())
                : Aggregator.countLong();
    }

    /**
     * Creates an aggregator to count rows.
     */
    public Aggregator<Integer> countInt() {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.countInt())
                : Aggregator.countInt();
    }

    public Aggregator<Double> averageDouble(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Aggregator<Double> averageDouble(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.averageDouble(column))
                : Aggregator.averageDouble(column);
    }

    public Aggregator<Long> sumLong(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Aggregator<Long> sumLong(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.sumLong(column))
                : Aggregator.sumLong(column);
    }

    public Aggregator<Integer> sumInt(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Aggregator<Integer> sumInt(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.sumInt(column))
                : Aggregator.sumInt(column);
    }

    public Aggregator<Long> maxLong(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxLong(column))
                : Aggregator.maxLong(column);
    }

    public Aggregator<Long> maxLong(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxLong(column))
                : Aggregator.maxLong(column);
    }


    public Aggregator<Long> minLong(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minLong(column))
                : Aggregator.minLong(column);
    }

    public Aggregator<Long> minLong(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minLong(column))
                : Aggregator.minLong(column);
    }

    public Aggregator<Integer> maxInt(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxInt(column))
                : Aggregator.maxInt(column);
    }

    public Aggregator<Integer> maxInt(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxInt(column))
                : Aggregator.maxInt(column);
    }

    public Aggregator<Integer> minInt(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minInt(column))
                : Aggregator.minInt(column);
    }

    public Aggregator<Integer> minInt(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minInt(column))
                : Aggregator.minInt(column);
    }

    public Aggregator<Double> maxDouble(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxDouble(column))
                : Aggregator.maxDouble(column);
    }

    public Aggregator<Double> maxDouble(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.maxDouble(column))
                : Aggregator.maxDouble(column);
    }

    public Aggregator<Double> minDouble(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minDouble(column))
                : Aggregator.minDouble(column);
    }

    public Aggregator<Double> minDouble(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, Aggregator.minDouble(column))
                : Aggregator.minDouble(column);
    }

    protected void appendRowFilter(RowPredicate filter) {
        this.rowFilter = this.rowFilter != null ? this.rowFilter.and(filter) : filter;
    }
}
