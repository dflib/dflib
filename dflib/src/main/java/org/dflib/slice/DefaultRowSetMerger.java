package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Arrays;

class DefaultRowSetMerger extends RowSetMerger {

    // An index to reconstruct a Series from the original source and a transformed row set. It encodes a source
    // of row value in each position (source Series, or transformed row set series), and also implicitly allows
    // to generate more or less values (compared to the original Series)

    // Model of the index:
    //   "mergeIndex.length":                 merged size, can be bigger, smaller or the same as the source
    //   "i" in 0..length-1:                  merged Series position
    //   "mergeIndex[i]" is negative:         rowSetPos = -1 - i
    //   "mergeIndex[i]" is positive or zero: srcPos = i

    private final int[] mergeIndex;

    DefaultRowSetMerger(int[] mergeIndex) {
        this.mergeIndex = mergeIndex;
    }

    public <T> Series<T> merge(Series<T> srcColumn, Series<T> rsColumn) {

        int h = mergeIndex.length;

        // TODO: primitive Series
        T[] values = (T[]) new Object[h];

        for (int i = 0; i < h; i++) {
            int si = mergeIndex[i];
            values[i] = si < 0 ? rsColumn.get(-1 - si) : srcColumn.get(si);
        }

        return Series.of(values);
    }

    @Override
    public RowSetMerger removeUnmatchedRows(BooleanSeries rsCondition) {

        int ch = mergeIndex.length;
        int nh = ch - rsCondition.size() + rsCondition.countTrue();

        if (ch == nh) {
            return this;
        }

        int[] shrunkIndex = new int[nh];

        for (int i = 0, si = 0, rsi = 0; i < ch; i++) {
            int mv = mergeIndex[i];

            if (mv < 0) {
                if (rsCondition.getBool(rsi++)) {
                    shrunkIndex[si++] = mv;
                }
                // else - delete row (don't add to the produced index)
            } else {
                shrunkIndex[si++] = mv;
            }
        }

        return new DefaultRowSetMerger(shrunkIndex);
    }

    @Override
    public RowSetMerger expandCols(ColumnExpander expander) {
        IntSeries rsStretchCounts = expander.getStretchCounts();
        int rsLen = expander.getExpanded().size();

        int ch = mergeIndex.length;
        int nn = mergeIndex.length - rsStretchCounts.size() + rsLen;

        int[] explodeIndex = new int[nn];

        for (int i = 0, si = 0, rsi = 0, et = 0; i < ch; i++) {
            int mv = mergeIndex[i];

            if (mv < 0) {
                int explodeBy = rsStretchCounts.getInt(rsi++);
                for (int j = 0; j < explodeBy; j++) {
                    explodeIndex[si++] = mv - et - j;
                }

                // subtract "1", as we are only interested in the expansion delta vs the original row set
                et += explodeBy - 1;

            } else {
                explodeIndex[si++] = mv;
            }
        }

        return new DefaultRowSetMerger(explodeIndex);
    }

    @Override
    public RowSetMerger stretchCols(ColumnExpander expander) {

        IntSeries rsStretchCounts = expander.getStretchCounts();
        int rsLen = expander.getExpanded().size();

        int ch = mergeIndex.length;
        int nn = mergeIndex.length - rsStretchCounts.size() + rsLen;

        int[] stretchIndex = new int[nn];

        for (int i = 0, si = 0, rsi = 0; i < ch; i++) {
            int mv = mergeIndex[i];

            if (mv < 0) {

                // unlike "expand", the returned index here will ignore the row set Series, and will fill
                // everything from the source, thus "stretching" values to fill exploded ranges

                int stretchBy = rsStretchCounts.getInt(rsi++);
                if (stretchBy > 1) {
                    Arrays.fill(stretchIndex, si, si + stretchBy, i);
                    si += stretchBy;
                } else {
                    stretchIndex[si++] = i;
                }
            } else {
                stretchIndex[si++] = mv;
            }
        }

        // TODO: a custom RowSetMerger that ignores row set on merge, as all indices above are known non-negative
        return new DefaultRowSetMerger(stretchIndex);
    }
}
