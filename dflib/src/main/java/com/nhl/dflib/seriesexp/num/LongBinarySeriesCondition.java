package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class LongBinarySeriesCondition extends BinarySeriesCondition<Long, Long> {

    private final BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp;

    public LongBinarySeriesCondition(
            String name,
            SeriesExp<Long> left,
            SeriesExp<Long> right,
            BiFunction<Series<Long>, Series<Long>, BooleanSeries> op,
            BiFunction<LongSeries, LongSeries, BooleanSeries> primitiveOp) {

        super(name, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries eval(Series<Long> ls, Series<Long> rs) {
        return (ls instanceof LongSeries && rs instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) ls, (LongSeries) rs)
                : super.eval(ls, rs);
    }
}
