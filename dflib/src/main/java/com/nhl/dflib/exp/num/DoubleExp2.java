package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp2;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class DoubleExp2 extends MapExp2<Double, Double, Double> implements NumExp<Double> {

    public static DoubleExp2 mapVal(
            String opName,
            Exp<Double> left,
            Exp<Double> right,
            BiFunction<Double, Double, Double> op,
            BinaryOperator<DoubleSeries> primitiveOp) {
        return new DoubleExp2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BinaryOperator<DoubleSeries> primitiveOp;

    protected DoubleExp2(
            String opName,
            Exp<Double> left,
            Exp<Double> right,
            BiFunction<Series<Double>, Series<Double>, Series<Double>> op,
            BinaryOperator<DoubleSeries> primitiveOp) {

        super(opName, Double.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Double> doEval(Series<Double> ls, Series<Double> rs) {
        return (ls instanceof DoubleSeries && rs instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) ls, (DoubleSeries) rs)
                : super.doEval(ls, rs);
    }
}
