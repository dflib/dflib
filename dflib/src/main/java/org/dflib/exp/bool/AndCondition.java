package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.exp.map.MapConjunctiveConditionN;
import org.dflib.exp.ConjunctiveConditionN;

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
                ? new AndCondition(ConjunctiveConditionN.combine(this.args, ((AndCondition) exp).args))
                : new AndCondition(ConjunctiveConditionN.combine(this.args, exp));
    }

    // TODO: an optimized version of "firstMatch" that does partial evaluation of the parts
}
