package org.dflib.exp.map;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.BoolBuilder;
import org.dflib.exp.Exp2;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @since 2.0.0
 */
public class MapCondition2<L, R> extends Exp2<L, R, Boolean> implements Condition {

    public static <L, R> MapCondition2<L, R> map(
            String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        return new MapCondition2<>(opName, left, right, op);
    }

    public static <L, R> MapCondition2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiPredicate<L, R> predicate) {
        return new MapCondition2<>(opName, left, right, valToSeries(predicate));
    }

    protected static <L, R> BiFunction<Series<L>, Series<R>, BooleanSeries> valToSeries(BiPredicate<L, R> predicate) {
        return (ls, rs) -> BoolBuilder.buildSeries(i -> {
            L l = ls.get(i);
            R r = rs.get(i);
            return l != null && r != null && predicate.test(l, r);
        }, ls.size());
    }

    private final BiFunction<Series<L>, Series<R>, BooleanSeries> op;

    protected MapCondition2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, BooleanSeries> op) {
        super(opName, Boolean.class, left, right);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(left.eval(df), right.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(left.eval(s), right.eval(s));
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return doEval(
                Series.ofVal(left.reduce(df), 1),
                Series.ofVal(right.reduce(df), 1)
        ).get(0);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return doEval(
                Series.ofVal(left.reduce(s), 1),
                Series.ofVal(right.reduce(s), 1)
        ).get(0);
    }

    protected BooleanSeries doEval(Series<L> left, Series<R> right) {
        return op.apply(left, right);
    }
}
