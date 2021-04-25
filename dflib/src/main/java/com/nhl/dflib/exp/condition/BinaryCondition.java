package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class BinaryCondition<L, R> implements Condition {

    private final String name;
    protected final Exp<L> left;
    protected final Exp<R> right;
    private final BiFunction<Series<L>, Series<R>, BooleanSeries> op;

    public BinaryCondition(String name, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        this.name = name;
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return eval(left.eval(df), right.eval(df));
    }

    protected BooleanSeries eval(Series<L> ls, Series<R> rs) {
        return op.apply(ls, rs);
    }

    /**
     * Converts a BiPredicate operating on individual values to a BiFunction operating on the entire Series. If either
     * R or L value is null, the result is automatically assumed to be "false", and the "predicate" is not invoked.
     */
    public static <L, R> BiFunction<Series<L>, Series<R>, BooleanSeries> toSeriesCondition(BiPredicate<L, R> predicate) {
        return (ls, rs) -> {
            int len = ls.size();
            BooleanAccumulator accum = new BooleanAccumulator(len);
            for (int i = 0; i < len; i++) {
                L l = ls.get(i);
                R r = rs.get(i);
                accum.addBoolean(l != null && r != null ? predicate.test(l, r) : false);
            }

            return accum.toSeries();
        };
    }
}
