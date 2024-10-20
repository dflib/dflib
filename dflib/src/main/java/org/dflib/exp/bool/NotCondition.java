package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;


public class NotCondition implements Condition {

    private final Condition delegate;

    public NotCondition(Condition delegate) {
        this.delegate = delegate;
    }

    @Override
    public Condition not() {
        return delegate;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "not (" + delegate.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "not (" + delegate.toQL(df) + ")";
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return delegate.eval(df).not();
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return delegate.eval(s).not();
    }
}
