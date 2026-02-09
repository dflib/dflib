package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.series.IndexedSeries;
import org.dflib.series.SingleValueSeries;

/**
 * @since 2.0.0
 */
public abstract class ConditionalJoiner extends Joiner {

    protected ConditionalJoiner(JoinType type) {
        super(type);
    }

    protected abstract IntSeries[] innerJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] leftJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] rightJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] fullJoin(DataFrame lf, DataFrame rf);

    @Override
    public Series<?>[] buildColumns(
            DataFrame leftFrame,
            DataFrame rightFrame,
            String indicatorColumn,
            int[] positions) {

        IntSeries[] selectors = rowSelectors(leftFrame, rightFrame);
        return merge(leftFrame, rightFrame, indicatorColumn, selectors[0], selectors[1], positions);
    }

    private IntSeries[] rowSelectors(DataFrame lf, DataFrame rf) {
        return switch (type) {
            case inner -> innerJoin(lf, rf);
            case left -> leftJoin(lf, rf);
            case right -> rightJoin(lf, rf);
            case full -> fullJoin(lf, rf);
        };
    }

    private Series<?>[] merge(
            DataFrame leftFrame,
            DataFrame rightFrame,
            String indicatorColumn,
            IntSeries leftIndex,
            IntSeries rightIndex,
            int[] positions) {

        int llen = leftFrame.width();
        int rlen = rightFrame.width();
        int lrlen = llen + rlen;
        int len = positions.length;
        int h = leftIndex.size();

        Series[] data = new Series[len];
        for (int i = 0; i < len; i++) {

            int si = positions[i];
            if (si < llen) {
                data[i] = new IndexedSeries<>(leftFrame.getColumn(si), leftIndex);
            } else if (si < lrlen) {
                data[i] = new IndexedSeries<>(rightFrame.getColumn(si - llen), rightIndex);
            } else if (si == lrlen && indicatorColumn != null) {
                data[i] = buildIndicator(leftIndex, rightIndex);
            } else {
                data[i] = new SingleValueSeries<>(null, h);
            }
        }

        return data;
    }

    private Series<JoinIndicator> buildIndicator(IntSeries leftIndex, IntSeries rightIndex) {
        int h = leftIndex.size();
        ObjectAccum<JoinIndicator> appender = new ObjectAccum<>(h);

        for (int i = 0; i < h; i++) {
            appender.push(
                    leftIndex.getInt(i) < 0
                            ? JoinIndicator.right_only
                            : rightIndex.getInt(i) < 0 ? JoinIndicator.left_only : JoinIndicator.both
            );
        }

        return appender.toSeries();
    }

}
