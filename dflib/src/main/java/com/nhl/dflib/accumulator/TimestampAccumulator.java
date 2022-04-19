package com.nhl.dflib.accumulator;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.TimestampSeries;

import java.sql.Timestamp;
import java.util.Arrays;

public class TimestampAccumulator implements Accumulator<Timestamp> {
    public TimestampAccumulator() {
        this(10);
    }

    public TimestampAccumulator(int capacity) {
        this.size = 0;
        this.data = new Timestamp[capacity];
    }


    protected Timestamp[] data;
    protected int size;


    public void fill(int from, int to, Timestamp value) {
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
    public void add(Timestamp value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void set(int pos, Timestamp v) {
        if (pos >= size) {
            throw new IndexOutOfBoundsException(pos + " is out of bounds for " + size);
        }

        data[pos] = v;
    }

    @Override
    public Series<Timestamp> toSeries() {
        Timestamp[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        // TODO: difference from IntMutableList in that IntArraySeries supports ranged... Reconcile ArraySeries?
        return new TimestampSeries(data);
    }

    protected Timestamp[] compactData() {
        if (data.length == size) {
            return data;
        }

        Timestamp[] newData = new Timestamp[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        Timestamp[] newData = new Timestamp[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
