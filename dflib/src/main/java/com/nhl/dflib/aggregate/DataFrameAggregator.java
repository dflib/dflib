package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.6
 */
public class DataFrameAggregator<T> implements SeriesExp<T> {

    private Function<DataFrame, T> aggregator;
    private Function<Index, String> targetColumnNamer;

    public DataFrameAggregator(Function<DataFrame, T> aggregator, Function<Index, String> targetColumnNamer) {
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        T val = aggregator.apply(df);
        return new SingleValueSeries<>(val, 1);
    }


    @Override
    public String getName(DataFrame df) {
        return targetColumnNamer.apply(df.getColumnsIndex());
    }

    @Override
    public Class<T> getType() {
        // TODO: ....
        return (Class<T>) Object.class;
    }
}
