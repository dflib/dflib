package com.nhl.dflib.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueIntMutableList extends IntMutableList {

    private Set<Integer> seen;

    public UniqueIntMutableList() {
        this(10);
    }

    public UniqueIntMutableList(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void add(int value) {

        if (seen.add(value)) {
            super.add(value);
        }
    }
}
