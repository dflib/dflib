package com.nhl.dflib.join;

import java.util.Objects;

/**
 * An opaque object used as a row comparision key in the indexed joins. The key made out of two components that are
 * compared positionally with another CombinationKey.
 */
public class CombinationKey {

    private Object key1;
    private Object key2;

    public CombinationKey(Object key1, Object key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CombinationKey)) {
            return false;
        }

        CombinationKey ck = (CombinationKey) o;
        return Objects.equals(key1, ck.key1) && Objects.equals(key2, ck.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }
}
