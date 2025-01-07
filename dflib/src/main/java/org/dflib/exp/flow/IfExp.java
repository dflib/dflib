package org.dflib.exp.flow;


import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Objects;


// TODO: this exp doesn't fit our Exp templates,
//  as it mixes condition and expressions and also does lazy eval.
//  Is there a reasonable template for that?
public class IfExp<T> implements Exp<T> {

    private final Condition condition;
    private final Exp<T> ifTrueExp;
    private final Exp<T> ifFalseExp;

    public IfExp(Condition condition, Exp<T> ifTrueExp, Exp<T> ifFalseExp) {
        this.condition = Objects.requireNonNull(condition);
        this.ifTrueExp = Objects.requireNonNull(ifTrueExp);
        this.ifFalseExp = Objects.requireNonNull(ifFalseExp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IfExp<?> ifExp = (IfExp<?>) o;
        return Objects.equals(condition, ifExp.condition)
                && Objects.equals(ifTrueExp, ifExp.ifTrueExp)
                && Objects.equals(ifFalseExp, ifExp.ifFalseExp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, ifTrueExp, ifFalseExp);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return ifTrueExp.getType();
    }

    @Override
    public String toQL() {
        return "if(" + condition.toQL() + "," + ifTrueExp.toQL() + "," + ifFalseExp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "if(" + condition.toQL(df) + "," + ifTrueExp.toQL(df) + "," + ifFalseExp.toQL(df) + ")";
    }

    @Override
    public Series<T> eval(DataFrame df) {

        BooleanSeries mask = condition.eval(df);
        IntSeries indexTrue = mask.indexTrue();

        if (indexTrue.size() == 0) {
            return ifFalseExp.eval(df);
        } else if (indexTrue.size() == df.height()) {
            return ifTrueExp.eval(df);
        }

        IntSeries indexFalse = mask.indexFalse();

        return evalMerge(
                ifTrueExp.eval(df.rows(indexTrue).select()),
                ifFalseExp.eval(df.rows(indexFalse).select()),
                indexTrue,
                indexFalse
        );
    }

    @Override
    public Series<T> eval(Series<?> s) {
        BooleanSeries mask = condition.eval(s);
        IntSeries indexTrue = mask.indexTrue();

        if (indexTrue.size() == 0) {
            return ifFalseExp.eval(s);
        } else if (indexTrue.size() == s.size()) {
            return ifTrueExp.eval(s);
        }

        IntSeries indexFalse = mask.indexFalse();

        return evalMerge(
                ifTrueExp.eval(s.select(indexTrue)),
                ifFalseExp.eval(s.select(indexFalse)),
                indexTrue,
                indexFalse
        );
    }

    @Override
    public T reduce(DataFrame df) {
        throw new UnsupportedOperationException("IF expression '" + getType().getSimpleName() + " does not define a 'reduce' operation");
    }

    @Override
    public T reduce(Series<?> s) {
        throw new UnsupportedOperationException("IF expression '" + getType().getSimpleName() + " does not define a 'reduce' operation");
    }

    protected Series<T> evalMerge(
            Series<T> dataIfTrue,
            Series<T> dataIfFalse,
            IntSeries indexTrue,
            IntSeries indexFalse) {

        int st = dataIfTrue.size();
        int sf = dataIfFalse.size();

        Object[] vals = new Object[st + sf];

        for (int i = 0; i < st; i++) {
            vals[indexTrue.getInt(i)] = dataIfTrue.get(i);
        }

        for (int i = 0; i < sf; i++) {
            vals[indexFalse.getInt(i)] = dataIfFalse.get(i);
        }

        return (Series<T>) Series.of(vals);
    }

}
