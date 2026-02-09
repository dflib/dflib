package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.series.IntSequenceSeries;

import java.util.Arrays;

/**
 * A DataFrame joiner that matches rows by their position (index) in the DataFrame rather than by key or predicate.
 * This is the equivalent of a "zip" operation with join semantics controlling how mismatched heights are handled.
 *
 * @since 2.0.0
 */
public class PositionalJoiner extends BaseJoiner {

    public PositionalJoiner(JoinType semantics) {
        super(semantics);
    }

    @Override
    protected IntSeries[] innerJoin(DataFrame lf, DataFrame rf) {
        int h = Math.min(lf.height(), rf.height());
        IntSeries range = new IntSequenceSeries(0, h);
        return new IntSeries[]{range, range};
    }

    @Override
    protected IntSeries[] leftJoin(DataFrame lf, DataFrame rf) {
        int lh = lf.height();
        int rh = rf.height();

        IntSeries li = new IntSequenceSeries(0, lh);
        IntSeries ri = paddedRange(rh, lh);

        return new IntSeries[]{li, ri};
    }

    @Override
    protected IntSeries[] rightJoin(DataFrame lf, DataFrame rf) {
        int lh = lf.height();
        int rh = rf.height();

        IntSeries li = paddedRange(lh, rh);
        IntSeries ri = new IntSequenceSeries(0, rh);

        return new IntSeries[]{li, ri};
    }

    @Override
    protected IntSeries[] fullJoin(DataFrame lf, DataFrame rf) {
        int lh = lf.height();
        int rh = rf.height();
        int h = Math.max(lh, rh);

        IntSeries li = paddedRange(lh, h);
        IntSeries ri = paddedRange(rh, h);

        return new IntSeries[]{li, ri};
    }

    private IntSeries paddedRange(int sourceHeight, int desiredHeight) {
        if (sourceHeight >= desiredHeight) {
            return new IntSequenceSeries(0, desiredHeight);
        }

        int[] data = new int[desiredHeight];
        for (int i = 0; i < sourceHeight; i++) {
            data[i] = i;
        }
        Arrays.fill(data, sourceHeight, desiredHeight, -1);
        return Series.ofInt(data);
    }
}
