package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class BinaryExp<L, R, T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;
    private final Exp<L> left;
    private final Exp<R> right;
    private final BiFunction<Series<L>, Series<R>, Series<T>> op;

    public BinaryExp(
            String opName,
            Class<T> type,
            Exp<L> left,
            Exp<R> right,
            BiFunction<Series<L>, Series<R>, Series<T>> op) {
        this.opName = opName;
        this.type = type;
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return left.getName() + " " + opName + " " + right.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + " " + opName + " " + right.getName(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return eval(left.eval(df), right.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return eval(left.eval(s), right.eval(s));
    }

    protected Series<T> eval(Series<L> ls, Series<R> rs) {
        return op.apply(ls, rs);
    }

    /**
     * Utility method that converts a BiFunction operating on individual values to a BiFunction operating on object
     * series. If either R or L value is null, the result is assumed to be null, and the "op" function is not invoked.
     */
    public static <L, R, V> BiFunction<Series<L>, Series<R>, Series<V>> toSeriesOp(BiFunction<L, R, V> op) {
        return (ls, rs) -> {
            int len = ls.size();
            ObjectAccumulator<V> accum = new ObjectAccumulator<>(len);
            for (int i = 0; i < len; i++) {
                L l = ls.get(i);
                R r = rs.get(i);
                accum.add(l != null && r != null ? op.apply(l, r) : null);
            }

            return accum.toSeries();
        };
    }
}
