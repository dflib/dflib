package com.nhl.dflib.benchmark.memory;

@FunctionalInterface
public interface MeasuredAction<T> {

    T run();
}
