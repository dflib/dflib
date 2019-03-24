package com.nhl.dflib.filter;

import java.util.HashSet;
import java.util.Set;

@FunctionalInterface
public interface ValuePredicate<V> {

    static <V> ValuePredicate<V> isIn(V... values) {

        if (values.length == 0) {
            return v -> false;
        }

        // index for faster lookups
        Set<V> set = new HashSet<>((int) (values.length / 0.75) + 1);
        for (V v : values) {
            set.add(v);
        }
        
        return set::contains;
    }

    static <V> ValuePredicate<V> isIn(Iterable<? extends V> values) {

        // index for faster lookups
        Set<V> set;
        if (values instanceof Set) {
            set = (Set<V>) values;
        } else {
            set = new HashSet<>();
            for (V v : values) {
                set.add(v);
            }
        }

        return set::contains;
    }

    boolean test(V value);
}
