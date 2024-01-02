package org.dflib.benchmark.memory.benchmark;

// TODO: should we wrap this in JUnit?
public abstract class MemoryTest {

    private MemoryGauge memoryGauge = new MemoryGauge();

    protected void run(String label, MeasuredAction<?> test, long cells) {
        long m = memoryGauge.measure(test);
        double mpc = m / (double) cells;

        // bytes / DataFrame cell
        System.out.println(String.format("%22s %,6.2f", label, mpc));
    }
}
