package com.nhl.dflib.exp.map;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.exp.Exp2;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class MapExp2<L, R, T> extends Exp2<L, R, T> {

    private final BiFunction<Series<L>, Series<R>, Series<T>> op;

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
            ObjectAccumulator<T> accum = new ObjectAccumulator<>(len);
            for (int i = 0; i < len; i++) {
                L l = ls.get(i);
                R r = rs.get(i);
                accum.add(l != null && r != null ? op.apply(l, r) : null);
            }

            return accum.toSeries();
        };
    }

    protected MapExp2(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<T>> op) {
        super(opName, type, left, right);
        this.op = op;
    }

    @Override
    protected Series<T> doEval(Series<L> left, Series<R> right) {
        return op.apply(left, right);
    }
}
