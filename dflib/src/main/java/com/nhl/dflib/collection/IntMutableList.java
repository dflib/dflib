package com.nhl.dflib.collection;

import com.nhl.dflib.series.IntSeries;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link IntSeries}.
 */
public class IntMutableList {

    private int[] data;
    private int size;

    public IntMutableList() {
        this.size = 0;
        this.data = new int[10];
    }

    public void add(int value) {

        if (size == data.length) {
            expand();
        }

        data[size++] = value;
    }

    public IntSeries toSeries() {
        int[] data = this.data;

        // making sure no one can change the series via the Mutable List
        this.data = null;

        return new IntSeries(data, size);
    }

    private void expand() {

        int newCapacity = data.length * 2;
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
