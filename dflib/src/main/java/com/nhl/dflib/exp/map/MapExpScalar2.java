package com.nhl.dflib.exp.map;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.exp.ExpScalar2;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class MapExpScalar2<L, R, T> extends ExpScalar2<L, R, T> {

    private final BiFunction<Series<L>, R, Series<T>> op;

    public static <L, R, T> MapExpScalar2<L, R, T> map(
            String opName, Class<T> type, Exp<L> left, R right, BiFunction<Series<L>, R, Series<T>> op) {
        return new MapExpScalar2<>(opName, type, left, right, op);
    }

    public static <L, R, T> MapExpScalar2<L, R, T> mapVal(String opName, Class<T> type, Exp<L> left, R right, BiFunction<L, R, T> op) {
        return new MapExpScalar2<>(opName, type, left, right, valToSeries(op,type));
    }

    protected static <L, R, T> BiFunction<Series<L>, R, Series<T>> valToSeries(BiFunction<L, R, T> op,Class<T> type) {
        return (left, right) -> {

            if (right == null) {
                return new SingleValueSeries<>(null, left.size());
            }

            int len = left.size();
            Accumulator accum = Accumulator.factory(type, len);
            for (int i = 0; i < len; i++) {
                L l = left.get(i);
                accum.add(l != null ? op.apply(l, right) : null);
            }

            return accum.toSeries();
        };
    }

    protected MapExpScalar2(String opName, Class<T> type, Exp<L> left, R right, BiFunction<Series<L>, R, Series<T>> op) {
        super(opName, type, left, right);
        this.op = op;
    }

    @Override
    protected Series<T> doEval(Series<L> left, R right) {
        return op.apply(left, right);
    }
}
