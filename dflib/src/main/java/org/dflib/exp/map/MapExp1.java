package org.dflib.exp.map;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

import java.util.function.Function;

/**
 * A unary expression that evaluates parent expression first, and passes the result to a mapping function to
 * produce the final result.
 */
public class MapExp1<F, T> extends Exp1<F, T> {

    public static <F, T> MapExp1<F, T> map(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, Series<T>> op) {
        return new MapExp1<>(opName, type, exp, op);
    }


    public static <F, T> MapExp1<F, T> mapValWithNulls(String opName, Class<T> type, Exp<F> exp, Function<F, T> op) {
        return new MapExp1<>(opName, type, exp, valToSeriesWithNulls(op));
    }

    public static <F, T> MapExp1<F, T> mapVal(String opName, Class<T> type, Exp<F> exp, Function<F, T> op) {
        return new MapExp1<>(opName, type, exp, valToSeries(op));
    }

    protected static <F, T> Function<Series<F>, Series<T>> valToSeriesWithNulls(Function<F, T> op) {
        return s -> s.map(op::apply);
    }

    protected static <F, T> Function<Series<F>, Series<T>> valToSeries(Function<F, T> op) {
        return s -> s.map(v -> v != null ? op.apply(v) : null);
    }

    private final Function<Series<F>, Series<T>> op;

    protected MapExp1(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, Series<T>> op) {
        super(opName, type, exp);
        this.op = op;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return op.apply(exp.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return op.apply(exp.eval(s));
    }

    @Override
    public T reduce(DataFrame df) {
        return op.apply(Series.ofVal(exp.reduce(df), 1)).get(0);
    }

    @Override
    public T reduce(Series<?> s) {
        return op.apply(Series.ofVal(exp.reduce(s), 1)).get(0);
    }
}
