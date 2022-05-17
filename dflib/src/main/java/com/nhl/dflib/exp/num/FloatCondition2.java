package com.nhl.dflib.exp.num;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.FloatSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapCondition2;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class FloatCondition2 extends MapCondition2<Float, Float> {

    public static FloatCondition2 mapVal(
            String opName,
            Exp<Float> left,
            Exp<Float> right,
            BiPredicate<Float, Float> op,
            BiFunction<FloatSeries, FloatSeries, BooleanSeries> primitiveOp) {
        return new FloatCondition2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BiFunction<FloatSeries, FloatSeries, BooleanSeries> primitiveOp;

    public FloatCondition2(
            String opName,
            Exp<Float> left,
            Exp<Float> right,
            BiFunction<Series<Float>, Series<Float>, BooleanSeries> op,
            BiFunction<FloatSeries, FloatSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Float> left, Series<Float> right) {
        return (left instanceof FloatSeries && right instanceof FloatSeries)
                ? primitiveOp.apply((FloatSeries) left, (FloatSeries) right)
                : super.doEval(left, right);
    }
}
