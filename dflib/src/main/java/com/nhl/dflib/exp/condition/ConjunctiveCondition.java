package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Condition;

import java.util.function.Function;

/**
 * A condition that joins a number of other conditions.
 *
 * @since 0.11
 */
public abstract class ConjunctiveCondition implements Condition {

    protected static Condition[] combine(Condition[] partsLeft, Condition... partsRight) {
        Condition[] combined = new Condition[partsLeft.length + partsRight.length];
        System.arraycopy(partsLeft, 0, combined, 0, partsLeft.length);
        System.arraycopy(partsRight, 0, combined, partsLeft.length, partsRight.length);
        return combined;
    }

    private final String name;
    protected final Condition[] parts;
    private final Function<BooleanSeries[], BooleanSeries> op;

    public ConjunctiveCondition(String name, Condition[] parts, Function<BooleanSeries[], BooleanSeries> op) {

        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty sub-expressions arrays");
        }

        this.name = name;
        this.parts = parts;
        this.op = op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        int len = parts.length;
        BooleanSeries[] values = new BooleanSeries[len];

        for (int i = 0; i < len; i++) {
            values[i] = parts[i].eval(df);
        }

        return op.apply(values);
    }
}
