package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.6
 * @deprecated since 0.11 replaced by the {@link com.nhl.dflib.Exp} based aggregation API
 */
@Deprecated
public class DataFrameAggregator<T> implements Exp<T> {

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
    public Series<T> eval(Series<?> s) {
        // do not expect to be called on this deprecated class
        throw new UnsupportedOperationException("Unsupported eval with Series... The class is deprecated, consider switching to Exp API");
    }

    @Override
    public String getName() {
        // the class is deprecated and on the way out... this should never get called in reality
        return "col";
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
