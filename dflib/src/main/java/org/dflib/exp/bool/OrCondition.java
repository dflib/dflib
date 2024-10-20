package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.ExpN;


public class OrCondition extends ExpN<Boolean> implements Condition {

    public OrCondition(Condition... args) {
        super("or", Boolean.class, args);
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
            // we can safely do this cast, as constructor only allows args that are BooleanSeries
            columns[i] = ((Condition) args[i]).eval(s);
        }

        return doEval(s.size(), columns);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        int w = args.length;
        BooleanSeries[] columns = new BooleanSeries[w];
        for (int i = 0; i < w; i++) {
            // we can safely do this cast, as constructor only allows args that are BooleanSeries
            columns[i] = ((Condition) args[i]).eval(df);
        }

        return doEval(df.height(), columns);
    }

    @Override
    protected BooleanSeries doEval(int height, Series<?>[] args) {
        // we can safely do this cast, as eval(..) always passes BooleanSeries
        return BooleanSeries.orAll((BooleanSeries[]) args);
    }
}
