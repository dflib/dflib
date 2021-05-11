package com.nhl.dflib.seriesexp.condition;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.SeriesExp;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 0.11
 */
public class BinarySeriesCondition<L, R> implements SeriesCondition {

    private final String opName;
    protected final SeriesExp<L> left;
    protected final SeriesExp<R> right;
    private final BiFunction<Series<L>, Series<R>, BooleanSeries> op;

    public BinarySeriesCondition(String opName, SeriesExp<L> left, SeriesExp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        this.opName = opName;
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String getName() {
        return left.getName() + opName + right.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + opName + right.getName(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return eval(left.eval(df), right.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return eval(left.eval(s), right.eval(s));
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
