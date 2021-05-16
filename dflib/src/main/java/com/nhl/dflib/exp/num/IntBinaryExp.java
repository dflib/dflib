package com.nhl.dflib.exp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.BinaryExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class IntBinaryExp extends BinaryExp<Integer, Integer, Integer> implements NumExp<Integer> {

    private final BinaryOperator<IntSeries> primitiveOp;

    protected IntBinaryExp(
            String opName,
            Exp<Integer> left,
            Exp<Integer> right,
            BiFunction<Series<Integer>, Series<Integer>, Series<Integer>> op,
            BinaryOperator<IntSeries> primitiveOp) {

        super(opName, Integer.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Integer> eval(Series<Integer> ls, Series<Integer> rs) {
        return (ls instanceof IntSeries && rs instanceof IntSeries)
                ? primitiveOp.apply((IntSeries) ls, (IntSeries) rs)
                : super.eval(ls, rs);
    }
}
