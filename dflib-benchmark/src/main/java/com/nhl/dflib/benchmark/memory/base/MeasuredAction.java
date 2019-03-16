package com.nhl.dflib.benchmark.memory.base;

@FunctionalInterface
public interface MeasuredAction<T> {

    T run();
}
