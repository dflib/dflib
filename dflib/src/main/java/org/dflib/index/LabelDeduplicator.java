package org.dflib.index;

import org.dflib.Index;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0.0-M19
 */
public abstract class LabelDeduplicator {

    public static LabelDeduplicator of(int capacity) {
        return new DefaultDeduplicator(capacity);
    }

    public static LabelDeduplicator of(Index index, int capacity) {
        return new RefIndexDeduplicator(index, capacity);
    }

    public abstract String nonConflictingName(String baseName);

    public String[] nonConflictingLabels(String[] labels) {
        int len = labels.length;

        int i = 0;
        String firstConflict = null;
        for (; i < len; i++) {
            String l = nonConflictingName(labels[i]);
            if (l != labels[i]) {
                firstConflict = l;
                break;
            }
        }

        if (firstConflict == null) {
            return labels;
        }

        String[] nonConflicting = new String[len];
        System.arraycopy(labels, 0, nonConflicting, 0, i);
        nonConflicting[i] = firstConflict;
        for (int j = i + 1; j < len; j++) {
            nonConflicting[j] = nonConflictingName(labels[j]);
        }

        return nonConflicting != null ? nonConflicting : labels;
    }

    public static class DefaultDeduplicator extends LabelDeduplicator {

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

    public static class RefIndexDeduplicator extends LabelDeduplicator {

        private final Index refIndex;
        private final Set<String> seen;

        RefIndexDeduplicator(Index refIndex, int capacity) {
            this.refIndex = refIndex;
            this.seen = new HashSet<>((int) Math.ceil(capacity / 0.75));
        }

        @Override
        public String nonConflictingName(String baseName) {
            while (refIndex.hasLabel(baseName) || !seen.add(baseName)) {
                baseName = baseName + "_";
            }
            return baseName;
        }
    }
}
