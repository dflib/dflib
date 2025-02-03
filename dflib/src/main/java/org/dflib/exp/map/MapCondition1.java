package org.dflib.exp.map;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.BoolBuilder;
import org.dflib.exp.Exp1;

import java.util.function.Function;
import java.util.function.Predicate;


public class MapCondition1<F> extends Exp1<F, Boolean> implements Condition {

    public static <F> MapCondition1<F> map(String opName, Exp<F> exp, Function<Series<F>, BooleanSeries> op) {
        return new MapCondition1<>(opName, exp, op);
    }

    public static <F> MapCondition1<F> mapValWithNulls(String opName, Exp<F> exp, Predicate<F> predicate) {
        return new MapCondition1<>(opName, exp, valToSeriesWithNulls(predicate));
    }

    public static <F> MapCondition1<F> mapVal(String opName, Exp<F> exp, Predicate<F> predicate) {
        return new MapCondition1<>(opName, exp, valToSeries(predicate));
    }

    protected static <F> Function<Series<F>, BooleanSeries> valToSeriesWithNulls(Predicate<F> predicate) {
        return s -> BoolBuilder.buildSeries(i -> {
            F v = s.get(i);
            return predicate.test(v);
        }, s.size());
    }

    protected static <F> Function<Series<F>, BooleanSeries> valToSeries(Predicate<F> predicate) {
        return s -> BoolBuilder.buildSeries(i -> {
            F v = s.get(i);
            return v != null && predicate.test(v);
        }, s.size());
    }

    private final Function<Series<F>, BooleanSeries> op;

    protected MapCondition1(String opName, Exp<F> exp, Function<Series<F>, BooleanSeries> op) {
        super(opName, Boolean.class, exp);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return op.apply(exp.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return op.apply(exp.eval(s));
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return op.apply(Series.ofVal(exp.reduce(df), 1)).get(0);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return op.apply(Series.ofVal(exp.reduce(s), 1)).get(0);
    }
}
