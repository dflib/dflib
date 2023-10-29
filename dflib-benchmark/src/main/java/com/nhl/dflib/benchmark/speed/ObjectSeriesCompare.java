package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.Series;
import com.nhl.dflib.benchmark.ValueMaker;
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
public class ObjectSeriesCompare {

    @Param("5000000")
    public int rows;

    private Series<String> s1;
    private Series<String> s2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.stringSeq().series(rows);
        s2 = ValueMaker.stringSeq().series(rows);
    }

    @Benchmark
    public Object eqSeries() {
        return s1.eq(s2);
    }

    @Benchmark
    public Object isNull() {
        return s1.isNull();
    }

    @Benchmark
    public Object locate() {
        return s1.locate(s -> s.length() % 2 == 0);
    }
}
