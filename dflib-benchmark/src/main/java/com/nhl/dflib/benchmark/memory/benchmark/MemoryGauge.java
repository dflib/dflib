package com.nhl.dflib.benchmark.memory.benchmark;

public class MemoryGauge {

    private static long measureMemoryConsumption() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private static void runGC() {
        System.gc();
        System.gc();
        System.gc();
    }

    public long measure(MeasuredAction<?> action) {

        // the method is taking a callable to prevent GC of the result during measurement

        long m0 = measureNow();
        Object result = action.run();
        long m1 = measureNow();

        // dummy invocation to ensure the result is not garbage-collected prior to the measurement
        result.hashCode();

        return m1 - m0;
    }

    public long measureNow() {
        runGC();
        return measureMemoryConsumption();
    }
}
