package com.nhl.dflib.join;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.IntSeries;
import com.nhl.dflib.series.IndexedSeries;

public class JoinMerger {

    private int height;
    private IntSeries leftIndex;
    private IntSeries rightIndex;

    public JoinMerger(IntSeries leftIndex, IntSeries rightIndex) {

        this.height = leftIndex.size();

        if (height != rightIndex.size()) {
            throw new IllegalArgumentException("Left and right sides of the join and not of the same size: "
                    + height
                    + " vs "
                    + rightIndex.size());
        }

        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }

    public DataFrame join(Index joinedColumns, DataFrame left, DataFrame right) {

        int w = joinedColumns.size();
        int wl = left.width();

        Series[] data = new Series[w];

        for (int i = 0; i < wl; i++) {
            data[i] = new IndexedSeries(left.getColumn(i), leftIndex);
        }

        for (int i = wl; i < w; i++) {
            data[i] = new IndexedSeries(right.getColumn(i - wl), rightIndex);
        }

        return new ColumnDataFrame(joinedColumns, data);
    }
}
