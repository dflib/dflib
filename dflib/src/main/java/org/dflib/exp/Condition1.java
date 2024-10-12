package org.dflib.exp;


import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;


public abstract class Condition1<F> implements Condition {

    private final String opName;
    protected final Exp<F> exp;

    protected Condition1(String opName, Exp<F> exp) {
        this.opName = opName;
        this.exp = exp;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return opName + exp.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return opName + exp.toQL(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(exp.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(exp.eval(s));
    }

    protected abstract BooleanSeries doEval(Series<F> s);
}
