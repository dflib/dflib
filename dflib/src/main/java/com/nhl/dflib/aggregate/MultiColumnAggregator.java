package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.row.RowProxy;

import java.util.function.BiConsumer;

public class MultiColumnAggregator implements Aggregator {

    private ColumnAggregator[] aggregators;

    public MultiColumnAggregator(ColumnAggregator[] aggregators) {
        this.aggregators = aggregators;
    }

    @Override
    public Index aggregateIndex(Index columns) {
        String[] names = new String[aggregators.length];

        for (int i = 0; i < aggregators.length; i++) {
            names[i] = aggregators[i].getIndexMapper().map(columns);
        }

        // * note that 'selectLabels' does name deduplication
        return columns.selectLabels(names);
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        final int len = aggregators.length;

        Object[] result = new Object[len];
        Object[] accumResults = new Object[len];

        // cache accumulators invoked in the inner loop
        BiConsumer[] accums = new BiConsumer[len];

        for (int i = 0; i < len; i++) {
            accums[i] = aggregators[i].getCollector().accumulator();
        }

        for (int i = 0; i < len; i++) {
            accumResults[i] = aggregators[i].getCollector().supplier().get();
        }

        for(RowProxy r : df) {
            for (int i = 0; i < len; i++) {
                accums[i].accept(accumResults[i], aggregators[i].getReader().map(r));
            }
        }

        for (int i = 0; i < len; i++) {
            result[i] = aggregators[i].getCollector().finisher().apply(accumResults[i]);
        }

        return result;
    }
}
