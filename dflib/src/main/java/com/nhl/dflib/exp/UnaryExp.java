package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class UnaryExp<F, T> implements Exp<T> {

    private final String opName;
    private final Function<Series<F>, Series<T>> op;
    private final Exp<F> exp;
    private final Class<T> type;

    public UnaryExp(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, Series<T>> op) {
        this.opName = opName;
        this.exp = exp;
        this.type = type;
        this.op = op;
    }

    @Override
    public String toString() {
        return getName();
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
     * Creates a function operating on object Series from a Function operating on individual values. If F is null,
     * the result is automatically assumed to be null, and the "op" function is not invoked.
     */
    public static <F, T> Function<Series<F>, Series<T>> toSeriesOp(Function<F, T> op) {
        return s -> s.map(v -> v != null ? op.apply(v) : null);
    }
}
