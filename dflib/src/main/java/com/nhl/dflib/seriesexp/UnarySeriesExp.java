package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class UnarySeriesExp<F, T> implements SeriesExp<T> {

    private final Function<Series<F>, Series<T>> op;
    private final SeriesExp<F> exp;
    private final Class<T> type;

    public UnarySeriesExp(SeriesExp<F> exp, Class<T> type, Function<Series<F>, Series<T>> op) {
        this.exp = exp;
        this.type = type;
        this.op = op;
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return op.apply(exp.eval(df));
    }

    /**
     * Utility method that converts a Function operating on individual values to a Function operating on object
     * Series. If F is null, the result is assumed to be null, and the "op" function is not invoked.
     */
    public static <F, T> Function<Series<F>, Series<T>> toSeriesOp(Function<F, T> op) {
        return s -> s.map(v -> v != null ? op.apply(v) : null);
    }
}