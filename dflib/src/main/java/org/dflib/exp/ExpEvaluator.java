package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ValueAccum;
import org.dflib.series.SingleValueSeries;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Evaluates expressions with operation parallelization support.
 *
 * @since 2.0.0
 */
public class ExpEvaluator {

    /**
     * Evaluates multiple expressions over a given DataFrame, parallelizing evaluation if deemed necessary.
     */
    public static Series<?>[] eval(DataFrame df, Exp<?>... exps) {
        return run(
                e -> e.eval(df),
                df.height(),
                exps);
    }

    public static Series<?>[] reduce(DataFrame df, Exp<?>... exps) {
        return run(
                e -> new SingleValueSeries<>(e.reduce(df), 1),
                df.height(),
                exps);
    }

    public static Series<?>[] reduce(GroupBy groupBy, Exp<?>... exps) {
        int gbH = groupBy.size();
        return run(
                e -> {
                    // TODO: primitives support for performance
                    ValueAccum<Object> accum = new ObjectAccum<>(gbH);

                    // if aggH == 0, there will be no group keys, and the result will be empty
                    for (Object key : groupBy.getGroupKeys()) {
                        DataFrame group = groupBy.getGroup(key);
                        accum.push(e.reduce(group));
                    }

                    return accum.toSeries();
                },
                groupBy.getSource().height(),
                exps);
    }

    private static Series<?>[] run(Function<Exp<?>, Series<?>> seriesMaker, int srcHeight, Exp<?>... exps) {

        int w = exps.length;
        Series<?>[] result = new Series[w];

        if (shouldRunInParallel(w, srcHeight)) {
            ExecutorService pool = Environment.commonEnv().threadPool();
            Future<Series<?>>[] tasks = new Future[w];

            for (int i = 0; i < w; i++) {
                Exp<?> exp = exps[i];
                tasks[i] = pool.submit(() -> seriesMaker.apply(exp));
            }

            for (int i = 0; i < w; i++) {
                try {
                    result[i] = tasks[i].get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            for (int i = 0; i < w; i++) {
                result[i] = seriesMaker.apply(exps[i]);
            }
        }

        return result;
    }

    private static boolean shouldRunInParallel(int resultWidth, int srcHeight) {

        // 1. don't parallelize single-column operations
        // 2. don't parallelize short DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        return resultWidth > 1 || srcHeight >= Environment.commonEnv().parallelExecThreshold();
    }
}
