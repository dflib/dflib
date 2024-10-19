package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpN;

import java.util.function.Function;

/**
 * @since 1.1.0
 */
public class ExpAggregatorN<T> extends ExpN<T> {

    private final Function<Series[], T> aggregator;

    public ExpAggregatorN(String opName, Class<T> type, Exp<?>[] args, Function<Series[], T> aggregator) {
        super(opName, type, args);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<T> doEval(int height, Series<?>[] args) {
        T val = aggregator.apply(args);
        return Series.ofVal(val, 1);
    }
}
