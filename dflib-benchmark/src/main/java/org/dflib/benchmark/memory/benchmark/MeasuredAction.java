package org.dflib.benchmark.memory.benchmark;

@FunctionalInterface
public interface MeasuredAction<T> {

    T run();
}
