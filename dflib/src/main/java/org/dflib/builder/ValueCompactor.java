package org.dflib.builder;

import java.util.Map;

class ValueCompactor {

    static final <T> T get(Map<T, T> cache, T val) {
        return val != null
                ? cache.computeIfAbsent(val, v -> v)
                : null;
    }
}
