package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DoubleBinarySeriesCondition extends BinarySeriesCondition<Double, Double> {

    private final BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp;

    public DoubleBinarySeriesCondition(
            String name,
            SeriesExp<Double> left,
            SeriesExp<Double> right,
            BiFunction<Series<Double>, Series<Double>, BooleanSeries> op,
            BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {

        super(name, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries eval(Series<Double> ls, Series<Double> rs) {
        return (ls instanceof DoubleSeries && rs instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) ls, (DoubleSeries) rs)
                : super.eval(ls, rs);
    }
}
