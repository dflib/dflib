package org.dflib.benchmark.speed;

import org.dflib.DoubleSeries;
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
public class DoubleSeriesAdd {

    @Param("5000000")
    public int rows;

    private DoubleSeries s1;
    private DoubleSeries s2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.doubleSeq().doubleSeries(rows);
        s2 = ValueMaker.doubleSeq().doubleSeries(rows);
    }

    @Benchmark
    public Object add() {
        return s1.add(s2);
    }
}
