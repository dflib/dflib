package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpN;

public class AndCondition extends ExpN<Boolean> implements Condition {

    static Condition[] combine(Exp<?>[] partsLeft, Exp<?>... partsRight) {
        Condition[] combined = new Condition[partsLeft.length + partsRight.length];
        System.arraycopy(partsLeft, 0, combined, 0, partsLeft.length);
        System.arraycopy(partsRight, 0, combined, partsLeft.length, partsRight.length);
        return combined;
    }

    private final Condition[] conditionArgs;

    public AndCondition(Condition... args) {
        super("and", Boolean.class, args);
        this.conditionArgs = args;
    }

    @Override
    public Condition and(Condition exp) {
        // flatten AND
        return exp.getClass().equals(AndCondition.class)
                ? new AndCondition(AndCondition.combine(this.args, ((AndCondition) exp).args))
                : new AndCondition(AndCondition.combine(this.args, exp));
    }

    // TODO: an optimized version of "firstMatch" that does partial evaluation of the parts

    @Override
    public BooleanSeries eval(Series<?> s) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = conditionArgs[i].eval(s);
        }

        return BooleanSeries.andAll(columns);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = conditionArgs[i].eval(df);
        }

        return BooleanSeries.andAll(columns);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofBool(conditionArgs[i].reduce(s));
        }

        return BooleanSeries.andAll(columns).get(0);
    }

    @Override
    public Boolean reduce(DataFrame df) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofBool(conditionArgs[i].reduce(df));
        }

        return BooleanSeries.andAll(columns).get(0);
    }
}
