package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class LongBinarySeriesExp extends BinarySeriesExp<Long, Long, Long> implements NumericSeriesExp<Long> {

    private final BinaryOperator<LongSeries> primitiveOp;

    protected LongBinarySeriesExp(
            String name,
            SeriesExp<Long> left,
            SeriesExp<Long> right,
            BiFunction<Series<Long>, Series<Long>, Series<Long>> op,
            BinaryOperator<LongSeries> primitiveOp) {

        super(name, Long.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Long> eval(Series<Long> ls, Series<Long> rs) {
        return (ls instanceof LongSeries && rs instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) ls, (LongSeries) rs)
                : super.eval(ls, rs);
    }
}
