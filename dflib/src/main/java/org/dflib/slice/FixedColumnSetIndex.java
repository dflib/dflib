package org.dflib.slice;

import org.dflib.Index;
import org.dflib.index.StringDeduplicator;

public class FixedColumnSetIndex {

    protected final String[] labels;

    public static FixedColumnSetIndex of(Index csIndex) {
        return new FixedColumnSetIndex(csIndex.toArray());
    }

    public static FixedColumnSetIndex of(String[] csIndex) {
        return new FixedColumnSetIndex(csIndex);
    }

    public static FixedColumnSetIndex ofAppend(Index sourceIndex, String[] csIndex) {

        int csLen = csIndex.length;

        StringDeduplicator deduplicator = StringDeduplicator.of(sourceIndex, csLen);
        String[] csIndexAdd = new String[csLen];
        for (int i = 0; i < csLen; i++) {
            csIndexAdd[i] = deduplicator.nonConflictingName(csIndex[i]);
        }

        // TODO: an "append-only" version of FixedColumnSetIndex, as we are guaranteed to not replace any existing columns
        return new FixedColumnSetIndex(csIndexAdd);
    }

    public static FixedColumnSetIndex of(Index sourceIndex, int[] csIndex) {

        int sLen = sourceIndex.size();
        int csLen = csIndex.length;

        String[] csLabelsIndex = new String[csLen];
        for (int i = 0; i < csLen; i++) {
            csLabelsIndex[i] = csIndex[i] < sLen
                    ? sourceIndex.get(csIndex[i])
                    : String.valueOf(csIndex[i]);
        }

        return new FixedColumnSetIndex(csLabelsIndex);
    }

    protected FixedColumnSetIndex(String[] labels) {
        this.labels = labels;
    }

    public Index getIndex() {
        return Index.ofDeduplicated(labels);
    }

    public String[] getLabels() {
        return labels;
    }
}
