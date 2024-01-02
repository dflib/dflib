package org.dflib.exp.flow;


import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Objects;

/**
 * @since 0.11
 */
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
                ifTrueExp.eval(df.selectRows(indexTrue)),
                ifFalseExp.eval(df.selectRows(indexFalse)),
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

    protected Series<T> evalMerge(Series<T> dataIfTrue, Series<T> dataIfFalse, IntSeries indexTrue, IntSeries indexFalse) {

        int st = dataIfTrue.size();
        int sf = dataIfFalse.size();

        // TODO: Use Accumulator, but for this 'Accumulator.set(pos, val)' must auto-expand when position is >= size
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
