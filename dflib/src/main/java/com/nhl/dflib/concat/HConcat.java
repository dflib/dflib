package com.nhl.dflib.concat;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.MultiArrayRowBuilder;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ArraySeries;

import java.util.Iterator;
import java.util.function.UnaryOperator;

public class HConcat {

    private JoinType semantics;

    public HConcat(JoinType semantics) {
        this.semantics = semantics;
    }

    public static Index zipIndex(Index leftIndex, Index rightIndex) {

        int llen = leftIndex.size();
        int rlen = rightIndex.size();

        String[] lLabels = leftIndex.getLabels();
        String[] rLabels = rightIndex.getLabels();

        // zipped index is continuous to match rowZipper algorithm below that rebuilds the arrays, so reset left and
        // right positions, only preserve the names...

        String[] zipped = new String[llen + rlen];
        for (int i = 0; i < llen; i++) {
            zipped[i] = lLabels[i];
        }

        // resolve dupes on the right
        for (int i = 0; i < rlen; i++) {

            String name = rLabels[i];
            while (leftIndex.hasLabel(name)) {
                name = name + "_";
            }

            int ri = i + llen;
            zipped[ri] = name;
        }

        return Index.withLabels(zipped);
    }

    public DataFrame concat(Index joinedColumns, DataFrame lf, DataFrame rf, RowCombiner rowCombiner) {
        int lh = lf.height();
        int rh = rf.height();

        int h = concatHeight(lh, rh);

        MultiArrayRowBuilder tr = new MultiArrayRowBuilder(joinedColumns, h);

        Iterator<RowProxy> li = lf.iterator();
        Iterator<RowProxy> ri = rf.iterator();

        for (int i = 0; i < h; i++) {

            RowProxy lr = i < lh ? li.next() : null;
            RowProxy rr = i < rh ? ri.next() : null;

            rowCombiner.combine(lr, rr, tr);
            tr.reset();
        }

        return new ColumnDataFrame(joinedColumns, tr.getData());
    }

    public DataFrame concat(Index joinedColumns, DataFrame lf, DataFrame rf) {
        int lh = lf.height();
        int rh = rf.height();

        int h = concatHeight(lh, rh);

        UnaryOperator<Series<?>> lt = seriesTrimmer(lh, h);
        UnaryOperator<Series<?>> rt = seriesTrimmer(rh, h);

        int w = joinedColumns.size();
        Series<?>[] newData = new Series[w];
        int i = 0;

        for (Series<?> s : lf.getColumns()) {
            newData[i++] = lt.apply(s);
        }

        for (Series<?> s : rf.getColumns()) {
            newData[i++] = rt.apply(s);
        }

        return new ColumnDataFrame(joinedColumns, newData);
    }

    private UnaryOperator<Series<?>> seriesTrimmer(int seriesHeight, int desiredHeight) {
        if (seriesHeight < desiredHeight) {
            return s -> padSeries(s, desiredHeight - seriesHeight);
        } else if (seriesHeight > desiredHeight) {
            return s -> s.openClosedRange(0, desiredHeight);
        } else {
            return UnaryOperator.identity();
        }
    }

    private <T> Series<T> padSeries(Series<T> series, int paddingSize) {
        Object[] padded = new Object[series.size() + paddingSize];
        series.copyTo(padded, 0, 0, series.size());
        return new ArraySeries(padded);
    }

    private int concatHeight(int lh, int rh) {
        switch (semantics) {
            case full:
                return Math.max(lh, rh);
            case right:
                return rh;
            case left:
                return lh;
            case inner:
                return Math.min(lh, rh);
            default:
                throw new IllegalStateException("Unsupported join type: " + semantics);
        }
    }


}
