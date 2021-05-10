package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.*;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.nhl.dflib.Exp.*;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameExp {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {

        IntSeries c0 = ValueMaker.intSeq().intSeries(rows);
        IntSeries c1 = ValueMaker.randomIntSeq(rows / 2).intSeries(rows);
        Series<Integer> c2 = Series.forData(c1);
        Series<String> c3 = ValueMaker.stringSeq().series(rows);
        Series<String> c4 = ValueMaker.constStringSeq("abc").series(rows);
        DoubleSeries c5 = ValueMaker.doubleSeq().doubleSeries(rows);
        DoubleSeries c6 = ValueMaker.randomDoubleSeq().doubleSeries(rows);

        df = DataFrame.newFrame("c0", "c1", "c2", "c3", "c4", "c5", "c6")
                .columns(c0, c1, c2, c3, c4, c5, c6);
    }

    @Benchmark
    public Object mapIntViaLambda() {
        return df.mapColumn(r -> ((Integer) r.get("c0")) + ((Integer) r.get("c1")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapIntViaExp() {
        SeriesExp<?> add = $int("c0").add($int("c1"));
        return add.eval(df).materialize().iterator();
    }

    @Benchmark
    public Object mapIntegerViaLambda() {
        return df.mapColumn(r -> ((Integer) r.get("c0")) + ((Integer) r.get("c2")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapIntegerViaExp() {
        SeriesExp<?> add = $int("c0").add($int("c2"));
        return add.eval(df).materialize().iterator();
    }

    @Benchmark
    public Object mapStringViaLambda() {
        return df.mapColumn(r -> r.get("c3") + ((String) r.get("c4")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapStringViaExp() {
        SeriesExp<String> concat = concat($col("c3"), $col("c4"));
        return concat.eval(df).materialize().iterator();
    }

    @Benchmark
    public Object mapDoubleViaLambda() {
        return df.mapColumn(r -> ((Double) r.get("c5")) + ((Double) r.get("c6")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapDoubleViaExp() {
        SeriesExp<?> add = $double("c5").add($double("c6"));
        return add.eval(df).materialize().iterator();
    }
}
