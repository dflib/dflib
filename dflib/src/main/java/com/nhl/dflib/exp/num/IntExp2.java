package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.exp.map.MapExp2;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class IntExp2 extends MapExp2<Integer, Integer, Integer> implements NumExp<Integer> {

    public static IntExp2 mapVal(
            String opName,
            Exp<Integer> left,
            Exp<Integer> right,
            BiFunction<Integer, Integer, Integer> op,
            BinaryOperator<IntSeries> primitiveOp) {
        return new IntExp2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BinaryOperator<IntSeries> primitiveOp;

    protected IntExp2(
            String opName,
            Exp<Integer> left,
            Exp<Integer> right,
            BiFunction<Series<Integer>, Series<Integer>, Series<Integer>> op,
            BinaryOperator<IntSeries> primitiveOp) {

        super(opName, Integer.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Integer> doEval(Series<Integer> ls, Series<Integer> rs) {
        return (ls instanceof IntSeries && rs instanceof IntSeries)
                ? primitiveOp.apply((IntSeries) ls, (IntSeries) rs)
                : super.doEval(ls, rs);
    }
}
