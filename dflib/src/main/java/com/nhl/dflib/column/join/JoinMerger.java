package com.nhl.dflib.column.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.column.ColumnDataFrame;
import com.nhl.dflib.series.IndexedSeries;

import java.util.Iterator;

public class JoinMerger {

    private int height;
    private Series<Integer> leftIndex;
    private Series<Integer> rightIndex;

    public JoinMerger(Series<Integer> leftIndex, Series<Integer> rightIndex) {

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

        Iterator<Series<?>> ls = left.getDataColumns().iterator();
        for (int i = 0; i < wl; i++) {
            data[i] = new IndexedSeries(ls.next(), leftIndex);
        }

        Iterator<Series<?>> rs = right.getDataColumns().iterator();
        for (int i = wl; i < w; i++) {
            data[i] = new IndexedSeries(rs.next(), rightIndex);
        }

        return new ColumnDataFrame(joinedColumns, data);
    }
}
