package org.dflib.exp.agg;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

import java.util.function.BiFunction;

/**
 * @since 1.1.0
 */
public class ExpAggregator2<L, R, T> extends Exp2<L, R, T> {

    private final BiFunction<Series<L>, Series<R>, T> aggregator;

    public ExpAggregator2(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, T> aggregator) {
        super(opName, type, left, right);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<T> doEval(Series<L> left, Series<R> right) {
        T val = aggregator.apply(left, right);
        return Series.ofVal(val, 1);
    }

    @Override
    public String toQL() {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL() + "," + right.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL(df) + "," + right.toQL(df) + ")";
    }
}
