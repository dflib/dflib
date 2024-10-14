package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.benchmark.ValueMaker;
import org.dflib.series.IntegerSeries;
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
public class IntegerSeriesAdd {

    @Param("5000000")
    public int rows;

    private IntegerSeries s1;
    private IntegerSeries s2;
    private DataFrame s1_s2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.integerSeq(0.1).integerSeries(rows);
        s2 = ValueMaker.integerSeq(0.1).integerSeries(rows);

        s1_s2 = DataFrame.byColumn("s1", "s2").of(s1, s2);
    }

    @Benchmark
    public Object addSeries() {
        return s1.add(s2);
    }

    @Benchmark
    public Object map_AddConst() {
        return s1.map(Exp.$int(0).add(10));
    }

    @Benchmark
    public Object map_Add() {
        return s1_s2.cols().merge(Exp.$int(0).add(Exp.$int(1)));
    }

    @Benchmark
    public Object mapAsInt_Add() {
        return s1.mapAsInt(i -> i == null ? 0 : i + 10);
    }
}

/*
// no Exp optimizations
Benchmark                       (rows)  Mode  Cnt   Score    Error  Units
IntegerSeriesAdd.addSeries     5000000  avgt    6   2.653 ±  0.024  ms/op
IntegerSeriesAdd.mapAsInt_Add  5000000  avgt    6   9.995 ±  0.061  ms/op
IntegerSeriesAdd.map_Add       5000000  avgt    6  60.832 ± 30.853  ms/op
IntegerSeriesAdd.map_AddConst  5000000  avgt    6  39.170 ±  8.099  ms/op

Benchmark                     (rows)  Mode  Cnt  Score   Error  Units
IntSeriesAdd.addSeries       5000000  avgt    6  0.772 ± 0.015  ms/op
IntSeriesAdd.addRangeSeries  5000000  avgt    6  1.257 ± 0.005  ms/op
IntSeriesAdd.mapAsInt_Add    5000000  avgt    6  5.704 ± 0.025  ms/op
IntSeriesAdd.map_Add         5000000  avgt    6  0.772 ± 0.015  ms/op
IntSeriesAdd.map_AddConst    5000000  avgt    6  0.605 ± 0.008  ms/op
 */