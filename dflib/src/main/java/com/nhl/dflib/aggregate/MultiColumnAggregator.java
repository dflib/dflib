package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

public class MultiColumnAggregator implements Aggregator {

    private ColumnAggregator[] aggregators;

    public MultiColumnAggregator(ColumnAggregator[] aggregators) {
        this.aggregators = aggregators;
    }

    @Override
    public Index aggregateIndex(Index columns) {
        String[] names = new String[aggregators.length];

        for (int i = 0; i < aggregators.length; i++) {
            names[i] = aggregators[i].getLabel(columns);
        }

        // note that 'selectLabels' does name de-duplication
        // TODO: this will blow up with ColumnAggregator.getLabel(..) generates its own label instead of grabbing
        //  the original label from source
        return columns.selectLabels(names);
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        final int len = aggregators.length;

        Object[] result = new Object[len];

        for (int i = 0; i < len; i++) {
            result[i] = aggregators[i].aggregate(df);
        }

        return result;
    }
}
