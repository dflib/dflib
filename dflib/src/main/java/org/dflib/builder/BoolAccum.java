package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.series.BooleanBitsetSeries;
import org.dflib.series.BitSet;

public class BoolAccum implements ValueAccum<Boolean> {

    private final java.util.BitSet bitset;
    private int size;

    public BoolAccum() {
        bitset = new java.util.BitSet(64);
    }

    public BoolAccum(int capacity) {
        bitset = new java.util.BitSet(capacity);
    }

    public void fill(BooleanSeries values, int fromOffset, int toOffset, int len) {
        if (len <= 0) {
            return;
        }

        for (int i = 0; i < len; i++) {
            replaceBool(toOffset + i, values.getBool(fromOffset + i));
        }
    }

    public void fill(int from, int to, boolean value) {
        for (int i = from; i < to; i++) {
            replaceBool(i, value);
        }
    }

    @Override
    public void push(Boolean v) {
        pushBool(v != null ? v : false);
    }

    @Override
    public void pushBool(boolean v) {
        if (v) {
            bitset.set(size);
        }
        size++;
    }

    @Override
    public void replace(int pos, Boolean v) {
        replaceBool(pos,  v != null ? v : false);
    }

    @Override
    public void replaceBool(int pos, boolean v) {
        if (pos >= size) {
            size = pos + 1;
        }

        if (v) {
            bitset.set(pos);
        } else {
            bitset.clear(pos);
        }
    }

    @Override
    public BooleanBitsetSeries toSeries() {
        BitSet bitSet = size == 0
                ? BitSet.EMPTY

                // TODO: avoid this copy. Build the "long[]" directly in the Accum without relying on the Java BitSet
                //   We can use a similar pattern for expansion of the long[] as we have for instance in ObjectAccum
                : new BitSet(bitset.toLongArray(), size);

        return new BooleanBitsetSeries(bitSet);
    }

    @Override
    public int size() {
        return size;
    }
}
