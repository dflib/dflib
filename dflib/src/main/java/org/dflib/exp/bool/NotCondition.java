package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;

import java.util.Objects;

public class NotCondition implements Condition {

    private final Condition exp;

    public NotCondition(Condition exp) {
        this.exp = exp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotCondition that = (NotCondition) o;
        return Objects.equals(exp, that.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(exp);
    }

    @Override
    public Condition not() {
        return exp;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return "not (" + exp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "not (" + exp.toQL(df) + ")";
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return exp.eval(df).not();
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return exp.eval(s).not();
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return !exp.reduce(df);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return !exp.reduce(s);
    }
}
