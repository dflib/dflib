package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

public class SingleColumnAggregator implements Aggregator {

    private DataRowToValueMapper columnReader;
    private Collector aggregator;

    public SingleColumnAggregator(DataRowToValueMapper columnReader, Collector aggregator) {
        this.columnReader = columnReader;
        this.aggregator = aggregator;
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        Object accum = aggregator.supplier().get();
        BiConsumer accumulator = aggregator.accumulator();

        df.consume((ix, r) -> accumulator.accept(accum, columnReader.map(ix, r)));
        Object result = aggregator.finisher().apply(accum);
        return new Object[]{result};
    }

    @Override
    public Aggregator and(DataRowToValueMapper anotherReader, Collector<?, ?, ?> columnAggregator) {
        DataRowToValueMapper[] columnReaders = new DataRowToValueMapper[]{
                columnReader,
                anotherReader
        };

        Collector[] aggregators = new Collector[]{
                aggregator, columnAggregator
        };

        return new MultiColumnAggregator(columnReaders, aggregators);
    }
}
