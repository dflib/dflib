package org.dflib;

import org.dflib.print.InlineClassExposingPrinter;
import org.dflib.print.Printer;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * A static "environment" used by DFLib, providing settings for parallel operations, common optimization thresholds,
 * etc. {@link #commonEnv()} provides access to the Environment singleton. Its parameters can be customized individually
 * via static "set" methods.
 */
public class Environment {

    private static final AtomicReference<Environment> commonEnv;

    static {
        Environment defaultEnv = new Environment(
                ForkJoinPool.commonPool(),
                5000,
                new InlineClassExposingPrinter());

        commonEnv = new AtomicReference<>(defaultEnv);
    }


    private final ExecutorService threadPool;
    private final int parallelExecThreshold;
    private final Printer printer;

    public static Environment commonEnv() {
        return commonEnv.get();
    }

    /**
     * Sets the singleton thread pool used by parallelizable operations. Intended for the apps to override the default
     * pool configured by DFLib.
     */
    public static void setThreadPool(ExecutorService threadPool) {
        Objects.requireNonNull(threadPool);
        resetEnv(old -> new Environment(threadPool, old.parallelExecThreshold, old.printer));
    }

    /**
     * Sets a common minimal size threshold for operations that can be split and run in parallel.
     */
    public static void setParallelExecThreshold(int parallelExecThreshold) {
        resetEnv(old -> new Environment(old.threadPool, parallelExecThreshold, old.printer));
    }

    /**
     * Sets a common printer for Series and DataFrames
     */
    public static void setPrinter(Printer printer) {
        resetEnv(old -> new Environment(old.threadPool, old.parallelExecThreshold, printer));
    }

    static void resetEnv(UnaryOperator<Environment> envFactory) {
        Environment oldEnv;
        Environment newEnv;
        do {
            oldEnv = commonEnv();
            newEnv = envFactory.apply(oldEnv);
        } while (!Environment.commonEnv.compareAndSet(oldEnv, newEnv));
    }

    protected Environment(
            ExecutorService threadPool,
            int parallelExecThreshold,
            Printer printer) {
        this.threadPool = threadPool;
        this.parallelExecThreshold = parallelExecThreshold;
        this.printer = printer;
    }

    public ExecutorService threadPool() {
        return threadPool;
    }

    // TODO: Parallelization threshold is dependent on the type of data we are aggregating. E.g. concatenating
    //  Strings is going to be much slower than summing integers. Need to be smarter about it.

    public int parallelExecThreshold() {
        return parallelExecThreshold;
    }

    public Printer printer() {
        return printer;
    }

    // only used by tests
    static void setCommonEnv(Environment env) {
        commonEnv.set(env);
    }
}
