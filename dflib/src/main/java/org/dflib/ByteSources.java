package org.dflib;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * Represents a collection of binary resources that provide data for DataFrames. It is an abstract interface between
 * DataFrame loaders and data storages. A ByteSources can be a filesystem folder, a zip archive, a cloud drive folder,
 * or some other "catalog" of resources. DFLib provides a number of ByteSources types out of the box.
 *
 * @since 1.1.0
 */
@FunctionalInterface
public interface ByteSources {

    default <T> Map<String, T> processStreams(BiFunction<String, InputStream, T> processor) {
        return process((n, s) -> processor.apply(n, s.stream()));
    }

    <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor);

    /**
     * Creates a sources object from a collection of {@link ByteSource} objects. The sources will be resolved in parallel.
     */
    static ByteSources of(Map<String, ? extends ByteSource> sources) {

        switch (sources.size()) {
            case 0:
                return new ByteSources() {
                    @Override
                    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {
                        return Map.of();
                    }
                };

            case 1:
                Map.Entry<String, ? extends ByteSource> s1 = sources.entrySet().iterator().next();
                return new ByteSources() {
                    @Override
                    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {
                        // since the result can contain nulls, we can't use Map.of()
                        return Collections.singletonMap(s1.getKey(), processor.apply(s1.getKey(), s1.getValue()));
                    }
                };

            default:

                int len = sources.size();
                String[] keys = new String[len];
                ByteSource[] srcs = new ByteSource[len];

                Iterator<? extends Map.Entry<String, ? extends ByteSource>> it = sources.entrySet().iterator();
                for (int i = 0; i < len; i++) {
                    Map.Entry<String, ? extends ByteSource> e = it.next();
                    keys[i] = e.getKey();
                    srcs[i] = e.getValue();
                }

                return new ByteSources() {
                    @Override
                    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {
                        ExecutorService pool = Environment.commonEnv().threadPool();
                        Future<T>[] tasks = new Future[len];

                        for (int i = 0; i < len; i++) {
                            int ix = i;
                            Function<ByteSource, T> sp = st -> processor.apply(keys[ix], st);
                            tasks[i] = pool.submit(() -> sp.apply(srcs[ix]));
                        }

                        Map<String, T> result = new HashMap<>((int) (1 + len / 0.75));
                        for (int i = 0; i < len; i++) {
                            try {
                                // TODO: resolve with timeout?
                                result.put(keys[i], tasks[i].get());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        return result;
                    }
                };
        }
    }
}
