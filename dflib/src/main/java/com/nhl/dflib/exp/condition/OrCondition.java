package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;

/**
 * @since 0.11
 */
public class OrCondition extends ConjunctiveCondition {

    public OrCondition(Condition... parts) {
        super("or", parts, BooleanSeries::orAll);
    }

    @Override
    public Condition or(Condition exp) {
        // flatten OR
        return exp.getClass().equals(OrCondition.class)
                ? new OrCondition(combine(this.parts, ((OrCondition) exp).parts))
                : new OrCondition(combine(this.parts, exp));
    }

    @Override
    public int firstMatch(DataFrame df) {
        int len = parts.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = parts[i].firstMatch(df);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }

    @Override
    public int firstMatch(Series<?> s) {
        int len = parts.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = parts[i].firstMatch(s);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }
}
