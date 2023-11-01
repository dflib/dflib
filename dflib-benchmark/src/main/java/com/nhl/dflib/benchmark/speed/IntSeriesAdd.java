package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.IntSeries;
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
public class IntSeriesAdd {

    @Param("5000000")
    public int rows;

    private IntSeries s1;
    private IntSeries s2;
    private IntSeries sr1;
    private IntSeries sr2;
    private DataFrame s1_s2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.intSeq().intSeries(rows);
        s2 = ValueMaker.intSeq().intSeries(rows);

        // "head" would turn IntArraySeries to IntArrayRangeSeries
        sr1 = s1.head(-1);
        sr2 = s2.head(-1);

        s1_s2 = DataFrame.byColumn("s1", "s2").of(s1, s2);
    }

    @Benchmark
    public Object addSeries() {
        return s1.add(s2);
    }

    @Benchmark
    public Object addRangeSeries() {
        return sr1.add(sr2);
    }

    @Benchmark
    public Object map_AddConst() {
        return s1.map(Exp.$int(0).add(10));
    }

    @Benchmark
    public Object map_Add() {
        return s1_s2.map(Exp.$int(0).add(Exp.$int(1)));
    }

    @Benchmark
    public Object mapAsInt_Add() {
        return s1.mapAsInt(i -> i + 10);
    }
}
