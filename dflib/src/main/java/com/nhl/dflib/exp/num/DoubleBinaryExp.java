package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.NumericExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class DoubleBinaryExp extends BinaryExp<Double, Double, Double> implements NumericExp<Double> {

    private final BinaryOperator<DoubleSeries> primitiveOp;

    protected DoubleBinaryExp(
            String name,
            Exp<Double> left,
            Exp<Double> right,
            BiFunction<Series<Double>, Series<Double>, Series<Double>> op,
            BinaryOperator<DoubleSeries> primitiveOp) {

        super(name, Double.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Double> eval(Series<Double> ls, Series<Double> rs) {
        return (ls instanceof DoubleSeries && rs instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) ls, (DoubleSeries) rs)
                : super.eval(ls, rs);
    }
}
