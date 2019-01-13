package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.DataRowToValueMapper;
import com.nhl.dflib.map.IndexMapper;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * @see Aggregator for convenience factory methods.
 */
public class ColumnAggregator implements Aggregator {

    private IndexMapper indexMapper;
    private DataRowToValueMapper reader;
    private Collector collector;

    public ColumnAggregator(IndexMapper indexMapper, DataRowToValueMapper reader, Collector collector) {
        this.reader = reader;
        this.collector = collector;
        this.indexMapper = indexMapper;
    }

    public DataRowToValueMapper getReader() {
        return reader;
    }

    public Collector getCollector() {
        return collector;
    }

    public IndexMapper getIndexMapper() {
        return indexMapper;
    }

    @Override
    public Object[] aggregate(DataFrame df) {

        BiConsumer accumulator = collector.accumulator();
        Object accumResult = collector.supplier().get();

        df.consume((ix, r) -> accumulator.accept(accumResult, reader.map(ix, r)));

        Object result = collector.finisher().apply(accumResult);
        return new Object[]{result};
    }

    @Override
    public Index aggregateIndex(Index columns) {
        IndexPosition p = indexMapper.map(columns);
        return Index.withNames(p.name());
    }
}
