package org.dflib.benchmark.speed;

import org.dflib.IntSeries;
import org.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class IntSeriesIndex {

    @Param("5000000")
    public int rows;

    private IntSeries s1;

    @Setup
    public void setUp() {
        s1 = ValueMaker.intSeq().intSeries(rows);
    }

    @Benchmark
    public Object index() {
        return s1.index(i -> i % 2 == 0);
    }

    @Benchmark
    public Object indexInt() {
        return s1.indexInt(i -> i % 2 == 0);
    }
}
