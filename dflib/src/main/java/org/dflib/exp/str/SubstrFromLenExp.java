package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class SubstrFromLenExp implements StrExp {

    public static StrExp of(Exp<?> exp, int fromInclusive, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("'len' must be non-negative: " + len);
        } else if (len == 0) {
            return Exp.$strVal("");
        } else {
            return new SubstrFromLenExp(exp, fromInclusive, len);
        }
    }

    private final Exp<?> exp;
    private final int fromInclusive;
    private final int len;

    protected SubstrFromLenExp(Exp<?> exp, int fromInclusive, int len) {
        this.exp = exp;
        this.fromInclusive = fromInclusive;
        this.len = len;
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

        SubstrFromLenExp exp2 = (SubstrFromLenExp) o;
        return Objects.equals(exp, exp2.exp)
                && fromInclusive == exp2.fromInclusive
                && len == exp2.len;
    }

    @Override
    public int hashCode() {
        return Objects.hash("substr", exp, fromInclusive, len);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "substr(" + exp.toQL() + "," + fromInclusive + "," + len + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "substr(" + exp.toQL(df) + "," + fromInclusive + "," + len + ")";
    }

    private Series<String> doEval(Series<?> data) {

        if (fromInclusive < 0) {
            int endOffset = -fromInclusive;

            return data.map(o -> {

                if (o == null) {
                    return null;
                }

                String s = o.toString();
                return s.length() <= endOffset ? "" : s.substring(s.length() - endOffset, Math.min(s.length(), s.length() - endOffset + len));
            });
        } else {

            return data.map(o -> {

                if (o == null) {
                    return null;
                }

                String s = o.toString();
                return s.length() <= fromInclusive ? "" : s.substring(fromInclusive, Math.min(s.length(), fromInclusive + len));
            });
        }
    }
}
