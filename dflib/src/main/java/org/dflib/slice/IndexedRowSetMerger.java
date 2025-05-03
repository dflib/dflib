package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

class IndexedRowSetMerger extends RowSetMerger {

    // An index to reconstruct a Series from the original source and a transformed row set. It encodes a source
    // of row value in each position (source Series, or transformed row set series), and also implicitly allows
    // to generate more or less values (compared to the original Series)

    // Model of the index:
    //   "mergeIndex.length":                 merged size, can be bigger, smaller or the same as the source
    //   "i" in 0..length-1:                  merged Series position
    //   "mergeIndex[i]" is negative:         rowSetPos = -1 - i
    //   "mergeIndex[i]" is positive or zero: srcPos = i

    private final int[] mergeIndex;

    IndexedRowSetMerger(int[] mergeIndex) {
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

        return new IndexedRowSetMerger(shrunkIndex);
    }

    @Override
    public RowSetMerger expandCols(IntSeries stretchCounts, int stretchedSize) {

        int ch = mergeIndex.length;
        int nn = mergeIndex.length - stretchCounts.size() + stretchedSize;

        int[] explodeIndex = new int[nn];

        for (int i = 0, si = 0, rsi = 0, et = 0; i < ch; i++) {
            int mv = mergeIndex[i];

            if (mv < 0) {
                int explodeBy = stretchCounts.getInt(rsi++);
                for (int j = 0; j < explodeBy; j++) {
                    explodeIndex[si++] = mv - et - j;
                }

                // subtract "1", as we are only interested in the expansion delta vs the original row set
                et += explodeBy - 1;

            } else {
                explodeIndex[si++] = mv;
            }
        }

        return new IndexedRowSetMerger(explodeIndex);
    }
}
