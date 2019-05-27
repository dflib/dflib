package com.nhl.dflib.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.6
 */
public class UniqueDoubleMutableList extends DoubleMutableList {

    private Set<Double> seen;

    public UniqueDoubleMutableList() {
        this(10);
    }

    public UniqueDoubleMutableList(int capacity) {
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
