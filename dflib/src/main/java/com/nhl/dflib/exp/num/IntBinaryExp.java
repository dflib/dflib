package com.nhl.dflib.exp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.NumericExp;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @since 0.11
 */
public class IntBinaryExp extends BinaryExp<Integer, Integer, Integer> implements NumericExp<Integer> {

    private final BinaryOperator<IntSeries> primitiveOp;

    protected IntBinaryExp(
            String name,
            Exp<Integer> left,
            Exp<Integer> right,
            BiFunction<Series<Integer>, Series<Integer>, Series<Integer>> op,
            BinaryOperator<IntSeries> primitiveOp) {

        super(name, Integer.class, left, right, op);
        this.primitiveOp = primitiveOp;
    }

    @Override
    protected Series<Integer> eval(Series<Integer> ls, Series<Integer> rs) {
        return (ls instanceof IntSeries && rs instanceof IntSeries)
                ? primitiveOp.apply((IntSeries) ls, (IntSeries) rs)
                : super.eval(ls, rs);
    }
}
