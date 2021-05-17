package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public abstract class ConjunctiveCondition1 implements Condition {

    private final String opName;
    protected final Condition arg;

    public ConjunctiveCondition1(String opName, Condition arg) {
        this.opName = opName;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return opName + "(" + arg.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return opName + "(" + arg.toQL(df) + ")";
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(arg.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(arg.eval(s));
    }

    protected abstract BooleanSeries doEval(BooleanSeries s);
}
