package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapConjunctiveConditionN;

/**
 * @since 0.11
 */
public class OrCondition extends MapConjunctiveConditionN {

    public OrCondition(Condition... parts) {
        super("or", parts, BooleanSeries::orAll);
    }

    @Override
    public Condition or(Condition exp) {
        // flatten OR
        return exp.getClass().equals(OrCondition.class)
                ? new OrCondition(combine(this.args, ((OrCondition) exp).args))
                : new OrCondition(combine(this.args, exp));
    }

    @Override
    public int firstMatch(DataFrame df) {
        int len = args.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = args[i].firstMatch(df);
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
            int rx = args[i].firstMatch(s);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }
}
