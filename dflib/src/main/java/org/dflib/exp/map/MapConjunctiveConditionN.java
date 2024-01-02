package org.dflib.exp.map;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.exp.ConjunctiveConditionN;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class MapConjunctiveConditionN extends ConjunctiveConditionN {

    private final Function<BooleanSeries[], BooleanSeries> op;

    public MapConjunctiveConditionN(String opName, Condition[] args, Function<BooleanSeries[], BooleanSeries> op) {
        super(opName, args);
        this.op = op;
    }

    @Override
    protected BooleanSeries doEval(BooleanSeries[] parts) {
        return op.apply(parts);
    }
}
