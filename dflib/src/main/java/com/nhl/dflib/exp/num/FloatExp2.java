package com.nhl.dflib.exp.num;

import com.nhl.dflib.FloatSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp2;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class FloatExp2 extends MapExp2<Float, Float, Float> implements NumExp<Float> {

    public static FloatExp2 mapVal(
            String opName,
            Exp<Float> left,
            Exp<Float> right,
            BiFunction<Float, Float, Float> op,
            BinaryOperator<FloatSeries> primitiveOp) {
        return new FloatExp2(opName, left, right, valToSeries(op,Float.class), primitiveOp);
    }

    private final BinaryOperator<FloatSeries> primitiveOp;

    protected FloatExp2(
            String opName,
            Exp<Float> left,
            Exp<Float> right,
            BiFunction<Series<Float>, Series<Float>, Series<Float>> op,
            BinaryOperator<FloatSeries> primitiveOp) {

        super(opName, Float.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Float> doEval(Series<Float> ls, Series<Float> rs) {
        return (ls instanceof FloatSeries && rs instanceof FloatSeries)
                ? primitiveOp.apply((FloatSeries) ls, (FloatSeries) rs)
                : super.doEval(ls, rs);
    }
}
