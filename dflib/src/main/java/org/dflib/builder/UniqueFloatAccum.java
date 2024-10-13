package org.dflib.builder;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.1.0
 */
public class UniqueFloatAccum extends FloatAccum {

    private Set<Float> seen;

    public UniqueFloatAccum() {
        this(10);
    }

    public UniqueFloatAccum(int capacity) {
        super(capacity);
        this.seen = new HashSet<>(capacity);
    }

    @Override
    public void pushFloat(float value) {

        if (seen.add(value)) {
            super.pushFloat(value);
        }
    }

    @Override
    public void replaceFloat(int pos, float value) {
        throw new UnsupportedOperationException("'replaceFloat' operation is undefined for unique accumulator");
    }
}
