package org.dflib.exp.str;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.bool.TrueCondition;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class StartsWithExp implements Condition {

    public static Condition of(Exp<?> exp, String prefix) {
        Objects.requireNonNull(prefix, "Null 'prefix'");
        return prefix.isEmpty() ? new TrueCondition() : new StartsWithExp(exp, prefix);
    }

    private final Exp<?> exp;
    private final String prefix;

    protected StartsWithExp(Exp<?> exp, String prefix) {
        this.exp = exp;
        this.prefix = prefix;
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

        StartsWithExp exp2 = (StartsWithExp) o;
        return Objects.equals(exp, exp2.exp)
                && Objects.equals(prefix, exp2.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash("startsWith", exp, prefix);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "startsWith(" + exp.toQL() + "," + prefix + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "startsWith(" + exp.toQL(df) + "," + prefix + ")";
    }

    private BooleanSeries doEval(Series<?> data) {
        return data.locate(o -> o != null ? o.toString().startsWith(prefix) : false);
    }
}
