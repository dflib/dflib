package com.nhl.dflib.seriesexp.compat;

import com.nhl.dflib.*;

/**
 * An adapter from {@link ValuePredicate} to a {@link SeriesCondition}.
 *
 * @since 0.11
 */
// TODO: hopefully this one goes away once we switch everything to the Exp API
public class ValuePredicateCondition<T> implements SeriesCondition {

    private final SeriesExp<T> exp;
    private final ValuePredicate<T> predicate;

    public ValuePredicateCondition(SeriesExp<T> exp, ValuePredicate<T> predicate) {
        this.predicate = predicate;
        this.exp = exp;
    }

    @Override
    public String getName() {
        return exp.getName();
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return exp.eval(df).locate(predicate);
    }
}
