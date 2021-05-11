package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class DoubleBinaryExp extends BinarySeriesExp<Double, Double, Double> implements NumericExp<Double> {

    private final BinaryOperator<DoubleSeries> primitiveOp;

    protected DoubleBinaryExp(
            String opName,
            SeriesExp<Double> left,
            SeriesExp<Double> right,
            BiFunction<Series<Double>, Series<Double>, Series<Double>> op,
            BinaryOperator<DoubleSeries> primitiveOp) {

        super(opName, Double.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Double> eval(Series<Double> ls, Series<Double> rs) {
        return (ls instanceof DoubleSeries && rs instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) ls, (DoubleSeries) rs)
                : super.eval(ls, rs);
    }
}
