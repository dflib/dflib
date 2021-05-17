package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;

/**
 * An adapter from {@link RowPredicate} to a {@link Condition}.
 */
@Deprecated
class RowPredicateCondition implements Condition {

    private final RowPredicate predicate;

    public RowPredicateCondition(RowPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toQL() {
        return "row_predicate";
    }

    @Override
    public String toQL(DataFrame df) {
        return "row_predicate";
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        // TODO: no "DataFrame.locate()" method exists (like it does in Series), so implementing it on the spot
        int len = df.height();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        df.forEach(r -> {
            matches.addBoolean(predicate.test(r));
        });

        return matches.toSeries();
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        // do not expect to be called on this deprecated class
        throw new UnsupportedOperationException("Unsupported eval with Series... The class is deprecated, consider switching to Exp API");
    }
}
