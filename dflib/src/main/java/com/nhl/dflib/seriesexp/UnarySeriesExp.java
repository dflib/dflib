package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class UnarySeriesExp<F, V> implements SeriesExp<V> {

    private final Function<Series<F>, Series<V>> op;
    private final SeriesExp<F> exp;
    private final Class<V> type;

    public UnarySeriesExp(SeriesExp<F> exp, Class<V> type, Function<Series<F>, Series<V>> op) {
        this.exp = exp;
        this.type = type;
        this.op = op;
    }

    @Override
    public String getName() {
        return exp.getName();
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    @Override
    public Series<V> eval(DataFrame df) {
        return op.apply(exp.eval(df));
    }

    /**
     * Utility method that converts a Function operating on individual values to a Function operating on object
     * Series. If F is null, the result is assumed to be null, and the "op" function is not invoked.
     */
    public static <F, V> Function<Series<F>, Series<V>> toSeriesOp(Function<F, V> op) {
        return s -> s.map(v -> v != null ? op.apply(v) : null);
    }
}
