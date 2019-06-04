package com.nhl.dflib.join;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.concat.HConcat;

import java.util.Objects;

/**
 * @since 0.6
 */
public abstract class BaseJoiner {

    private JoinType semantics;
    private String indicatorColumn;

    public BaseJoiner(JoinType semantics, String indicatorColumn) {
        this.semantics = Objects.requireNonNull(semantics);
        this.indicatorColumn = indicatorColumn;
    }

    public DataFrame join(DataFrame lf, DataFrame rf) {
        IntSeries[] indicesPair = calcLeftRightIndices(lf, rf);
        DataFrame joined = merge(indicesPair[0], indicesPair[1], lf, rf);

        return indicatorColumn != null
                ? joined.addColumn(indicatorColumn, buildIndicator(indicesPair[0], indicesPair[1]))
                : joined;
    }

    protected IntSeries[] calcLeftRightIndices(DataFrame lf, DataFrame rf) {

        switch (semantics) {
            case inner:
                return innerJoin(lf, rf);
            case left:
                return leftJoin(lf, rf);
            case right:
                return rightJoin(lf, rf);
            case full:
                return fullJoin(lf, rf);
            default:
                throw new IllegalStateException("Unsupported join semantics: " + semantics);
        }
    }

    protected Series<JoinIndicator> buildIndicator(IntSeries leftIndex, IntSeries rightIndex) {

        int h = leftIndex.size();
        ObjectAccumulator<JoinIndicator> appender = new ObjectAccumulator<>(h);

        for (int i = 0; i < h; i++) {
            appender.add(
                    leftIndex.getInt(i) < 0
                            ? JoinIndicator.right_only
                            : rightIndex.getInt(i) < 0 ? JoinIndicator.left_only : JoinIndicator.both
            );
        }

        return appender.toSeries();
    }

    protected DataFrame merge(IntSeries leftIndex, IntSeries rightIndex, DataFrame lf, DataFrame rf) {

        Index index = joinIndex(lf.getColumnsIndex(), rf.getColumnsIndex());

        int w = index.size();
        int wl = lf.width();

        Series[] data = new Series[w];

        for (int i = 0; i < wl; i++) {
            data[i] = lf.getColumn(i).select(leftIndex);
        }

        for (int i = wl; i < w; i++) {
            data[i] = rf.getColumn(i - wl).select(rightIndex);
        }

        return new ColumnDataFrame(index, data);
    }

    protected Index joinIndex(Index li, Index ri) {
        return HConcat.zipIndex(li, ri);
    }


    protected abstract IntSeries[] innerJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] leftJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] rightJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] fullJoin(DataFrame lf, DataFrame rf);
}
