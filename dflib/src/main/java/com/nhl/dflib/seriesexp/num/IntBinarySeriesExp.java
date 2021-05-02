package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class IntBinarySeriesExp extends BinarySeriesExp<Integer, Integer, Integer> implements NumericSeriesExp<Integer> {

    private final BinaryOperator<IntSeries> primitiveOp;

    protected IntBinarySeriesExp(
            String name,
            SeriesExp<Integer> left,
            SeriesExp<Integer> right,
            BiFunction<Series<Integer>, Series<Integer>, Series<Integer>> op,
            BinaryOperator<IntSeries> primitiveOp) {

        super(name, Integer.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Integer> eval(Series<Integer> ls, Series<Integer> rs) {
        return (ls instanceof IntSeries && rs instanceof IntSeries)
                ? primitiveOp.apply((IntSeries) ls, (IntSeries) rs)
                : super.eval(ls, rs);
    }
}
