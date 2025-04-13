package org.dflib.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * A thread-unsafe value compactor
 */
public class ValueCompactor<T> {

    private final Map<T, T> cache;

    public ValueCompactor() {
        this.cache = new HashMap<>();
    }

    public T get(T val) {
        return val != null
                ? cache.computeIfAbsent(val, v -> v)
                : null;
    }
}
