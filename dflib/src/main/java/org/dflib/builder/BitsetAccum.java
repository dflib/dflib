package org.dflib.builder;

import org.dflib.series.BooleanBitsetSeries;
import org.dflib.series.FixedSizeBitSet;

import java.util.BitSet;

/**
 * @since 1.1.0
 */
public class BitsetAccum implements ValueAccum<Boolean> {

    BitSet bitset;
    int size = 0;

    public BitsetAccum() {
        bitset = new BitSet(64);
    }

    public BitsetAccum(int capacity) {
        bitset = new BitSet(capacity);
    }

    @Override
    public BooleanBitsetSeries toSeries() {
        FixedSizeBitSet bitSet = size == 0
                ? FixedSizeBitSet.EMPTY
                : new FixedSizeBitSet(bitset.toLongArray(), size);
        return new BooleanBitsetSeries(bitSet);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void push(Boolean v) {
        if (v != null) {
            pushBool(v);
        }
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
        if(v != null) {
            replaceBool(pos, v);
        }
    }

    @Override
    public void replaceBool(int pos, boolean v) {
        if(v) {
            bitset.set(pos);
        } else {
            bitset.clear(pos);
        }
    }
}
