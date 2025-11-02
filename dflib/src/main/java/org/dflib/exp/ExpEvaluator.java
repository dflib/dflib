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

        int w = exps.length;
        Series<?>[] result = new Series[w];

        if (shouldRunInParallel(w, df.height())) {
            ExecutorService pool = Environment.commonEnv().threadPool();
            Future<Series<?>>[] tasks = new Future[w];

            for (int i = 0; i < w; i++) {
                Exp<?> exp = exps[i];
                tasks[i] = pool.submit(() -> exp.eval(df));
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
                result[i] = exps[i].eval(df);
            }
        }

        return result;
    }

    public static Series<?>[] reduce(DataFrame df, Exp<?>... exps) {

        int w = exps.length;
        Series<?>[] result = new Series[w];

        if (shouldRunInParallel(w, df.height())) {
            ExecutorService pool = Environment.commonEnv().threadPool();
            Future<Series<?>>[] tasks = new Future[w];

            for (int i = 0; i < w; i++) {
                Exp<?> exp = exps[i];
                tasks[i] = pool.submit(() -> new SingleValueSeries<>(exp.reduce(df), 1));
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
                result[i] = new SingleValueSeries<>(exps[i].reduce(df), 1);
            }
        }

        return result;
    }

    public static Series<?>[] reduce(GroupBy groupBy, Exp<?>... exps) {

        int w = exps.length;
        int gbH = groupBy.size();
        Series<?>[] result = new Series[w];

        if (shouldRunInParallel(w, groupBy.getSource().height())) {
            ExecutorService pool = Environment.commonEnv().threadPool();
            Future<Series<?>>[] tasks = new Future[w];

            for (int i = 0; i < w; i++) {
                Exp<?> exp = exps[i];
                tasks[i] = pool.submit(() -> reduceColumn(groupBy, exp, gbH));
            }

            for (int i = 0; i < w; i++) {
                try {
                    result[i] = tasks[i].get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            for (int i = 0; i < w; i++) {
                result[i] = reduceColumn(groupBy, exps[i], gbH);
            }
        }

        return result;
    }

    private static Series<?> reduceColumn(GroupBy groupBy, Exp<?> exp, int aggH) {
        // TODO: primitives support for performance
        ValueAccum<Object> columnBuilder = new ObjectAccum<>(aggH);

        // if aggH == 0, there will be no group keys, and the result will be empty
        // TODO: a check for aggH == 0 to avoid the implicit assumption?
        for (Object key : groupBy.getGroupKeys()) {
            DataFrame group = groupBy.getGroup(key);

            // expecting 1-element Series. Unpack them and add to the accum
            columnBuilder.push(exp.reduce(group));
        }

        return columnBuilder.toSeries();
    }

    private static boolean shouldRunInParallel(int resultWidth, int srcHeight) {

        // 1. don't parallelize single-column operations
        // 2. don't parallelize short DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        return resultWidth > 1 || srcHeight >= Environment.commonEnv().parallelExecThreshold();
    }
}
