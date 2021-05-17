package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * An adapter from {@link ValuePredicate} to a {@link Condition}.
 */
@Deprecated
class ValuePredicateCondition<T> implements Condition {

    private final Exp<T> exp;
    private final ValuePredicate<T> predicate;

    public ValuePredicateCondition(Exp<T> exp, ValuePredicate<T> predicate) {
        this.predicate = predicate;
        this.exp = exp;
    }

    @Override
    public String toQL() {
        return exp.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return exp.toQL(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return exp.eval(df).locate(predicate);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        // do not expect to be called on this deprecated class
        throw new UnsupportedOperationException("Unsupported eval with Series... The class is deprecated, consider switching to Exp API");
    }
}
