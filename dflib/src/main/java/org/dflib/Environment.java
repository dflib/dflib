package org.dflib;

import org.dflib.print.InlineClassExposingPrinter;
import org.dflib.print.Printer;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * A static "environment" used by DFLib, providing settings for parallel operations, common optimization thresholds,
 * etc. {@link #commonEnv()} provides access to the Environment singleton. Its parameters can be customized individually
 * via static "set" methods.
 *
 * @since 1.0.0-M19
 */
public class Environment {

    private static Environment commonEnv = new Environment(
            ForkJoinPool.commonPool(),
            5000,
            new InlineClassExposingPrinter());

    private final ExecutorService threadPool;
    private final int parallelExecThreshold;
    private final Printer printer;

    public static Environment commonEnv() {
        return commonEnv;
    }

    /**
     * Sets the singleton thread pool used by parallelizable operations. Intended for the apps to override the default
     * pool configured by DFLib.
     */
    public static void setThreadPool(ExecutorService threadPool) {
        Environment.commonEnv = new Environment(
                Objects.requireNonNull(threadPool),
                commonEnv.parallelExecThreshold,
                commonEnv.printer
        );
    }

    /**
     * Sets a common minimal size threshold for operations that can be split and run in parallel.
     */
    public static void setParallelExecThreshold(int parallelExecThreshold) {
        Environment.commonEnv = new Environment(
                commonEnv.threadPool,
                parallelExecThreshold,
                commonEnv.printer
        );
    }

    /**
     * Sets a common printer for Series and DataFrames
     *
     * @since 1.0.0-M20
     */
    public static void setPrinter(Printer printer) {
        Environment.commonEnv = new Environment(
                commonEnv.threadPool,
                commonEnv.parallelExecThreshold,
                printer
        );
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

    /**
     * @since 1.0.0-M20
     */
    public Printer printer() {
        return printer;
    }
}
