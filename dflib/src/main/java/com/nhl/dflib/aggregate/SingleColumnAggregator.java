package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

/**
 * @since 0.6
 */
public class SingleColumnAggregator implements Aggregator {

    private ColumnAggregator aggregator;

    public SingleColumnAggregator(ColumnAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public Index aggregateIndex(Index columns) {
        return Index.forLabels(aggregator.getLabel(columns));
    }

    @Override
    public Object[] aggregate(DataFrame df) {
        return new Object[]{aggregator.aggregate(df)};
    }
}
