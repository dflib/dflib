package com.nhl.dflib.collection;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.series.BooleanArraySeries;

/**
 * @since 0.6
 */
public class BooleanMutableList {

    // TODO: bitmap?
    private boolean[] data;
    private int size;

    public BooleanMutableList() {
        this(10);
    }

    public BooleanMutableList(int capacity) {
        this.size = 0;
        this.data = new boolean[capacity];
    }

    public void add(boolean value) {

        if (size == data.length) {
            expand();
        }

        data[size++] = value;
    }

    public BooleanSeries toBooleanSeries() {
        boolean[] data = this.data;

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new BooleanArraySeries(data, 0, size);
    }

    private void expand() {

        int newCapacity = data.length * 2;
        boolean[] newData = new boolean[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
