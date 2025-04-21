package org.dflib;

import org.dflib.f.CachingSupplier;
import org.dflib.print.InlineClassExposingPrinter;
import org.dflib.print.Printer;

import java.net.http.HttpClient;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
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
                new InlineClassExposingPrinter(),
                createDefaultHttpClientSupplier());

        commonEnv = new AtomicReference<>(defaultEnv);
    }

    private final ExecutorService threadPool;
    private final int parallelExecThreshold;
    private final Printer printer;

    // HttpClient creates at least 2 threads active until it is garbage collected. So create it lazily.
    // Note that HttpClient doesn't require an explicit shutdown.
    private final Supplier<HttpClient> lazyHttpClient;

    public static Environment commonEnv() {
        return commonEnv.get();
    }

    private static Supplier<HttpClient> createDefaultHttpClientSupplier() {
        Supplier<HttpClient> s = () -> HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        return new CachingSupplier<>(s);
    }

    /**
     * Sets the singleton thread pool used by parallelizable operations. Intended for the apps to override the default
     * pool configured by DFLib.
     */
    public static void setThreadPool(ExecutorService threadPool) {
        Objects.requireNonNull(threadPool);
        resetEnv(old -> new Environment(
                threadPool,
                old.parallelExecThreshold,
                old.printer,
                old.lazyHttpClient));
    }

    /**
     * Sets a common minimal size threshold for operations that can be split and run in parallel.
     */
    public static void setParallelExecThreshold(int parallelExecThreshold) {
        resetEnv(old -> new Environment(
                old.threadPool,
                parallelExecThreshold,
                old.printer,
                old.lazyHttpClient));
    }

    /**
     * Sets a common printer for Series and DataFrames
     */
    public static void setPrinter(Printer printer) {
        resetEnv(old -> new Environment(
                old.threadPool,
                old.parallelExecThreshold,
                printer,
                old.lazyHttpClient));
    }

    /**
     * @since 2.0.0
     */
    public static void setHttpClient(HttpClient client) {
        Supplier<HttpClient> supplier = client != null ? () -> client : createDefaultHttpClientSupplier();

        resetEnv(old -> new Environment(
                old.threadPool,
                old.parallelExecThreshold,
                old.printer,
                supplier));
    }

    /**
     * @since 2.0.0
     */
    public static void setHttpClient(Supplier<HttpClient> clientSupplier) {
        resetEnv(old -> new Environment(
                old.threadPool,
                old.parallelExecThreshold,
                old.printer,
                new CachingSupplier<>(clientSupplier)));
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
            Printer printer,
            Supplier<HttpClient> lazyHttpClient) {
        this.threadPool = threadPool;
        this.parallelExecThreshold = parallelExecThreshold;
        this.printer = printer;
        this.lazyHttpClient = lazyHttpClient;
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

    /**
     * Returns a lazily-created reusable HttpClient
     *
     * @since 2.0.0
     */
    public HttpClient httpClient() {
        return lazyHttpClient.get();
    }

    // only used by tests
    static void setCommonEnv(Environment env) {
        commonEnv.set(env);
    }
}
