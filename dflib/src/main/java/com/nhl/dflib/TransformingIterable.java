package com.nhl.dflib;

import java.util.Iterator;
import java.util.function.Function;

class TransformingIterable<S, T> implements Iterable<T> {

    private Iterable<S> source;
    private Function<S, T> transformer;

    public TransformingIterable(Iterable<S> source, Function<S, T> transformer) {
        this.source = source;
        this.transformer = transformer;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Iterator<S> delegateIt = TransformingIterable.this.source.iterator();

            @Override
            public boolean hasNext() {
                return delegateIt.hasNext();
            }

            @Override
            public T next() {
                return transformer.apply(delegateIt.next());
            }
        };
    }
}
