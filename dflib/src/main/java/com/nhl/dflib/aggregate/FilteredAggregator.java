package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowPredicate;

/**
 * @since 0.7
 */
public class FilteredAggregator<T> implements Aggregator<T> {

    private RowPredicate rowFilter;
    private Aggregator<T> aggregator;

    public FilteredAggregator(RowPredicate rowFilter, Aggregator<T> aggregator) {
        this.rowFilter = rowFilter;
        this.aggregator = aggregator;
    }

    @Override
    public T aggregate(DataFrame df) {

        // TODO: we can probably gain significant performance improvements by not creating an intermediate filtered
        //  DataFrame (as most aggregators will only work with a single column).. At the same time aggregating over
        //  an Iterator<RowProxy> doesn't allow to reuse Series Aggregators... Need to find a well-performing solution,
        //  perhaps a lazily-resolved filtered DataFrame
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
