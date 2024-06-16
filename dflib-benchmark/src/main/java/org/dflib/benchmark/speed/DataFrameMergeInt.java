package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
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
public class DataFrameMergeInt {

    @Param("1000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        IntSeries c0 = ValueMaker.intSeq().intSeries(rows);
        df = DataFrame.byColumn("c0").of(c0);
    }

    @Benchmark
    public Object mapCast() {
        return df
                .cols()
                .merge((r, t) -> t.set(0, ((int) r.get(0)) * 10))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapApiCast() {
        return df
                .cols()
                .merge((r, t) -> t.set(0, r.get(0, Integer.class) * 10))
                .materialize()
                .iterator();
    }


    @Benchmark
    public Object mapInt() {
        return df
                .cols()
                .merge((r, t) -> t.set(0, r.getInt(0) * 10))
                .materialize()
                .iterator();
    }
}
