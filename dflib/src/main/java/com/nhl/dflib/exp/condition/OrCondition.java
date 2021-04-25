package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
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


}
