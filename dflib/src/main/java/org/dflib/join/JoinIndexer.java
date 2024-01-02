package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.Index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class JoinIndexer {

    public static Index simpleIndex(DataFrame leftFrame, DataFrame rightFrame, String indicatorColumn) {
        int baseLen = leftFrame.width() + rightFrame.width();
        int len = indicatorColumn != null ? baseLen + 1 : baseLen;
        return index(new LabelAccum(len), leftFrame, rightFrame, indicatorColumn).toIndex();
    }

    public static MultiNameIndex multiNameIndex(DataFrame leftFrame, DataFrame rightFrame, String indicatorColumn) {
        int baseLen = leftFrame.width() + rightFrame.width();
        int len = indicatorColumn != null ? baseLen + 1 : baseLen;
        return index(new MultiLabelAccum(len), leftFrame, rightFrame, indicatorColumn).toMultiNameIndex();
    }

    public static Map<String, Integer> multiLabelPositions(DataFrame leftFrame, DataFrame rightFrame, String indicatorColumn) {
        return index(new MultiLabelPosAccum(), leftFrame, rightFrame, indicatorColumn).positions();
    }

    private static <T extends Accum> T index(
            T accum,
            DataFrame leftFrame,
            DataFrame rightFrame,
            String indicatorColumn) {

        String[] lLabels = leftFrame.getColumnsIndex().getLabels();
        String[] rLabels = rightFrame.getColumnsIndex().getLabels();

        int llen = lLabels.length;
        int rlen = rLabels.length;

        String lp = leftFrame.getName() != null ? leftFrame.getName() + "." : null;
        String rp = rightFrame.getName() != null ? rightFrame.getName() + "." : null;

        for (int i = 0; i < llen; i++) {
            accum.add(lp, lLabels[i]);
        }

        for (int i = 0; i < rlen; i++) {
            accum.add(rp, rLabels[i]);
        }

        if (indicatorColumn != null) {
            accum.add(null, indicatorColumn);
        }

        return accum;
    }

    static abstract class Accum {

        final Set<String> uniqueNames;
        int i;

        Accum() {
            this.uniqueNames = new HashSet<>();
        }

        abstract void add(String prefix, String label);

        // TODO: can we reuse this in Index for name deduplication?
        String uniqueName(String possiblyDuplicatedName) {
            while (!uniqueNames.add(possiblyDuplicatedName)) {
                possiblyDuplicatedName = possiblyDuplicatedName + "_";
            }

            return possiblyDuplicatedName;
        }
    }

    static class LabelAccum extends Accum {

        final String[] labels;

        LabelAccum(int len) {
            this.labels = new String[len];
        }

        @Override
        public void add(String prefix, String label) {
            String prefixed = prefix != null ? prefix + label : label;
            labels[i++] = uniqueName(prefixed);
        }

        Index toIndex() {
            return Index.of(labels);
        }
    }

    static class MultiLabelAccum extends Accum {

        final String[] labels;
        private final Map<String, String> aliases;

        MultiLabelAccum(int len) {
            this.labels = new String[len];
            this.aliases = new HashMap<>();
        }

        @Override
        public void add(String prefix, String label) {

            if (prefix != null) {
                String name = uniqueName(prefix + label);
                labels[i++] = name;
                aliases.putIfAbsent(label, name);
            } else {
                labels[i++] = uniqueName(label);
            }
        }

        MultiNameIndex toMultiNameIndex() {
            return new MultiNameIndex(aliases, labels);
        }
    }

    static class MultiLabelPosAccum extends Accum {

        private final Map<String, Integer> positions;
        int i;

        MultiLabelPosAccum() {
            this.positions = new HashMap<>();
        }

        @Override
        public void add(String prefix, String label) {

            Integer pos = i++;

            if (prefix != null) {
                String prefixed = uniqueName(prefix + label);
                positions.put(prefixed, pos);
                positions.put(uniqueName(label), pos);
            } else {
                positions.put(uniqueName(label), pos);
            }
        }

        Map<String, Integer> positions() {
            return positions;
        }
    }
}
