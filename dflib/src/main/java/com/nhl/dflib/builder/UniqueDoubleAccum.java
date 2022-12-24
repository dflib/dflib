package com.nhl.dflib.builder;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueDoubleAccum extends DoubleAccum {

    private Set<Double> seen;

    public UniqueDoubleAccum() {
        this(10);
    }

    public UniqueDoubleAccum(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void pushDouble(double value) {

        if (seen.add(value)) {
            super.pushDouble(value);
        }
    }

    @Override
    public void replaceDouble(int pos, double value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
