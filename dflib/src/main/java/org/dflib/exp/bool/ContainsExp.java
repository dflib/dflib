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
public class ContainsExp implements Condition {

    public static Condition of(Exp<?> exp, String substring) {
        Objects.requireNonNull(substring, "Null 'substring'");
        return new ContainsExp(exp, substring);
    }

    private final Exp<?> exp;
    private final String substring;

    protected ContainsExp(Exp<?> exp, String substring) {
        this.exp = exp;
        this.substring = substring;
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

        ContainsExp exp2 = (ContainsExp) o;
        return Objects.equals(exp, exp2.exp)
                && Objects.equals(substring, exp2.substring);
    }

    @Override
    public int hashCode() {
        return Objects.hash("contains", exp, substring);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "contains(" + exp.toQL() + "," + substring + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "contains(" + exp.toQL(df) + "," + substring + ")";
    }

    private BooleanSeries doEval(Series<?> data) {
        return data.locate(o -> o != null ? o.toString().contains(substring) : false);
    }
}
