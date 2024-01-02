package org.dflib.colset;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

import java.util.Arrays;

abstract class ColumnSetMerger {

    final Index sourceIndex;
    final int[] mergeIndex;
    final int addOffset;

    static ColumnSetMerger of(Index sourceIndex, String[] labels) {
        int sourceLen = sourceIndex.size();
        int mergedLen = labels.length;

        // allocate max possible array for the merge index to fit all merge possibilities,
        // up to when all columns are added and none are replaced
        int[] mergeIndex = new int[sourceLen + mergedLen];
        Arrays.fill(mergeIndex, -1);

        int addOffset = 0;

        for (int i = 0; i < mergedLen; i++) {
            if (sourceIndex.hasLabel(labels[i])) {

                int pos = sourceIndex.position(labels[i]);
                if (mergeIndex[pos] == -1) {
                    mergeIndex[pos] = i;
                }
                // duplicate of existing, add as new
                else {
                    mergeIndex[sourceLen + addOffset++] = i;
                }
            } else {
                mergeIndex[sourceLen + addOffset++] = i;
            }
        }

        return new ByLabelColumnSetMerger(sourceIndex, mergeIndex, addOffset, labels);
    }

    static ColumnSetMerger of(Index sourceIndex, int[] positions) {
        int sourceLen = sourceIndex.size();
        int mergedLen = positions.length;

        // allocate max possible array for the merge index to fit all merge possibilities,
        // up to when all columns are added and none are replaced
        int[] mergeIndex = new int[sourceLen + mergedLen];
        Arrays.fill(mergeIndex, -1);

        int addOffset = 0;

        for (int i = 0; i < mergedLen; i++) {
            int pos = positions[i];
            if (positions[i] < sourceLen) {
                if (mergeIndex[pos] == -1) {
                    mergeIndex[pos] = i;
                }
                // duplicate of existing, add as new
                else {
                    mergeIndex[sourceLen + addOffset++] = i;
                }
            } else {
                mergeIndex[sourceLen + addOffset++] = i;
            }
        }

        return new ByPosColumnSetMerger(sourceIndex, mergeIndex, addOffset, positions);
    }

    ColumnSetMerger(Index sourceIndex, int[] mergeIndex, int addOffset) {
        this.sourceIndex = sourceIndex;
        this.mergeIndex = mergeIndex;
        this.addOffset = addOffset;
    }

    Series<?>[] mergedColumns(DataFrame source, Series<?>[] newColumns) {

        int w = sourceIndex.size() + addOffset;
        Series<?>[] mergedColumns = new Series[w];

        for (int i = 0; i < w; i++) {
            if (mergeIndex[i] == -1) {
                mergedColumns[i] = source.getColumn(i);
            } else {
                mergedColumns[i] = newColumns[mergeIndex[i]];
            }
        }

        return mergedColumns;
    }

    protected abstract Index mergedIndex();

    static final class ByLabelColumnSetMerger extends ColumnSetMerger {

        final String[] labels;

        ByLabelColumnSetMerger(Index sourceIndex, int[] mergeIndex, int addOffset, String[] labels) {
            super(sourceIndex, mergeIndex, addOffset);
            this.labels = labels;
        }

        @Override
        protected Index mergedIndex() {

            if (addOffset <= 0) {
                return sourceIndex;
            }

            int sourceLen = sourceIndex.size();

            String[] extraLabels = new String[addOffset];
            for (int i = 0; i < addOffset; i++) {
                extraLabels[i] = labels[mergeIndex[sourceLen + i]];
            }

            // "addLabels" also does name deduplication
            return sourceIndex.addLabels(extraLabels);
        }
    }

    static final class ByPosColumnSetMerger extends ColumnSetMerger {

        final int[] positions;

        ByPosColumnSetMerger(Index sourceIndex, int[] mergeIndex, int addOffset, int[] positions) {
            super(sourceIndex, mergeIndex, addOffset);
            this.positions = positions;
        }

        @Override
        protected Index mergedIndex() {

            if (addOffset <= 0) {
                return sourceIndex;
            }

            int sourceLen = sourceIndex.size();

            String[] extraLabels = new String[addOffset];
            for (int i = 0; i < addOffset; i++) {
                // generate extra column names based on the position numbers
                extraLabels[i] = String.valueOf(positions[mergeIndex[sourceLen + i]]);
            }

            // "addLabels" also does name deduplication
            return sourceIndex.addLabels(extraLabels);
        }
    }
}
