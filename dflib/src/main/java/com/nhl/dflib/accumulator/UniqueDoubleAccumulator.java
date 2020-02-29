package com.nhl.dflib.accumulator;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueDoubleAccumulator extends DoubleAccumulator {

    private Set<Double> seen;

    public UniqueDoubleAccumulator() {
        this(10);
    }

    public UniqueDoubleAccumulator(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void addDouble(double value) {

        if (seen.add(value)) {
            super.addDouble(value);
        }
    }

    @Override
    public void setDouble(int pos, double value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
