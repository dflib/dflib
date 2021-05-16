package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.exp.map.MapConjunctiveConditionN;

/**
 * @since 0.11
 */
public class AndCondition extends MapConjunctiveConditionN {

    public AndCondition(Condition... parts) {
        super("and", parts, BooleanSeries::andAll);
    }

    @Override
    public Condition and(Condition exp) {
        // flatten AND
        return exp.getClass().equals(AndCondition.class)
                ? new AndCondition(combine(this.args, ((AndCondition) exp).args))
                : new AndCondition(combine(this.args, exp));
    }

    // TODO: an optimized version of "firstMatch" that does partial evaluation of the parts
}
