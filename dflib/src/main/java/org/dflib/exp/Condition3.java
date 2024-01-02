package org.dflib.exp;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

/**
 * @since 1.0.0-M19
 */
public abstract class Condition3<One, Two, Three> implements Condition {

    private final String opName1;
    private final String opName2;
    protected final Exp<One> one;
    protected final Exp<Two> two;
    protected final Exp<Three> three;

    public Condition3(String opName1, String opName2, Exp<One> one, Exp<Two> two, Exp<Three> three) {
        this.opName1 = opName1;
        this.opName2 = opName2;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Override
    public String toString() {
        return toQL();
    }

    public String toQL() {
        return one.toQL() + " " + opName1 + " " + two.toQL() + " " + opName2 + " " + three.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return one.toQL(df) + " " + opName1 + " " + two.toQL(df) + " " + opName2 + " " + three.toQL(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(one.eval(df), two.eval(df), three.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(one.eval(s), two.eval(s), three.eval(s));
    }

    protected abstract BooleanSeries doEval(Series<One> one, Series<Two> two, Series<Three> three);
}
