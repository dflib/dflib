package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.exp.ConjunctiveCondition1;


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
