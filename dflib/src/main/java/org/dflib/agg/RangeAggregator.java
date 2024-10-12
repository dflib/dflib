package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.window.WindowRange;

import java.util.Arrays;

public abstract class RangeAggregator {

    public static RangeAggregator of(DataFrame df, WindowRange range) {
        return range.alwaysInRange(df.height())
                ? new AllRangeAggregator(df)
                : new SubRangesAggregator(df, range);
    }

    protected final DataFrame source;

    protected RangeAggregator(DataFrame source) {
        this.source = source;
    }

    public abstract Series<?>[] agg(Exp<?>... aggregators);

    static class AllRangeAggregator extends RangeAggregator {

        AllRangeAggregator(DataFrame source) {
            super(source);
        }

        @Override
        public Series<?>[] agg(Exp<?>... aggregators) {
            int w = aggregators.length;
            int h = source.height();

            // must check for empty series before aggregation, as the next step would return a Series of size 1
            if (h == 0) {
                Series<?>[] expandedColumns = new Series[w];
                Arrays.fill(expandedColumns, Series.of());
                return expandedColumns;
            }

            Series<?>[] oneRowSeries = DataFrameAggregator.agg(source, aggregators);

            // expand each column to the height of the original DataFrame
            Series<?>[] expandedColumns = new Series[w];
            for (int i = 0; i < w; i++) {
                expandedColumns[i] = Series.ofVal(oneRowSeries[i].get(0), h);
            }

            return expandedColumns;
        }
    }

    static class SubRangesAggregator extends RangeAggregator {

        private final DataFrame[] ranges;

        SubRangesAggregator(DataFrame source, WindowRange range) {
            super(source);

            int h = source.height();
            DataFrame[] ranges = new DataFrame[h];
            for (int i = 0; i < h; i++) {

                // TODO: would be great if "Exp.eval" could be range-aware, so we won't need to create "h" DataFrames
                ranges[i] = range.selectRows(source, i);

            }
            this.ranges = ranges;
        }

        @Override
        public Series<?>[] agg(Exp<?>... aggregators) {

            int w = aggregators.length;
            int h = source.height();

            Series<?>[] data = new Series[w];
            for (int i = 0; i < w; i++) {
                ObjectAccum accum = new ObjectAccum<>(h);
                Exp<?> agg = aggregators[i];

                for (int j = 0; j < h; j++) {
                    accum.push(agg.eval(ranges[j]).get(0));
                }

                data[i] = accum.toSeries();
            }

            return data;
        }
    }
}
