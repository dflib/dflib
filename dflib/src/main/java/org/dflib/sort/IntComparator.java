package org.dflib.sort;

import java.util.Objects;

public interface IntComparator {

    int compare(int i1, int i2);


    default IntComparator thenComparing(IntComparator other) {
        Objects.requireNonNull(other);
        return (i1, i2) -> {
            int res = compare(i1, i2);
            return res != 0 ? res : other.compare(i1, i2);
        };
    }
}
