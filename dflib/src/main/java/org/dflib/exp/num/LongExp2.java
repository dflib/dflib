package org.dflib.exp.num;


import org.dflib.exp.map.MapExp2;
import org.dflib.Exp;
import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.Series;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;


public class LongExp2 extends MapExp2<Long, Long, Long> implements NumExp<Long> {

    public static LongExp2 mapVal(
            String opName,
            Exp<Long> left,
            Exp<Long> right,
            BiFunction<Long, Long, Long> op,
            BinaryOperator<LongSeries> primitiveOp) {
        return new LongExp2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BinaryOperator<LongSeries> primitiveOp;

    protected LongExp2(
            String opName,
            Exp<Long> left,
            Exp<Long> right,
            BiFunction<Series<Long>, Series<Long>, Series<Long>> op,
            BinaryOperator<LongSeries> primitiveOp) {

        super(opName, Long.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Long> doEval(Series<Long> left, Series<Long> right) {
        return (left instanceof LongSeries && right instanceof LongSeries)
                ? primitiveOp.apply((LongSeries) left, (LongSeries) right)
                : super.doEval(left, right);
    }

    @Override
    public NumExp<Long> castAsLong() {
        return this;
    }
}
