package com.nhl.dflib.seriesexp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.SeriesExp;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 0.11
 */
public class UnarySeriesCondition<V> implements SeriesCondition {

    private final String opName;
    protected final SeriesExp<V> exp;
    private final Function<Series<V>, BooleanSeries> op;

    public UnarySeriesCondition(String opName, SeriesExp<V> exp, Function<Series<V>, BooleanSeries> op) {
        this.opName = opName;
        this.exp = exp;
        this.op = op;
    }

    @Override
    public String getName() {
        return opName + exp.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return opName + exp.getName(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return op.apply(exp.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return op.apply(exp.eval(s));
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
