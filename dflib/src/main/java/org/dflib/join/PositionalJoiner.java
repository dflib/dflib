package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.series.ArraySeries;
import org.dflib.series.SingleValueSeries;

import java.util.function.UnaryOperator;

/**
 * A DataFrame joiner that matches rows by their position (index) in the DataFrame rather than by key or predicate.
 * This is the equivalent of a "zip" operation with join semantics controlling how mismatched heights are handled.
 *
 * @since 2.0.0
 */
public class PositionalJoiner {

    private final JoinType type;

    public PositionalJoiner(JoinType type) {
        this.type = type;
    }

    public Series<?>[] buildColumns(
            DataFrame leftFrame,
            DataFrame rightFrame,
            String indicatorColumn,
            int[] positions) {

        int lh = leftFrame.height();
        int rh = rightFrame.height();
        int h = targetHeight(lh, rh);

        UnaryOperator<Series<?>> lt = seriesTrimmer(lh, h);
        UnaryOperator<Series<?>> rt = seriesTrimmer(rh, h);

        int llen = leftFrame.width();
        int rlen = rightFrame.width();
        int lrlen = llen + rlen;
        int len = positions.length;

        Series[] data = new Series[len];
        for (int i = 0; i < len; i++) {
            int si = positions[i];
            if (si < llen) {
                data[i] = lt.apply(leftFrame.getColumn(si));
            } else if (si < lrlen) {
                data[i] = rt.apply(rightFrame.getColumn(si - llen));
            } else if (si == lrlen && indicatorColumn != null) {
                data[i] = buildIndicator(lh, rh, h);
            } else {
                data[i] = new SingleValueSeries<>(null, h);
            }
        }

        return data;
    }

    private int targetHeight(int lh, int rh) {
        return switch (type) {
            case full -> Math.max(lh, rh);
            case right -> rh;
            case left -> lh;
            case inner -> Math.min(lh, rh);
        };
    }

    private UnaryOperator<Series<?>> seriesTrimmer(int seriesHeight, int desiredHeight) {
        if (seriesHeight < desiredHeight) {
            return s -> padSeries(s, desiredHeight - seriesHeight);
        } else if (seriesHeight > desiredHeight) {
            return s -> s.selectRange(0, desiredHeight);
        } else {
            return UnaryOperator.identity();
        }
    }

    private <T> Series<T> padSeries(Series<T> series, int paddingSize) {
        Object[] padded = new Object[series.size() + paddingSize];
        series.copyTo(padded, 0, 0, series.size());
        return new ArraySeries(padded);
    }

    private Series<JoinIndicator> buildIndicator(int lh, int rh, int h) {
        ObjectAccum<JoinIndicator> appender = new ObjectAccum<>(h);
        for (int i = 0; i < h; i++) {
            appender.push(
                    i >= lh
                            ? JoinIndicator.right_only
                            : i >= rh ? JoinIndicator.left_only : JoinIndicator.both
            );
        }
        return appender.toSeries();
    }
}
