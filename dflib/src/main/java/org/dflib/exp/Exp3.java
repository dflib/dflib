package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

/**
 * A ternary expression with three {@link Exp} arguments.
 *
 * @since 2.0.0
 */
public abstract class Exp3<One, Two, Three, T> implements Exp<T> {

    private final String opName1;
    private final String opName2;
    private final Class<T> type;
    protected final Exp<One> one;
    protected final Exp<Two> two;
    protected final Exp<Three> three;

    public Exp3(String opName1, String opName2, Class<T> type, Exp<One> one, Exp<Two> two, Exp<Three> three) {
        this.opName1 = opName1;
        this.opName2 = opName2;
        this.type = type;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return one.toQL() + " " + opName1 + " " + two.toQL() + " " + opName2 + " " + three.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return one.toQL(df) + " " + opName1 + " " + two.toQL(df) + " " + opName2 + " " + three.toQL(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(one.eval(df), two.eval(df), three.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(one.eval(s), two.eval(s), three.eval(s));
    }

    protected abstract Series<T> doEval(Series<One> one, Series<Two> two, Series<Three> three);
}
