package org.dflib.builder;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.series.FloatArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link DoubleSeries}.
 *
 * @since 1.1.0
 */
public class FloatAccum implements ValueAccum<Float> {

    private float[] data;
    private int size;

    public FloatAccum() {
        this(10);
    }

    public FloatAccum(int capacity) {
        this.size = 0;
        this.data = new float[capacity];
    }


    public void fill(FloatSeries values, int valuesOffset, int accumOffset, int len) {
        if (len <= 0) {
            return;
        }

        int pastFillEnd = accumOffset + len;
        if (data.length < pastFillEnd) {
            expand(pastFillEnd);
            size = pastFillEnd;
        } else if (size < pastFillEnd) {
            size = pastFillEnd;
        }

        values.copyToFloat(data, valuesOffset, accumOffset, len);
    }

    public void fill(int from, int to, float value) {

        if (to - from < 1) {
            return;
        }

        if (data.length <= to) {
            expand(to);
        }

        Arrays.fill(data, from, to, value);
        size += to - from;
    }

    @Override
    public void push(Float v) {
        pushFloat(v != null ? v : 0.f);
    }

    @Override
    public void pushFloat(float value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Float v) {
        replaceFloat(pos, v != null ? v : 0.f);
    }

    @Override
    public void replaceFloat(int pos, float value) {

        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = value;
    }

    @Override
    public FloatSeries toSeries() {
        float[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new FloatArraySeries(data);
    }

    @Override
    public int size() {
        return size;
    }

    private float[] compactData() {
        if (data.length == size) {
            return data;
        }

        float[] newData = new float[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {

        float[] newData = new float[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
