package org.dflib.map;

import java.util.Objects;

/**
 * An opaque object used as a comparision key for multi-column row comparisons. The key is made out of two components
 * that are compared positionally with another {@link CombinationHash}.
 */
public class CombinationHash {

    private Object hash1;
    private Object hash2;

    public CombinationHash(Object hash1, Object hash2) {
        this.hash1 = hash1;
        this.hash2 = hash2;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CombinationHash)) {
            return false;
        }

        CombinationHash ck = (CombinationHash) o;
        return Objects.equals(hash1, ck.hash1) && Objects.equals(hash2, ck.hash2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash1, hash2);
    }
}
