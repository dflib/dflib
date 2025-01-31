package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

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
    public RowSetMerger expandCols(ColumnExpander expander) {

        IntSeries rsStretchCounts = expander.getStretchCounts();
        int rsLen = expander.getExpanded().size();

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
}
