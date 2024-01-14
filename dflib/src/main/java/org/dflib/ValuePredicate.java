package org.dflib;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// TODO: consider deprecating it (as well as the primitive analogs, like DoublePredicate, etc.) in favor of a
//  null-aware Condition. We already deprecated "DataFrame.selectRows(_, ValuePredicate)", and while theoretically
//  it is faster than the Condition version, benchmarks are inconclusive (ValuePredicate is a often slower, but the
//  margins of error are very large).
@FunctionalInterface
public interface ValuePredicate<V> {

    static <V> ValuePredicate<V> isIn(V... values) {

        if (values.length == 0) {
            return v -> false;
        }
        else if(values.length == 1) {
            return v -> Objects.equals(values[0], v);
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

        if (set.size() == 0) {
            return v -> false;
        }
        else if(set.size() == 1) {
            Object v1 = set.iterator().next();
            return v -> Objects.equals(v1, v);
        }

        return set::contains;
    }

    boolean test(V value);

    default ValuePredicate<V> and(ValuePredicate<? super V> another) {
        return v -> this.test(v) && another.test(v);
    }

    default ValuePredicate<V> or(ValuePredicate<? super V> another) {
        return v -> this.test(v) || another.test(v);
    }

    default ValuePredicate<V> negate() {
        return v -> !this.test(v);
    }
}
