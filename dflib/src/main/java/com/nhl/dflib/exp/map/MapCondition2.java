package com.nhl.dflib.exp.map;

import com.nhl.dflib.*;
import com.nhl.dflib.builder.BooleanAccum;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.Condition2;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class MapCondition2<L, R> extends Condition2<L, R> {

    private final BiFunction<Series<L>, Series<R>, BooleanSeries> op;

    public static <L, R> MapCondition2<L, R> map(
            String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        return new MapCondition2<>(opName, left, right, op);
    }

    public static <L, R> MapCondition2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiPredicate<L, R> predicate) {
        return new MapCondition2<>(opName, left, right, valToSeries(predicate));
    }

    protected static <L, R> BiFunction<Series<L>, Series<R>, BooleanSeries> valToSeries(BiPredicate<L, R> predicate) {
        return (ls, rs) -> {
            int len = ls.size();
            BooleanAccum accum = new BooleanAccum(len);
            for (int i = 0; i < len; i++) {
                L l = ls.get(i);
                R r = rs.get(i);
                accum.pushBoolean(l != null && r != null ? predicate.test(l, r) : false);
            }

            return accum.toSeries();
        };
    }

    protected MapCondition2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        super(opName, left, right);
        this.op = op;
    }

    @Override
    protected BooleanSeries doEval(Series<L> left, Series<R> right) {
        return op.apply(left, right);
    }

}
