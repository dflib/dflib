package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class BinaryExp<L, R, V> implements Exp<V> {

    private final String name;
    private final Class<V> type;
    private final Exp<L> left;
    private final Exp<R> right;
    private final BiFunction<Series<L>, Series<R>, Series<V>> op;

    public BinaryExp(
            String name,
            Class<V> type,
            Exp<L> left,
            Exp<R> right,
            BiFunction<Series<L>, Series<R>, Series<V>> op) {
        this.name = name;
        this.type = type;
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    @Override
    public Series<V> eval(DataFrame df) {
        return eval(left.eval(df), right.eval(df));
    }

    protected Series<V> eval(Series<L> ls, Series<R> rs) {
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
