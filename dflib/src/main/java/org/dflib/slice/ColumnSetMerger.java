package org.dflib.slice;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

/**
 * @since 1.0.0-M19
 */
public class ColumnSetMerger {

    public static ColumnSetMerger of(Index sourceIndex, String[] csLabels) {

        int sLen = sourceIndex.size();
        int csLen = csLabels.length;

        // allocate max possible array for the merge index to fit all merge possibilities,
        // up to when all columns are added and none are replaced

        int[] mergeIndex = new int[sLen + csLen];
        for (int i = 0; i < sLen; i++) {
            mergeIndex[i] = i;
        }

        int expandBy = 0;

        for (int i = 0; i < csLen; i++) {
            if (sourceIndex.contains(csLabels[i])) {

                int srcPos = sourceIndex.position(csLabels[i]);

                // if duplicate existing name, add as an extra column
                int mPos = mergeIndex[srcPos] >= 0 ? srcPos : sLen + expandBy++;
                mergeIndex[mPos] = -1 - i;

            } else {
                mergeIndex[sLen + expandBy++] = -1 - i;
            }
        }

        return new ColumnSetMerger(sLen + expandBy, mergeIndex);
    }

    public static ColumnSetMerger of(Index sourceIndex, int[] csPositions) {
        int sLen = sourceIndex.size();
        int csLen = csPositions.length;

        // allocate max possible array for the merge index to fit all merge possibilities,
        // up to when all columns are added and none are replaced
        int[] mergeIndex = new int[sLen + csLen];
        for (int i = 0; i < sLen; i++) {
            mergeIndex[i] = i;
        }

        int expandBy = 0;

        for (int i = 0; i < csLen; i++) {
            int pos = csPositions[i];

            if (pos < sLen) {

                // if duplicate existing name, add as an extra column
                int mPos = mergeIndex[pos] >= 0 ? pos : sLen + expandBy++;
                mergeIndex[mPos] = -1 - i;

            } else {
                mergeIndex[sLen + expandBy++] = -1 - i;
            }
        }

        return new ColumnSetMerger(sLen + expandBy, mergeIndex);
    }


    // An index to reconstruct a DataFrame from the original source and a transformed column set. It encodes a source
    // of column value in each position (source Series, or transformed column set series), and also implicitly allows
    // to generate more or less values (compared to the original Series)

    // Model of the index:
    //   "mergeIndex.length":                 merged size
    //   "i" in 0..length-1:                  merged column position
    //   "mergeIndex[i]" is negative:         colSetPos = -1 - i
    //   "mergeIndex[i]" is positive or zero: srcPos = i

    private final int mergeLen;
    private final int[] mergeIndex;

    protected ColumnSetMerger(int mergeLen, int[] mergeIndex) {

        // merge index array may be bigger than the column set size. So using "mergeLen"
        // to define the relevant part of the index.
        this.mergeLen = mergeLen;
        this.mergeIndex = mergeIndex;
    }

    /**
     * Performs a merge of the source Series with a transformed row set Series.
     */
    public DataFrame merge(
            Index sIndex,
            Series<?>[] sColumns,
            String[] csLabels,
            Series<?>[] csColumns) {

        String[] labels = new String[mergeLen];
        Series<?>[] columns = new Series[mergeLen];

        for (int i = 0; i < mergeLen; i++) {
            int si = mergeIndex[i];

            if (si < 0) {
                int csi = -1 - si;
                labels[i] = csLabels[csi];
                columns[i] = csColumns[csi];
            } else {
                labels[i] = sIndex.get(si);
                columns[i] = sColumns[si];
            }
        }

        return new ColumnDataFrame(null, Index.ofDeduplicated(labels), columns);
    }
}
