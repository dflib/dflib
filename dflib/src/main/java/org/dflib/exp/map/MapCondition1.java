package org.dflib.exp.map;

import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.BoolAccum;
import org.dflib.exp.Condition1;

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

    /**
     * @since 1.0.0-M19
     */
    public static <F> MapCondition1<F> mapValWithNulls(String opName, Exp<F> exp, Predicate<F> predicate) {
        return new MapCondition1<>(opName, exp, valToSeriesWithNulls(predicate));
    }

    public static <F> MapCondition1<F> mapVal(String opName, Exp<F> exp, Predicate<F> predicate) {
        return new MapCondition1<>(opName, exp, valToSeries(predicate));
    }

    protected static <F> Function<Series<F>, BooleanSeries> valToSeriesWithNulls(Predicate<F> predicate) {
        return s -> {
            int len = s.size();
            BoolAccum accum = new BoolAccum(len);
            for (int i = 0; i < len; i++) {
                F v = s.get(i);
                accum.pushBool(predicate.test(v));
            }

            return accum.toSeries();
        };
    }

    protected static <F> Function<Series<F>, BooleanSeries> valToSeries(Predicate<F> predicate) {
        return s -> {
            int len = s.size();
            BoolAccum accum = new BoolAccum(len);
            for (int i = 0; i < len; i++) {
                F v = s.get(i);
                accum.pushBool(v != null ? predicate.test(v) : false);
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
