package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Arrays;

/**
 * @since 1.0.0-M19
 */
public abstract class RowSetMerger {

    public static RowSetMerger of(int srcLen, IntSeries rowIndex) {

        int[] mi = new int[srcLen];
        for (int i = 0; i < srcLen; i++) {
            mi[i] = i;
        }

        int[] mie = null;

        int h = rowIndex.size();
        int ei = 0;
        for (int i = 0; i < h; i++) {

            int pos = rowIndex.getInt(i);

            if (mi[pos] >= 0) {
                mi[pos] = -i - 1;
            }
            // account for repeating positions in the index
            else {
                if (ei == 0) {
                    mie = new int[h - i];
                }

                mie[ei++] = -i - 1;
            }
        }

        if (ei > 0) {
            int[] miCombined = Arrays.copyOf(mi, srcLen + ei);
            System.arraycopy(mie, 0, miCombined, srcLen, ei);
            return new DefaultRowSetMerger(miCombined);
        }

        return new DefaultRowSetMerger(mi);
    }

    public static RowSetMerger of(BooleanSeries rowIndex) {
        int h = rowIndex.size();
        int[] mergeIndex = new int[h];
        for (int i = 0, rsi = 0; i < h; i++) {
            mergeIndex[i] = rowIndex.getBool(i) ? -rsi++ - 1 : i;
        }
        return new DefaultRowSetMerger(mergeIndex);
    }

    public static RowSetMerger ofAll() {
        return AllRowSetMerger.instance;
    }

    public static RowSetMerger ofRange(int srcLen, int fromInclusive, int toExclusive) {
        return new RangeRowSetMerger(srcLen, fromInclusive, toExclusive);
    }

    /**
     * Performs a merge of the source Series with a transformed row set Series.
     */
    public abstract <T> Series<T> merge(Series<T> srcColumn, Series<T> rsColumn);

    /**
     * Returns a "contracting" merger that will merge using the same approach as this merger, but will also remove
     * the rows not matched by the supplied condition.
     */
    public abstract RowSetMerger removeUnmatchedRows(BooleanSeries rsCondition);

    public abstract RowSetMerger expandCols(ColumnExpander expander);

    public abstract RowSetMerger stretchCols(ColumnExpander expander);

}
