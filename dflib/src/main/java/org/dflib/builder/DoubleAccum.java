package org.dflib.builder;

import org.dflib.DoubleSeries;
import org.dflib.series.DoubleArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link DoubleSeries}.
 */
public class DoubleAccum implements ValueAccum<Double> {

    private double[] data;
    private int size;

    public DoubleAccum() {
        this(10);
    }

    public DoubleAccum(int capacity) {
        this.size = 0;
        this.data = new double[capacity];
    }


    public void fill(DoubleSeries values, int valuesOffset, int accumOffset, int len) {
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

        values.copyToDouble(data, valuesOffset, accumOffset, len);
    }

    public void fill(int from, int to, double value) {

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
    public void push(Double v) {
        pushDouble(v != null ? v : 0.);
    }

    @Override
    public void pushDouble(double value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Double v) {
        replaceDouble(pos, v != null ? v : 0.);
    }

    @Override
    public void replaceDouble(int pos, double value) {

        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = value;
    }

    @Override
    public DoubleSeries toSeries() {
        double[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new DoubleArraySeries(data);
    }

    @Override
    public int size() {
        return size;
    }

    private double[] compactData() {
        if (data.length == size) {
            return data;
        }

        double[] newData = new double[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {

        double[] newData = new double[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
