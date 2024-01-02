package org.dflib.builder;

import org.dflib.Extractor;

/**
 * @since 0.16
 */
public class SelfExtractor<T> implements Extractor<T, T> {

    @Override
    public void extractAndStore(T from, ValueStore<T> to) {
        to.push(from);
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to, int toPos) {
        to.replace(toPos, from);
    }

    @Override
    public ValueAccum<T> createAccum(int capacity) {
        return new ObjectAccum<>(capacity);
    }

    @Override
    public ValueHolder<T> createHolder() {
        return new ObjectHolder<>();
    }
}
