package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.exp.ExpEvaluator;

import java.util.Arrays;

abstract class WindowColumnEvaluator {

    public static WindowColumnEvaluator of(DataFrame df, WindowRange range) {
        return range.alwaysInRange(df.height())
                ? new NoRangeEvaluator(df)
                : new PerRangeEvaluator(df, range);
    }

    protected final DataFrame source;

    protected WindowColumnEvaluator(DataFrame source) {
        this.source = source;
    }

    public abstract Series<?>[] eval(Exp<?>... exps);

    static class NoRangeEvaluator extends WindowColumnEvaluator {

        NoRangeEvaluator(DataFrame source) {
            super(source);
        }

        @Override
        public Series<?>[] eval(Exp<?>... exps) {

            int w = exps.length;
            int h = source.height();

            // must check for empty series before aggregation, as the next step would return a Series of size 1
            if (h == 0) {
                Series<?>[] columns = new Series[w];
                Arrays.fill(columns, Series.of());
                return columns;
            }

            return ExpEvaluator.eval(source, exps);
        }
    }

    static class PerRangeEvaluator extends WindowColumnEvaluator {

        private final WindowRange range;

        PerRangeEvaluator(DataFrame source, WindowRange range) {
            super(source);
            this.range = range;
        }

        @Override
        public Series<?>[] eval(Exp<?>... exps) {

            int w = exps.length;
            int h = source.height();

            Series<?>[] data = new Series[w];
            for (int i = 0; i < w; i++) {
                ObjectAccum accum = new ObjectAccum<>(h);
                Exp<?> exp = exps[i];

                for (int j = 0; j < h; j++) {

                    // TODO: (performance)  would be great if "Exp.eval" could be range-aware, so we won't need to create
                    //  "h" DataFrames
                    DataFrame rangeDf = range.selectRows(source, j);

                    // since we have a mix of per-row and aggregating expressions, we need to call "eval" instead of
                    // "reduce", and then find and capture a value in the result that depends on the range structure

                    // TODO: (performance) Avoid running eval on ALL rows of the range, as we only use one value in a
                    //  range. Need smth like "Exp.evalAt(int pos, DataFrame)"
                    Series<?> s = exp.eval(rangeDf);
                    accum.push(s.get(range.rowOffset(j)));
                }

                data[i] = accum.toSeries();
            }

            return data;
        }
    }
}
