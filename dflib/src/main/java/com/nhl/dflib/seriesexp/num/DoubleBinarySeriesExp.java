package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class DoubleBinarySeriesExp extends BinarySeriesExp<Double, Double, Double> implements NumericSeriesExp<Double> {

    private final BinaryOperator<DoubleSeries> primitiveOp;

    protected DoubleBinarySeriesExp(
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
