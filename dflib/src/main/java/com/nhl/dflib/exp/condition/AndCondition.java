package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;

/**
 * @since 0.11
 */
public class AndCondition extends ConjunctiveCondition {

    public AndCondition(Condition... parts) {
        super("and", parts, BooleanSeries::andAll);
    }

    @Override
    public Condition and(Condition exp) {
        // flatten AND
        return exp.getClass().equals(AndCondition.class)
                ? new AndCondition(combine(this.parts, ((AndCondition) exp).parts))
                : new AndCondition(combine(this.parts, exp));
    }

    // TODO: an optimized version of "firstMatch" that does partial evaluation of the parts
}
