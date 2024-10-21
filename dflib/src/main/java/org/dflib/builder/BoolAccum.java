package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.series.BooleanBitsetSeries;
import org.dflib.series.BitSet;

public class BoolAccum implements ValueAccum<Boolean> {

    private static final int INDEX_BIT_SHIFT = 6;

    private long[] data;
    private int size;

    public BoolAccum() {
        data = new long[1];
    }

    public BoolAccum(int capacity) {
        data = new long[1 + ((capacity - 1) >> INDEX_BIT_SHIFT)];
    }

    public void fill(BooleanSeries values, int fromOffset, int toOffset, int len) {
        if (len <= 0) {
            return;
        }

        int toIdx = (toOffset + len - 1) >> INDEX_BIT_SHIFT;
        ensureCapacity(toIdx);
        // TODO: unwrap this, to eliminate repetitive checks
        for (int i = 0; i < len; i++) {
            replaceBool(toOffset + i, values.getBool(fromOffset + i));
        }
    }

    public void fill(int from, int to, boolean value) {
        int toIdx = (to - 1) >> INDEX_BIT_SHIFT;
        ensureCapacity(toIdx);
        // TODO: replace with a bit-logic to improve performance
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
        int idx = size >> INDEX_BIT_SHIFT;
        ensureCapacity(idx);
        if (v) {
            data[idx] |= (1L << size);
        }
        size++;
    }

    @Override
    public void replace(int pos, Boolean v) {
        replaceBool(pos,  v != null ? v : false);
    }

    @Override
    public void replaceBool(int pos, boolean v) {
        int idx;
        if (pos >= size) {
            size = pos + 1;
            idx = size >> INDEX_BIT_SHIFT;
        } else {
            idx = pos >> INDEX_BIT_SHIFT;
        }

        ensureCapacity(idx);
        if (v) {
            data[idx] |= (1L << pos);
        } else {
            data[idx] &= ~(1L << pos);
        }
    }

    @Override
    public BooleanBitsetSeries toSeries() {
        BitSet bitSet = size == 0
                ? BitSet.EMPTY
                : new BitSet(data, size);
        data = null; // nullify reference to avoid array (and eventually series) mutation
        return new BooleanBitsetSeries(bitSet);
    }

    void ensureCapacity(int capacity) {
        if(capacity < data.length) {
            return;
        }
        int capacityToSet = data.length * 2;
        while (capacity >= capacityToSet) {
            capacity = capacity * 2;
        }
        long[] newData = new long[capacityToSet];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public int size() {
        return size;
    }
}
