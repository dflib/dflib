package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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

            Series<?>[] columns = new Series[w];

            // TODO: unified parallelization engine
            Environment env = Environment.commonEnv();

            // 1. don't parallelize single-column DataFrames
            // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
            // creating, submitting and joining tasks

            if (w <= 1 || source.height() < env.parallelExecThreshold()) {
                for (int i = 0; i < w; i++) {
                    columns[i] = exps[i].eval(source);
                }
            } else {
                ExecutorService pool = env.threadPool();
                Future<Series<?>>[] tasks = new Future[w];

                for (int i = 0; i < w; i++) {
                    Exp<?> exp = exps[i];
                    tasks[i] = pool.submit(() -> exp.eval(source));
                }

                for (int i = 0; i < w; i++) {
                    try {
                        columns[i] = tasks[i].get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return columns;
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
