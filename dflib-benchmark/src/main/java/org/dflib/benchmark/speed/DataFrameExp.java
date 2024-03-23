package org.dflib.benchmark.speed;

import org.dflib.benchmark.ValueMaker;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.dflib.Exp.*;

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
        Series<Integer> c2 = Series.ofIterable(c1);
        Series<String> c3 = ValueMaker.stringSeq().series(rows);
        Series<String> c4 = ValueMaker.constStringSeq("abc").series(rows);
        DoubleSeries c5 = ValueMaker.doubleSeq().doubleSeries(rows);
        DoubleSeries c6 = ValueMaker.randomDoubleSeq().doubleSeries(rows);

        df = DataFrame.byColumn("c0", "c1", "c2", "c3", "c4", "c5", "c6")
                .of(c0, c1, c2, c3, c4, c5, c6);
    }

    @Benchmark
    public Object mapIntViaLambda() {
        return df.cols("C").map(r -> r.get("c0", Integer.class) + r.get("c1", Integer.class))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapIntViaExp() {
        Exp<?> add = $int("c0").add($int("c1"));
        return df.cols("C").map(add).materialize().iterator();
    }

    @Benchmark
    public Object mapIntegerViaLambda() {
        return df.cols("C").map(r -> r.get("c0", Integer.class) + r.get("c2", Integer.class))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapIntegerViaExp() {
        Exp<?> add = $int("c0").add($int("c2"));
        return df.cols("C").map(add).materialize().iterator();
    }

    @Benchmark
    public Object mapStringViaLambda() {
        return df.cols("C").map(r -> {
                    String c3 = (String) r.get("c3");
                    String c4 = (String) r.get("c4");

                    return c3 == null || c4 == null ? null : c3 + c4;
                })
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapStringViaExp() {
        Exp<String> concat = concat($col("c3"), $col("c4"));
        return df.cols("C").map(concat).materialize().iterator();
    }

    @Benchmark
    public Object mapDoubleViaLambda() {
        return df.cols("C").map(r -> ((Double) r.get("c5")) + ((Double) r.get("c6")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapDoubleViaExp() {
        Exp<?> add = $double("c5").add($double("c6"));
        return df.cols("C").map(add).materialize().iterator();
    }
}
