package com.nhl.dflib.series.builder;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueLongAccumulator extends LongAccumulator {

    private Set<Long> seen;

    public UniqueLongAccumulator() {
        this(10);
    }

    public UniqueLongAccumulator(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void addLong(long value) {

        if (seen.add(value)) {
            super.addLong(value);
        }
    }

    @Override
    public void setLong(int pos, long value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
