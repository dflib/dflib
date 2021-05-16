package com.nhl.dflib.exp.map;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.exp.Condition1;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 0.11
 */
public class MapCondition1<F> extends Condition1<F> {

    private final Function<Series<F>, BooleanSeries> op;

    public static <F> MapCondition1<F> map(String opName, Exp<F> exp, Function<Series<F>, BooleanSeries> op) {
        return new MapCondition1<>(opName, exp, op);
    }

    public static <F> MapCondition1<F> mapVal(String opName, Exp<F> exp, Predicate<F> predicate) {
        return new MapCondition1<>(opName, exp, valToSeries(predicate));
    }

    protected static <F> Function<Series<F>, BooleanSeries> valToSeries(Predicate<F> predicate) {
        return s -> {
            int len = s.size();
            BooleanAccumulator accum = new BooleanAccumulator(len);
            for (int i = 0; i < len; i++) {
                F v = s.get(i);
                accum.addBoolean(v != null ? predicate.test(v) : false);
            }

            return accum.toSeries();
        };
    }

    protected MapCondition1(String opName, Exp<F> exp, Function<Series<F>, BooleanSeries> op) {
        super(opName, exp);
        this.op = op;
    }

    @Override
    protected BooleanSeries doEval(Series<F> s) {
        return op.apply(s);
    }
}
