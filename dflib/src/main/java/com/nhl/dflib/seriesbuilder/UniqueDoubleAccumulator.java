package com.nhl.dflib.seriesbuilder;

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
    public void add(double value) {

        if (seen.add(value)) {
            super.add(value);
        }
    }
}
