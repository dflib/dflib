package com.nhl.dflib;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ArrayIterator<T> implements Iterator<T> {

    private final int len;
    private final T[] data;
    private int counter;

    public ArrayIterator(T[] data) {
        this.data = data;
        this.len = data.length;
    }

    @Override
    public boolean hasNext() {
        return counter < len;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Past the end of the iterator");
        }

        return data[counter++];
    }
}
