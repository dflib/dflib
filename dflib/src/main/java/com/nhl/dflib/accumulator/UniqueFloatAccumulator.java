package com.nhl.dflib.accumulator;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueFloatAccumulator extends FloatAccumulator {

    private Set<Float> seen;

    public UniqueFloatAccumulator() {
        this(10);
    }

    public UniqueFloatAccumulator(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void addFloat(float value) {

        if (seen.add(value)) {
            super.addFloat(value);
        }
    }

    @Override
    public void setFloat(int pos, float value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
