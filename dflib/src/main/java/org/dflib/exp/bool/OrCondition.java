package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.map.MapConjunctiveConditionN;
import org.dflib.exp.ConjunctiveConditionN;


public class OrCondition extends MapConjunctiveConditionN {

    public OrCondition(Condition... parts) {
        super("or", parts, BooleanSeries::orAll);
    }

    @Override
    public Condition or(Condition exp) {
        // flatten OR
        return exp.getClass().equals(OrCondition.class)
                ? new OrCondition(ConjunctiveConditionN.combine(this.args, ((OrCondition) exp).args))
                : new OrCondition(ConjunctiveConditionN.combine(this.args, exp));
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
