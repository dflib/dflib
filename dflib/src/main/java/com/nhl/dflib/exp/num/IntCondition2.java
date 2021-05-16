package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.exp.map.MapCondition2;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class IntCondition2 extends MapCondition2<Integer, Integer> {

    public static IntCondition2 mapVal(
            String opName,
            Exp<Integer> left,
            Exp<Integer> right,
            BiPredicate<Integer, Integer> op,
            BiFunction<IntSeries, IntSeries, BooleanSeries> primitiveOp) {
        return new IntCondition2(opName, left, right, valToSeries(op), primitiveOp);
    }

    private final BiFunction<IntSeries, IntSeries, BooleanSeries> primitiveOp;

    public IntCondition2(
            String opName,
            Exp<Integer> left,
            Exp<Integer> right,
            BiFunction<Series<Integer>, Series<Integer>, BooleanSeries> op,
            BiFunction<IntSeries, IntSeries, BooleanSeries> primitiveOp) {

        super(opName, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected BooleanSeries doEval(Series<Integer> left, Series<Integer> right) {
        return (left instanceof IntSeries && right instanceof IntSeries)
                ? primitiveOp.apply((IntSeries) left, (IntSeries) right)
                : super.doEval(left, right);
    }
}
