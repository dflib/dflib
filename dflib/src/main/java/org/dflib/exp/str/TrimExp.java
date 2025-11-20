package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class TrimExp implements StrExp {

    public static StrExp of(Exp<?> exp) {
        return new TrimExp(exp);
    }

    private final Exp<?> exp;

    protected TrimExp(Exp<?> exp) {
        this.exp = exp;
    }

    @Override
    public Series<String> eval(DataFrame df) {
        return doEval(exp.eval(df));
    }

    @Override
    public Series<String> eval(Series<?> s) {
        return doEval(exp.eval(s));
    }

    @Override
    public String reduce(DataFrame df) {
        return doEval(Series.ofVal(exp.reduce(df), 1)).get(0);
    }

    @Override
    public String reduce(Series<?> s) {
        return doEval(Series.ofVal(exp.reduce(s), 1)).get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrimExp exp2 = (TrimExp) o;
        return Objects.equals(exp, exp2.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash("trim", exp);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "trim(" + exp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "trim(" + exp.toQL(df) + ")";
    }

    private Series<String> doEval(Series<?> data) {
        return data.map(o -> o != null ? o.toString().trim() : null);
    }
}
