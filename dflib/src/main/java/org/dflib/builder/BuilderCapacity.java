package org.dflib.builder;

import java.util.Collection;

class BuilderCapacity {

    private static final int DEFAULT_CAPACITY = 1000;

    public static int capacity(Iterable<?> source) {
        return (source instanceof Collection) ? ((Collection) source).size() : DEFAULT_CAPACITY;
    }

    public static int defaultCapacity() {
        return DEFAULT_CAPACITY;
    }
}
