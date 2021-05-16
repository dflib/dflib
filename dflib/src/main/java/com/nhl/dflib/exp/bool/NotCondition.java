package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.exp.ConjunctiveCondition1;

/**
 * @since 0.11
 */
public class NotCondition extends ConjunctiveCondition1 {

    public NotCondition(Condition arg) {
        super("not", arg);
    }

    @Override
    protected BooleanSeries doEval(BooleanSeries s) {
        return s.not();
    }

    @Override
    public Condition not() {
        return arg;
    }
}
