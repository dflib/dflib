package org.dflib.exp;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public abstract class ConjunctiveConditionN implements Condition {

    private final String opName;
    protected final Condition[] args;

    protected static Condition[] combine(Condition[] partsLeft, Condition... partsRight) {
        Condition[] combined = new Condition[partsLeft.length + partsRight.length];
        System.arraycopy(partsLeft, 0, combined, 0, partsLeft.length);
        System.arraycopy(partsRight, 0, combined, partsLeft.length, partsRight.length);
        return combined;
    }

    public ConjunctiveConditionN(String opName, Condition[] args) {
        this.opName = opName;
        this.args = args;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return Arrays.stream(args).map(p -> p.toQL()).collect(Collectors.joining(opName));
    }

    @Override
    public String toQL(DataFrame df) {
        return Arrays.stream(args).map(p -> p.toQL(df)).collect(Collectors.joining(opName));
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        int len = args.length;
        BooleanSeries[] values = new BooleanSeries[len];

        for (int i = 0; i < len; i++) {
            values[i] = args[i].eval(df);
        }

        return doEval(values);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        int len = args.length;
        BooleanSeries[] values = new BooleanSeries[len];

        for (int i = 0; i < len; i++) {
            values[i] = args[i].eval(s);
        }

        return doEval(values);
    }

    protected abstract BooleanSeries doEval(BooleanSeries[] parts);
}
