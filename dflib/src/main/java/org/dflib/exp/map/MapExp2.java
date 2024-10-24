package org.dflib.exp.map;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.exp.Exp2;

import java.util.function.BiFunction;


public class MapExp2<L, R, T> extends Exp2<L, R, T> {

    public static <L, R, T> MapExp2<L, R, T> map(
            String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<T>> op) {
        return new MapExp2<>(opName, type, left, right, op);
    }

    public static <L, R, T> MapExp2<L, R, T> mapVal(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<L, R, T> op) {
        return new MapExp2<>(opName, type, left, right, valToSeries(op));
    }

    protected static <L, R, T> BiFunction<Series<L>, Series<R>, Series<T>> valToSeries(BiFunction<L, R, T> op) {
        return (ls, rs) -> {
            int len = ls.size();
            ObjectAccum<T> accum = new ObjectAccum<>(len);
            for (int i = 0; i < len; i++) {
                L l = ls.get(i);
                R r = rs.get(i);
                accum.push(l != null && r != null ? op.apply(l, r) : null);
            }

            return accum.toSeries();
        };
    }

    private final BiFunction<Series<L>, Series<R>, Series<T>> op;

    protected MapExp2(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<T>> op) {
        super(opName, type, left, right);
        this.op = op;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(left.eval(df), right.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(left.eval(s), right.eval(s));
    }

    @Override
    public T reduce(DataFrame df) {
        return doEval(
                Series.ofVal(left.reduce(df), 1),
                Series.ofVal(right.reduce(df), 1)
        ).get(0);
    }

    @Override
    public T reduce(Series<?> s) {
        return doEval(
                Series.ofVal(left.reduce(s), 1),
                Series.ofVal(right.reduce(s), 1)
        ).get(0);
    }

    protected Series<T> doEval(Series<L> left, Series<R> right) {
        return op.apply(left, right);
    }
}
