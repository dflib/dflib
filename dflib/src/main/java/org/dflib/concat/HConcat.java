package org.dflib.concat;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.JoinType;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.RowCombiner;
import org.dflib.row.RowProxy;
import org.dflib.series.ArraySeries;

import java.util.Iterator;
import java.util.function.UnaryOperator;

public class HConcat {

    private final JoinType semantics;

    public HConcat(JoinType semantics) {
        this.semantics = semantics;
    }

    public static Index zipIndex(Index leftIndex, String[] rightLabels) {

        String[] lLabels = leftIndex.getLabels();

        int rlen = rightLabels.length;
        int llen = lLabels.length;

        // zipped index is continuous to match rowZipper algorithm below that rebuilds the arrays, so reset left and
        // right positions, only preserve the names...

        String[] zipped = new String[llen + rlen];
        System.arraycopy(lLabels, 0, zipped, 0, llen);

        // resolve dupes on the right
        for (int i = 0; i < rlen; i++) {

            String name = rightLabels[i];
            while (leftIndex.hasLabel(name)) {
                name = name + "_";
            }

            int ri = i + llen;
            zipped[ri] = name;
        }

        return Index.of(zipped);
    }

    public DataFrame concat(Index joinedColumns, DataFrame lf, DataFrame rf, RowCombiner rowCombiner) {
        int lh = lf.height();
        int rh = rf.height();

        int h = concatHeight(lh, rh);

        MultiArrayRowBuilder tr = new MultiArrayRowBuilder(joinedColumns, h);

        Iterator<RowProxy> li = lf.iterator();
        Iterator<RowProxy> ri = rf.iterator();

        for (int i = 0; i < h; i++) {
            tr.next();

            RowProxy lr = i < lh ? li.next() : null;
            RowProxy rr = i < rh ? ri.next() : null;

            rowCombiner.combine(lr, rr, tr);
        }

        return new ColumnDataFrame(null, joinedColumns, tr.getData());
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

        for (String s : lf.getColumnsIndex()) {
            newData[i++] = lt.apply(lf.getColumn(s));
        }

        for (String s : rf.getColumnsIndex()) {
            newData[i++] = rt.apply(rf.getColumn(s));
        }

        return new ColumnDataFrame(null, joinedColumns, newData);
    }

    private UnaryOperator<Series<?>> seriesTrimmer(int seriesHeight, int desiredHeight) {
        if (seriesHeight < desiredHeight) {
            return s -> padSeries(s, desiredHeight - seriesHeight);
        } else if (seriesHeight > desiredHeight) {
            return s -> s.range(0, desiredHeight);
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
