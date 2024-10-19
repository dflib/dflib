package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.ExpN;


public class OrCondition extends ExpN<Boolean> implements Condition {

    private final Condition[] conditionArgs;

    public OrCondition(Condition... args) {
        super("or", Boolean.class, args);
        this.conditionArgs = args;
    }

    @Override
    public Condition or(Condition exp) {
        // flatten OR
        return exp.getClass().equals(OrCondition.class)
                ? new OrCondition(AndCondition.combine(this.args, ((OrCondition) exp).args))
                : new OrCondition(AndCondition.combine(this.args, exp));
    }

    @Override
    public int firstMatch(DataFrame df) {
        int len = args.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = ((Condition) args[i]).firstMatch(df);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }

    @Override
    public int firstMatch(Series<?> s) {
        int len = args.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = ((Condition) args[i]).firstMatch(s);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = conditionArgs[i].eval(s);
        }

        return BooleanSeries.orAll(columns);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = conditionArgs[i].eval(df);
        }

        return BooleanSeries.orAll(columns);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofBool(conditionArgs[i].reduce(s));
        }

        return BooleanSeries.orAll(columns).get(0);
    }

    @Override
    public Boolean reduce(DataFrame df) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofBool(conditionArgs[i].reduce(df));
        }

        return BooleanSeries.orAll(columns).get(0);
    }
}
