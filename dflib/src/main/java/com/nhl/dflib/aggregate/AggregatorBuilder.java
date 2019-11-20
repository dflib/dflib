package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.row.RowProxy;

import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    protected void appendRowFilter(RowPredicate filter) {
        this.rowFilter = this.rowFilter != null ? this.rowFilter.and(filter) : filter;
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(String column) {
        return rowFilter != null
                ? new FirstFilteredAggregator<>(rowFilter, index -> index.position(column), index -> column)
                : Aggregator.first(column);
    }

    /**
     * Returns the first value in an aggregation range.
     */
    public <T> Aggregator<T> first(int column) {
        return rowFilter != null
                ? new FirstFilteredAggregator<>(rowFilter, index -> column, index -> index.getLabel(column))
                : Aggregator.first(column);
    }

    /**
     * Creates an aggregator to count rows
     */
    public Aggregator<Long> countLong() {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, countLongCollector(), index -> "_long_count")
                : Aggregator.countLong();
    }

    /**
     * Creates an aggregator to count rows.
     */
    public Aggregator<Integer> countInt() {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, countIntCollector(), index -> "_int_count")
                : Aggregator.countInt();
    }

    public Aggregator<Long> sumLong(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, sumLongCollector(column), index -> column)
                : Aggregator.sumLong(column);
    }

    public Aggregator<Long> sumLong(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, sumLongCollector(column), index -> index.getLabel(column))
                : Aggregator.sumLong(column);
    }

    public Aggregator<Integer> sumInt(String column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, sumIntCollector(column), index -> column)
                : Aggregator.sumInt(column);
    }

    public Aggregator<Integer> sumInt(int column) {
        return rowFilter != null
                ? new CollectorFilteredAggregator<>(rowFilter, sumIntCollector(column), index -> index.getLabel(column))
                : Aggregator.sumInt(column);
    }

    protected Collector<RowProxy, ?, Long> sumLongCollector(int columnPos) {
        return Collectors.summingLong(r -> ((Number) r.get(columnPos)).longValue());
    }

    protected Collector<RowProxy, ?, Long> sumLongCollector(String columnLabel) {
        return Collectors.summingLong(r -> ((Number) r.get(columnLabel)).longValue());
    }

    protected Collector<RowProxy, ?, Integer> sumIntCollector(int columnPos) {
        return Collectors.summingInt(r -> ((Number) r.get(columnPos)).intValue());
    }

    protected Collector<RowProxy, ?, Integer> sumIntCollector(String columnLabel) {
        return Collectors.summingInt(r -> ((Number) r.get(columnLabel)).intValue());
    }

    protected Collector<RowProxy, ?, Integer> countIntCollector() {
        return Collectors.reducing(0, e -> 1, Integer::sum);
    }

    protected Collector<RowProxy, ?, Long> countLongCollector() {
        return Collectors.reducing(0L, e -> 1L, Long::sum);
    }
}
