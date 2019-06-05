package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

/**
 * @since 0.6
 */
public class LongCountAggregator implements Aggregator<Long> {

    private String targetColumnName;

    public LongCountAggregator(String targetColumnName) {
        this.targetColumnName = targetColumnName;
    }

    @Override
    public Long aggregate(DataFrame df) {
        return Long.valueOf(df.height());
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnName;
    }

    @Override
    public Aggregator<Long> named(String newAggregateLabel) {
        return new LongCountAggregator(newAggregateLabel);
    }
}
