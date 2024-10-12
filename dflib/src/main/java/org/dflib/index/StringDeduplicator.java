package org.dflib.index;

import org.dflib.Index;

import java.util.HashSet;
import java.util.Set;

public abstract class StringDeduplicator {

    public static StringDeduplicator of(int capacity) {
        return new DefaultDeduplicator(capacity);
    }

    public static StringDeduplicator of(Index index, int capacity) {
        return new RefIndexDeduplicator(index, capacity);
    }

    public abstract String nonConflictingName(String baseName);

    public String[] nonConflicting(String[] values) {
        int len = values.length;

        int i = 0;
        String firstConflict = null;
        for (; i < len; i++) {
            String l = nonConflictingName(values[i]);
            if (l != values[i]) {
                firstConflict = l;
                break;
            }
        }

        if (firstConflict == null) {
            return values;
        }

        String[] nonConflicting = new String[len];
        System.arraycopy(values, 0, nonConflicting, 0, i);
        nonConflicting[i] = firstConflict;
        for (int j = i + 1; j < len; j++) {
            nonConflicting[j] = nonConflictingName(values[j]);
        }

        return nonConflicting != null ? nonConflicting : values;
    }

    public static class DefaultDeduplicator extends StringDeduplicator {

        private final Set<String> seen;

        DefaultDeduplicator(int capacity) {
            this.seen = new HashSet<>((int) Math.ceil(capacity / 0.75));
        }

        @Override
        public String nonConflictingName(String baseName) {
            while (!seen.add(baseName)) {
                baseName = baseName + "_";
            }
            return baseName;
        }
    }

    public static class RefIndexDeduplicator extends StringDeduplicator {

        private final Index refIndex;
        private final Set<String> seen;

        RefIndexDeduplicator(Index refIndex, int capacity) {
            this.refIndex = refIndex;
            this.seen = new HashSet<>((int) Math.ceil(capacity / 0.75));
        }

        @Override
        public String nonConflictingName(String baseName) {
            while (refIndex.contains(baseName) || !seen.add(baseName)) {
                baseName = baseName + "_";
            }
            return baseName;
        }
    }
}
