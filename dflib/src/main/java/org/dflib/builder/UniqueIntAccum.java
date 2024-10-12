package org.dflib.builder;

import java.util.HashSet;
import java.util.Set;

public class UniqueIntAccum extends IntAccum {

    private Set<Integer> seen;

    public UniqueIntAccum() {
        this(10);
    }

    public UniqueIntAccum(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void pushInt(int value) {

        if (seen.add(value)) {
            super.pushInt(value);
        }
    }

    @Override
    public void replaceInt(int pos, int value) {
        throw new UnsupportedOperationException("'set' operation is undefined for unique accumulator");
    }
}
