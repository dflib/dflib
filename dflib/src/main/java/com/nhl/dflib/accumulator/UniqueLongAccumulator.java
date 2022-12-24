package com.nhl.dflib.accumulator;

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
    public void pushLong(long value) {

        if (seen.add(value)) {
            super.pushLong(value);
        }
    }

    @Override
    public void replaceLong(int pos, long value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
