package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final String opName;
    protected final Condition[] parts;
    private final Function<BooleanSeries[], BooleanSeries> op;

    public ConjunctiveCondition(String opName, Condition[] parts, Function<BooleanSeries[], BooleanSeries> op) {

        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty sub-expressions arrays");
        }

        this.opName = opName;
        this.parts = parts;
        this.op = op;
    }

    @Override
    public String getName() {
        return Arrays.stream(parts).map(p -> p.getName()).collect(Collectors.joining(opName));
    }

    @Override
    public String getName(DataFrame df) {
        return Arrays.stream(parts).map(p -> p.getName(df)).collect(Collectors.joining(opName));
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

    @Override
    public BooleanSeries eval(Series<?> s) {
        int len = parts.length;
        BooleanSeries[] values = new BooleanSeries[len];

        for (int i = 0; i < len; i++) {
            values[i] = parts[i].eval(s);
        }

        return op.apply(values);
    }
}
