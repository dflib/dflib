package org.dflib.benchmark.speed;

import org.dflib.Series;
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
public class IntegerSeriesIndex {

    @Param("5000000")
    public int rows;

    private IntegerSeries s1;
    private IntegerSeries s2;
    private IntegerSeries s3;
    private Series<Integer> s4;
    private Series<Integer> s5;
    private Series<Integer> s6;

    @Setup
    public void setUp() {
        s1 = ValueMaker.integerSeq().integerSeries(rows);
        s2 = ValueMaker.integerSeq(0.25).integerSeries(rows);
        s3 = ValueMaker.integerSeq(0.75).integerSeries(rows);
        s4 = ValueMaker.integerSeq().series(rows);
        s5 = ValueMaker.integerSeq(0.25).series(rows);
        s6 = ValueMaker.integerSeq(0.75).series(rows);
    }

    @Benchmark
    public Object index0PercentNulls() {
        return s1.index(i -> i % 2 == 0);
    }

    @Benchmark
    public Object indexInt0PercentNulls() {
        return s1.indexInt(i -> i % 2 == 0);
    }

    @Benchmark
    public Object indexObject0PercentNulls() {
        return s4.index(i -> i % 2 == 0);
    }

    @Benchmark
    public Object index25PercentsNulls() {
        return s2.index(i -> i != null && i % 2 == 0);
    }

    @Benchmark
    public Object indexObject25PercentsNulls() {
        return s5.index(i -> i != null && i % 2 == 0);
    }

    @Benchmark
    public Object indexInt25PercentsNulls() {
        return s2.indexInt(i -> i % 2 == 0);
    }

    @Benchmark
    public Object index75PercentsNulls() {
        return s3.index(i -> i != null && i % 2 == 0);
    }

    @Benchmark
    public Object indexInt75PercentsNulls() {
        return s3.indexInt(i -> i % 2 == 0);
    }

    @Benchmark
    public Object indexObject75PercentsNulls() {
        return s6.index(i -> i != null && i % 2 == 0);
    }
}

/*
Benchmark                                       (rows)  Mode  Cnt   Score   Error  Units
IntegerSeriesIndex.index0PercentNulls          5000000  avgt    6   8.018 ± 0.044  ms/op
IntegerSeriesIndex.index25PercentsNulls        5000000  avgt    6  13.801 ± 0.207  ms/op
IntegerSeriesIndex.index75PercentsNulls        5000000  avgt    6  11.528 ± 0.159  ms/op
IntegerSeriesIndex.indexInt0PercentNulls       5000000  avgt    6   3.664 ± 0.018  ms/op
IntegerSeriesIndex.indexInt25PercentsNulls     5000000  avgt    6  10.209 ± 0.575  ms/op
IntegerSeriesIndex.indexInt75PercentsNulls     5000000  avgt    6   9.201 ± 0.350  ms/op
IntegerSeriesIndex.indexObject0PercentNulls    5000000  avgt    6   3.189 ± 0.022  ms/op
IntegerSeriesIndex.indexObject25PercentsNulls  5000000  avgt    6   9.510 ± 0.160  ms/op
IntegerSeriesIndex.indexObject75PercentsNulls  5000000  avgt    6   8.396 ± 0.281  ms/op

Benchmark                     (rows)  Mode  Cnt  Score   Error  Units
IntSeriesIndex.index         5000000  avgt    6  6.038 ± 0.033  ms/op
IntSeriesIndex.indexInt      5000000  avgt    6  2.366 ± 0.182  ms/op
*/