package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapCondition2;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;


public class DoubleCondition2 extends MapCondition2<Double, Double> {

    public static DoubleCondition2 mapVal(
            String opName,
            Exp<Double> left,
            Exp<Double> right,
            BiPredicate<Double, Double> op,
            BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {
        return new DoubleCondition2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp;

    public DoubleCondition2(
            String opName,
            Exp<Double> left,
            Exp<Double> right,
            BiFunction<Series<Double>, Series<Double>, BooleanSeries> op,
            BiFunction<DoubleSeries, DoubleSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Double> left, Series<Double> right) {
        return (left instanceof DoubleSeries && right instanceof DoubleSeries)
                ? primitiveOp.apply((DoubleSeries) left, (DoubleSeries) right)
                : super.doEval(left, right);
    }
}
