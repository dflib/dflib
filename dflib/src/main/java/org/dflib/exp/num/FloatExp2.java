package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;


public class FloatExp2 extends MapExp2<Float, Float, Float> implements NumExp<Float> {

    public static FloatExp2 mapVal(
            String opName,
            Exp<Float> left,
            Exp<Float> right,
            BiFunction<Float, Float, Float> op,
            BinaryOperator<FloatSeries> primitiveOp) {
        return new FloatExp2(opName, left, right, valToSeries(op), primitiveOp);
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

    @Override
    public NumExp<Float> castAsFloat() {
        return this;
    }
}
