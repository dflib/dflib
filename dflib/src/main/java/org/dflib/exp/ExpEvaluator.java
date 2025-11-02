package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Series;

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
        int resultW = exps.length;

        Series<?>[] result = new Series[resultW];

        Environment env = Environment.commonEnv();

        // 1. don't parallelize single-column DataFrames
        // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        if (resultW <= 1 || df.height() < env.parallelExecThreshold()) {
            for (int i = 0; i < resultW; i++) {
                result[i] = exps[i].eval(df);
            }
        } else {
            ExecutorService pool = env.threadPool();
            Future<Series<?>>[] tasks = new Future[resultW];

            for (int i = 0; i < resultW; i++) {
                Exp<?> exp = exps[i];
                tasks[i] = pool.submit(() -> exp.eval(df));
            }

            for (int i = 0; i < resultW; i++) {
                try {
                    result[i] = tasks[i].get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return result;
    }
}
