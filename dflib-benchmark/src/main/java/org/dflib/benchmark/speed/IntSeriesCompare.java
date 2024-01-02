package org.dflib.benchmark.speed;

import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.Series;
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
public class IntSeriesCompare {

    @Param("5000000")
    public int rows;

    private IntSeries s1;
    private IntSeries s2;
    private Series<Integer> s3;
    private BooleanSeries odds;

    @Setup
    public void setUp() {
        s1 = ValueMaker.intSeq().intSeries(rows);
        s2 = ValueMaker.intSeq().intSeries(rows);
        s3 = ValueMaker.intSeq().series(rows);
        odds = ValueMaker.booleanSeq().booleanSeries(rows);
    }

    @Benchmark
    public Object map_LEConst() {
        return s1.map(Exp.$int(0).le(10_000));
    }

    @Benchmark
    public Object select_LEConst() {
        return s1.select(Exp.$int(0).le(1_000_000));
    }

    @Benchmark
    public Object select_BooleanPositions() {
        return s1.select(odds);
    }

    @Benchmark
    public Object locatePredicate() {
        // unboxing happens here
        return s1.locate(i -> i < 1_000_000);
    }

    @Benchmark
    public Object eqSeries() {
        return s1.eq(s2);
    }

    @Benchmark
    public Object leSeries() {
        return s1.le(s2);
    }

    @Benchmark
    public Object objectMap_LEConst() {
        return s3.map(Exp.$int(0).le(10_000));
    }
}
