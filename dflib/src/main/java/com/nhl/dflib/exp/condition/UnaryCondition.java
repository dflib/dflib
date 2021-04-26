package com.nhl.dflib.exp.condition;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 0.11
 */
public class UnaryCondition<V> implements Condition {

    private final String name;
    protected final Exp<V> exp;
    private final Function<Series<V>, BooleanSeries> op;

    public UnaryCondition(String name, Exp<V> exp, Function<Series<V>, BooleanSeries> op) {
        this.name = name;
        this.exp = exp;
        this.op = op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return eval(exp.eval(df));
    }

    protected BooleanSeries eval(Series<V> s) {
        return op.apply(s);
    }

    /**
     * Converts a Predicate operating on individual values to a Function operating on the entire Series. If V is null,
     * the result is automatically assumed to be "false", and the "predicate" is not invoked.
     */
    public static <V> Function<Series<V>, BooleanSeries> toSeriesCondition(Predicate<V> predicate) {
        return s -> {
            int len = s.size();
            BooleanAccumulator accum = new BooleanAccumulator(len);
            for (int i = 0; i < len; i++) {
                V v = s.get(i);
                accum.addBoolean(v != null ? predicate.test(v) : false);
            }

            return accum.toSeries();
        };
    }
}
