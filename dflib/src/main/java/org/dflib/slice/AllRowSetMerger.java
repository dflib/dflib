package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.Arrays;

class AllRowSetMerger extends RowSetMerger {

    static final AllRowSetMerger instance = new AllRowSetMerger();

    @Override
    public <T> Series<T> merge(Series<T> srcColumn, Series<T> rsColumn) {
        return rsColumn;
    }

    @Override
    public RowSetMerger removeUnmatchedRows(BooleanSeries rsCondition) {
        int rsh = rsCondition.size();
        int h = rsCondition.countTrue();
        int[] shrunkIndex = new int[h];

        for (int i = 0, si = 0; i < rsh; i++) {
            if (rsCondition.getBool(i)) {
                shrunkIndex[si++] = i;
            }
            // else - delete row (don't add to the produced index)
        }

        return new DefaultRowSetMerger(shrunkIndex);
    }

    @Override
    public RowSetMerger explodeRows(int rsLen, IntSeries rsStretchCounts) {

        int ch = rsStretchCounts.size();

        int[] explodeIndex = new int[rsLen];

        for (int i = 0, si = 0, rsi = 0, et = 0; i < ch; i++) {

            int explodeBy = rsStretchCounts.getInt(rsi++);
            for (int j = 0; j < explodeBy; j++) {
                explodeIndex[si++] = -i - 1 - et - j;
            }

            // subtract "1", as we are only interested in the expansion delta vs the original row set
            et += explodeBy - 1;
        }

        return new DefaultRowSetMerger(explodeIndex);
    }

    @Override
    public RowSetMerger stretchRows(int rsLen, IntSeries rsStretchCounts) {

        int ch = rsStretchCounts.size();

        int[] stretchIndex = new int[rsLen];

        for (int i = 0, si = 0, rsi = 0; i < ch; i++) {

            // unlike "explode", the returned index here will ignore the row set Series, and will fill
            // everything from the source, thus "stretching" values to fill exploded ranges

            int stretchBy = rsStretchCounts.getInt(rsi++);
            if (stretchBy > 1) {
                Arrays.fill(stretchIndex, si, si + stretchBy, i);
                si += stretchBy;
            } else {
                stretchIndex[si++] = i;
            }
        }

        // TODO: a custom RowSetMerger that ignores row set on merge, as all indices above are known non-negative
        return new DefaultRowSetMerger(stretchIndex);
    }
}
