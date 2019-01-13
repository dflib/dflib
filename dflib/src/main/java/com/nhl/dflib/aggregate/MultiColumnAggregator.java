package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.stream.Collector;

public class MultiColumnAggregator implements Aggregator {

    private DataRowToValueMapper[] columnReaders;
    private Collector[] aggregators;

    public MultiColumnAggregator(DataRowToValueMapper[] columnReaders, Collector[] aggregators) {
        this.columnReaders = columnReaders;
        this.aggregators = aggregators;
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        final int len = aggregators.length;

        Object[] result = new Object[len];
        Object[] accums = new Object[len];

        for (int i = 0; i < len; i++) {
            accums[i] = aggregators[i].supplier().get();
        }

        df.consume((ix, r) -> {
            for (int i = 0; i < len; i++) {
                aggregators[i].accumulator().accept(accums[i], columnReaders[i].map(ix, r));
            }
        });

        for (int i = 0; i < len; i++) {
            result[i] = aggregators[i].finisher().apply(accums[i]);
        }

        return result;
    }

    @Override
    public Aggregator and(DataRowToValueMapper columnReader, Collector<?, ?, ?> columnAggregator) {
        final int len = aggregators.length;

        DataRowToValueMapper[] columnReaders = new DataRowToValueMapper[len + 1];
        System.arraycopy(this.columnReaders, 0, columnReaders, 0, len);
        columnReaders[len] = columnReader;

        Collector[] aggregators = new Collector[len+1];
        System.arraycopy(this.aggregators, 0, aggregators, 0, len);
        aggregators[len] = columnAggregator;

        return new MultiColumnAggregator(columnReaders, aggregators);
    }
}
