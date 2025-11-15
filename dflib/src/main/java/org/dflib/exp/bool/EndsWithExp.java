package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class EndsWithExp implements Condition {

    public static Condition of(Exp<?> exp, String prefix) {
        Objects.requireNonNull(prefix, "Null 'prefix'");
        return new EndsWithExp(exp, prefix);
    }

    private final Exp<?> exp;
    private final String suffix;

    protected EndsWithExp(Exp<?> exp, String suffix) {
        this.exp = exp;
        this.suffix = suffix;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(exp.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(exp.eval(s));
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return doEval(Series.ofVal(exp.reduce(df), 1)).get(0);
    }

    @Override
    public Boolean reduce(Series<?> s) {
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

        EndsWithExp exp2 = (EndsWithExp) o;
        return Objects.equals(exp, exp2.exp)
                && Objects.equals(suffix, exp2.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash("endsWith", exp, suffix);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "endsWith(" + exp.toQL() + "," + suffix + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "endsWith(" + exp.toQL(df) + "," + suffix + ")";
    }

    private BooleanSeries doEval(Series<?> data) {
        return data.locate(o -> o != null ? o.toString().endsWith(suffix) : false);
    }
}
