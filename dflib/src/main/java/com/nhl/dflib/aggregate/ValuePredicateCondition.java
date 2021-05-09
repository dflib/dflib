package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * An adapter from {@link ValuePredicate} to a {@link SeriesCondition}.
 */
@Deprecated
class ValuePredicateCondition<T> implements SeriesCondition {

    private final SeriesExp<T> exp;
    private final ValuePredicate<T> predicate;

    public ValuePredicateCondition(SeriesExp<T> exp, ValuePredicate<T> predicate) {
        this.predicate = predicate;
        this.exp = exp;
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return exp.eval(df).locate(predicate);
    }
}
