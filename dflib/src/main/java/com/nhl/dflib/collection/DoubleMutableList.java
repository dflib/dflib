package com.nhl.dflib.collection;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.series.DoubleArraySeries;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link com.nhl.dflib.DoubleSeries}.
 *
 * @since 0.6
 */
public class DoubleMutableList {
    private double[] data;
    private int size;

    public DoubleMutableList() {
        this(10);
    }

    public DoubleMutableList(int capacity) {
        this.size = 0;
        this.data = new double[capacity];
    }

    public void add(double value) {

        if (size == data.length) {
            expand();
        }

        data[size++] = value;
    }

    public DoubleSeries toDoubleSeries() {
        double[] data = this.data;

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new DoubleArraySeries(data, 0, size);
    }

    private void expand() {

        int newCapacity = data.length * 2;
        double[] newData = new double[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
