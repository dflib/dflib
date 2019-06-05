package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

public class IntCountAggregator implements Aggregator<Integer> {

    private String targetColumnName;

    public IntCountAggregator(String targetColumnName) {
        this.targetColumnName = targetColumnName;
    }

    @Override
    public Integer aggregate(DataFrame df) {
        return df.height();
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnName;
    }

    @Override
    public Aggregator<Integer> named(String newAggregateLabel) {
        return new IntCountAggregator(newAggregateLabel);
    }
}
