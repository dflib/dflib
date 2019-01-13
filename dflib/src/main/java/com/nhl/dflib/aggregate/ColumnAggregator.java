package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

public class ColumnAggregator implements Aggregator {

    private DataRowToValueMapper reader;
    private Collector collector;

    public ColumnAggregator(DataRowToValueMapper reader, Collector collector) {
        this.reader = reader;
        this.collector = collector;
    }

    public DataRowToValueMapper getReader() {
        return reader;
    }

    public Collector getCollector() {
        return collector;
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        BiConsumer accumulator = collector.accumulator();
        Object accumResult = collector.supplier().get();


        df.consume((ix, r) -> accumulator.accept(accumResult, reader.map(ix, r)));

        Object result = collector.finisher().apply(accumResult);
        return new Object[]{result};
    }
}
