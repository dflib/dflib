package com.nhl.dflib.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueLongMutableList extends LongMutableList {

    private Set<Long> seen;

    public UniqueLongMutableList() {
        this(10);
    }

    public UniqueLongMutableList(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void add(long value) {

        if (seen.add(value)) {
            super.add(value);
        }
    }
}
