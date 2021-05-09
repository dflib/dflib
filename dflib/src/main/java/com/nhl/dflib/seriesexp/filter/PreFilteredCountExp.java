package com.nhl.dflib.seriesexp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
public class PreFilteredCountExp implements SeriesExp<Integer> {

    private final SeriesCondition filter;

    public PreFilteredCountExp(SeriesCondition filter) {
        this.filter = filter;
    }

    @Override
    public String getName(DataFrame df) {
        return "count";
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Series<Integer> eval(DataFrame df) {

        // optmization: not rebuilding a filtered DataFrame ... Just count filter index
        int c = filter.eval(df).countTrue();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        // optmization: not rebuilding a filtered DataFrame ... Just count filter index
        int c = filter.eval(s).countTrue();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }
}
