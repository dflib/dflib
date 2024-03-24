package org.dflib;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Contains static helper methods to create some useful predicates.
 *
 * @since 1.0.0-M21
 */
public final class Predicates {

    public static <T> Predicate<T> isIn(T... values) {

        if (values.length == 0) {
            return v -> false;
        } else if (values.length == 1) {
            return v -> Objects.equals(values[0], v);
        }

        // index for faster lookups
        Set<T> set = new HashSet<>((int) (values.length / 0.75) + 1);
        for (T v : values) {
            set.add(v);
        }

        return set::contains;
    }

    static <T> Predicate<T> isIn(Iterable<? extends T> values) {

        // index for faster lookups
        Set<T> set;
        if (values instanceof Set) {
            set = (Set<T>) values;
        } else {
            set = new HashSet<>();
            for (T v : values) {
                set.add(v);
            }
        }

        if (set.size() == 0) {
            return v -> false;
        } else if (set.size() == 1) {
            Object v1 = set.iterator().next();
            return v -> Objects.equals(v1, v);
        }

        return set::contains;
    }

    private Predicates() {
    }
}
