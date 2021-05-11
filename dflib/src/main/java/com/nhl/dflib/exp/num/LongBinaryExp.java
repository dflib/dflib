package com.nhl.dflib.exp.num;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class LongBinaryExp extends BinarySeriesExp<Long, Long, Long> implements NumericExp<Long> {

    private final BinaryOperator<LongSeries> primitiveOp;

    protected LongBinaryExp(
            String opName,
            SeriesExp<Long> left,
            SeriesExp<Long> right,
            BiFunction<Series<Long>, Series<Long>, Series<Long>> op,
            BinaryOperator<LongSeries> primitiveOp) {

        super(opName, Long.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Long> eval(Series<Long> ls, Series<Long> rs) {
        return (ls instanceof LongSeries && rs instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) ls, (LongSeries) rs)
                : super.eval(ls, rs);
    }
}
