package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.function.Function;

/**
 * @since 0.6
 */
public class DataFrameAggregator<T> implements Aggregator<T> {

    private Function<DataFrame, T> aggregator;
    private Function<Index, String> targetColumnNamer;

    public DataFrameAggregator(Function<DataFrame, T> aggregator, Function<Index, String> targetColumnNamer) {
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public T aggregate(DataFrame df) {
        return aggregator.apply(df);
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator<T> named(String newAggregateLabel) {
        return new DataFrameAggregator<>(aggregator, i -> newAggregateLabel);
    }
}
