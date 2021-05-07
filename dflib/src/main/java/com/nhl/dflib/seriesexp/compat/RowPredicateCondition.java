package com.nhl.dflib.seriesexp.compat;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;

/**
 * An adapter from {@link RowPredicate} to a {@link SeriesCondition}.
 *
 * @since 0.11
 */
// TODO: hopefully this one goes away once we switch everything to the Exp API
public class RowPredicateCondition implements SeriesCondition {

    private final RowPredicate predicate;

    public RowPredicateCondition(RowPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public String getName() {
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
}
