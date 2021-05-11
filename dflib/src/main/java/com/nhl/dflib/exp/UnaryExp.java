package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class UnaryExp<F, T> implements Exp<T> {

    private final Function<Series<F>, Series<T>> op;
    private final Exp<F> exp;
    private final Class<T> type;

    public UnaryExp(Exp<F> exp, Class<T> type, Function<Series<F>, Series<T>> op) {
        this.exp = exp;
        this.type = type;
        this.op = op;
    }

    @Override
    public String getName() {
        return exp.getName();
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

    @Override
    public Series<T> eval(Series<?> s) {
        return op.apply(exp.eval(s));
    }

    /**
     * Utility method that converts a Function operating on individual values to a Function operating on object
     * Series. If F is null, the result is assumed to be null, and the "op" function is not invoked.
     */
    public static <F, T> Function<Series<F>, Series<T>> toSeriesOp(Function<F, T> op) {
        return s -> s.map(v -> v != null ? op.apply(v) : null);
    }
}
