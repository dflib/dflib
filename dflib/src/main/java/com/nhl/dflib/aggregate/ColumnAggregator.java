package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.IndexMapper;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.ArrayRowProxy;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * @see Aggregator for convenience factory methods.
 */
public class ColumnAggregator implements Aggregator {

    private IndexMapper indexMapper;
    private RowToValueMapper reader;
    private Collector collector;

    public ColumnAggregator(IndexMapper indexMapper, RowToValueMapper reader, Collector collector) {
        this.reader = reader;
        this.collector = collector;
        this.indexMapper = indexMapper;
    }

    public RowToValueMapper getReader() {
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

        ArrayRowProxy rowProxy = new ArrayRowProxy(df.getColumns());
        df.consume((ix, r) -> accumulator.accept(accumResult, reader.map(rowProxy.reset(r))));

        Object result = collector.finisher().apply(accumResult);
        return new Object[]{result};
    }

    @Override
    public Index aggregateIndex(Index columns) {
        IndexPosition p = indexMapper.map(columns);
        return Index.withNames(p.name());
    }
}
