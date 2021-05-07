package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * @since 0.7
 */
public class FilteredAggregator<T> implements Aggregator<T> {

    private SeriesCondition rowFilter;
    private Aggregator<T> aggregator;

    public FilteredAggregator(SeriesCondition rowFilter, Aggregator<T> aggregator) {
        this.rowFilter = rowFilter;
        this.aggregator = aggregator;
    }

    @Override
    public T aggregate(DataFrame df) {

        // TODO: we can probably gain significant performance improvements by not creating an intermediate filtered
        //  DataFrame (as most aggregators will only work with a single column)

        return aggregator.aggregate(df.filterRows(rowFilter));
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return aggregator.aggregateLabel(columnIndex);
    }

    @Override
    public Aggregator named(String newAggregateLabel) {
        return new FilteredAggregator<>(rowFilter, aggregator.named(newAggregateLabel));
    }
}
