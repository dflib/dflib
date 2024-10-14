package org.dflib.builder;

import org.dflib.series.FixedSizeBitSet;
import org.dflib.series.IntegerSeries;

public class IntegerAccum implements ValueAccum<Integer> {

    private int[] data;
    private FixedSizeBitSet nulls;
    private int size;

    public IntegerAccum(int len) {
        data = new int[len];
        nulls = new FixedSizeBitSet(len);
        size = 0;
    }

    @Override
    public void push(Integer v) {
        if(v == null) {
            pushNull();
        } else {
            pushInt(v);
        }
    }

    public void pushNull() {
        if (size == data.length) {
            expand(data.length * 2);
        }
        nulls.set(size++);
    }

    public void pushInt(int v) {
        if (size == data.length) {
            expand(data.length * 2);
        }
        data[size++] = v;
    }

    @Override
    public IntegerSeries toSeries() {
        int[] data = compactData();
        this.data = null;
        return new IntegerSeries(data, nulls.resize(size));
    }

    @Override
    public int size() {
        return size;
    }

    private void expand(int newCapacity) {
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
        this.nulls = nulls.resize(newCapacity);
    }

    private int[] compactData() {
        if (data.length == size) {
            return data;
        }

        int[] newData = new int[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }
}
