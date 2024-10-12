package org.dflib.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

class LimitedIterator<T> implements Iterator<T> {

    private final int limit;
    private final Iterator<T> delegate;

    private int read;

    LimitedIterator(Iterator<T> delegate, int limit) {
        this.limit = limit;
        this.delegate = delegate;
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public boolean hasNext() {
        return read < limit && delegate.hasNext();
    }

    @Override
    public T next() {
        if (read++ >= limit) {
            throw new NoSuchElementException("Past the end of the iterator");
        }

        return delegate.next();
    }
}
