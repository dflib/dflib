package org.dflib.collection;

import java.util.Iterator;

public class Iterators {

    public static <T> Iterator<T> skip(Iterator<T> it, int skip) {
        for (int i = 0; i < skip && it.hasNext(); i++) {
            it.next();
        }

        return it;
    }

    public static <T> Iterator<T> limit(Iterator<T> it, int limit) {
        return new LimitedIterator<>(it, limit);
    }
}
