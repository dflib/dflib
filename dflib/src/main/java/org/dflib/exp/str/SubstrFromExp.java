package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class SubstrFromExp implements StrExp {

    public static StrExp of(Exp<?> exp, int fromInclusive) {
        return (fromInclusive == 0) ? exp.castAsStr() : new SubstrFromExp(exp, fromInclusive);
    }

    private final Exp<?> exp;
    private final int fromInclusive;

    protected SubstrFromExp(Exp<?> exp, int fromInclusive) {
        this.exp = exp;
        this.fromInclusive = fromInclusive;
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

        SubstrFromExp exp2 = (SubstrFromExp) o;
        return Objects.equals(exp, exp2.exp)
                && fromInclusive == exp2.fromInclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash("substr", exp, fromInclusive);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "substr(" + exp.toQL() + "," + fromInclusive + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "substr(" + exp.toQL(df) + "," + fromInclusive + ")";
    }

    private Series<String> doEval(Series<?> data) {

        if (fromInclusive < 0) {
            int endOffset = -fromInclusive;
            return data.map(o -> {

                if (o == null) {
                    return null;
                }

                String s = o.toString();
                return s.length() <= endOffset ? "" : s.substring(s.length() - endOffset);
            });
        } else if (fromInclusive > 0) {
            return data.map(o -> {

                if (o == null) {
                    return null;
                }

                String s = o.toString();
                return s.length() <= fromInclusive ? "" : s.substring(fromInclusive);
            });
        }
        // note that this branch is effectively eliminated by the factory method
        else {
            return String.class.equals(data.getNominalType()) ? (Series<String>) data : data.map(o -> o != null ? o.toString() : null);
        }
    }
}
