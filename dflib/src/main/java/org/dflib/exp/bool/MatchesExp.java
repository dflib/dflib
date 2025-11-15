package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @since 2.0.0
 */
public class MatchesExp implements Condition {

    public static Condition of(Exp<?> exp, String regex) {
        Objects.requireNonNull(regex, "Null 'regex'");
        return new MatchesExp(exp, regex);
    }

    private final Exp<?> exp;
    private final String regex;
    private final Pattern pattern;

    protected MatchesExp(Exp<?> exp, String regex) {
        this.exp = exp;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
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

        MatchesExp exp2 = (MatchesExp) o;
        return Objects.equals(exp, exp2.exp)
                && Objects.equals(regex, exp2.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash("matches", exp, regex);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "matches(" + exp.toQL() + "," + regex + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "matches(" + exp.toQL(df) + "," + regex + ")";
    }

    private BooleanSeries doEval(Series<?> data) {
        return data.locate(o -> o != null ? pattern.matcher(o.toString()).matches() : false);
    }
}
