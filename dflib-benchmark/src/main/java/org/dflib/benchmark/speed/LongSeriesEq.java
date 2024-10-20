package org.dflib.benchmark.speed;

import org.dflib.LongSeries;
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
public class LongSeriesEq {

    @Param("5000000")
    public int rows;

    private LongSeries s1;
    private LongSeries s2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.longSeq().longSeries(rows);

        int split = 2_500_000;
        s2 = s1.head(split).concatLong(ValueMaker.randomLongSeq().longSeries(rows - split));
    }

    @Benchmark
    public Object eq() {
        return s1.eq(s2);
    }
}
