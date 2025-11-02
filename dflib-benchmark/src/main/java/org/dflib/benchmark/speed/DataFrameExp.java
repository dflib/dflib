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
        Series<String> c4 = ValueMaker.constSeq("abc").series(rows);
        DoubleSeries c5 = ValueMaker.doubleSeq().doubleSeries(rows);
        DoubleSeries c6 = ValueMaker.randomDoubleSeq().doubleSeries(rows);

        df = DataFrame.byColumn("c0", "c1", "c2", "c3", "c4", "c5", "c6")
                .of(c0, c1, c2, c3, c4, c5, c6);
    }

    @Benchmark
    public Object selectIntegerViaLambda1() {
        return df.cols("C").select(r -> r.get("c0", Integer.class) + r.get("c2", Integer.class))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectIntegerViaExp1() {
        Exp<?> add = $int("c0").add($int("c2"));
        return df.cols("C").select(add).materialize().iterator();
    }

    @Benchmark
    public Object selectIntViaLambda1() {
        return df.cols("C").select(r -> r.get("c0", Integer.class) + r.get("c1", Integer.class))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectIntViaExp1() {
        Exp<?> add = $int("c0").add($int("c1"));
        return df.cols("C").select(add).materialize().iterator();
    }

    @Benchmark
    public Object selectIntViaExp4() {
        Exp<?> add = $int("c0").add($int("c1"));
        Exp<?> mul = $int("c0").mul($int("c1"));
        Exp<?> sub1 = $int("c0").sub($int("c1"));
        Exp<?> sub2 = $int("c1").sub($int("c0"));
        return df.cols("A", "M", "S1", "S2")
                .select(add, mul, sub1, sub2)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectDoubleViaLambda1() {
        return df.cols("C").select(r -> ((Double) r.get("c5")) + ((Double) r.get("c6")))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectDoubleViaExp1() {
        Exp<?> add = $double("c5").add($double("c6"));
        return df.cols("C").select(add).materialize().iterator();
    }

    @Benchmark
    public Object selectDoubleViaExp4() {
        Exp<?> add = $double("c5").add($double("c6"));
        Exp<?> mul = $double("c5").mul($double("c6"));
        Exp<?> sub1 = $double("c5").sub($double("c6"));
        Exp<?> sub2 = $double("c6").sub($double("c5"));
        return df.cols("A", "M", "S1", "S2")
                .select(add, mul, sub1, sub2)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectStringViaLambda1() {
        return df.cols("C").select(r -> {
                    String c3 = (String) r.get("c3");
                    String c4 = (String) r.get("c4");

                    return c3 == null || c4 == null ? null : c3 + c4;
                })
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectStringViaExp1() {
        Exp<String> concat = concat($col("c3"), " ", $col("c4"));
        return df.cols("C").select(concat).materialize().iterator();
    }

    @Benchmark
    public Object selectStringViaExp2() {
        Exp<String> concat1 = concat($col("c3"), $col("c4"));
        Exp<String> concat2 = concat($col("c3"), " ", $col("c4"));
        return df.cols("C1", "C2")
                .select(concat1, concat2)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectStringViaExp4() {
        Exp<String> concat1 = concat($col("c3"), " ", $col("c4"));
        Exp<String> concat2 = concat($col("c3"), "_", $col("c4"));
        Exp<String> concat3 = concat($col("c3"), "-", $col("c4"));
        Exp<String> concat4 = concat($col("c3"), "|", $col("c4"));
        return df.cols("C1", "C2", "C3", "C4")
                .select(concat1, concat2, concat3, concat4)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectStringViaExp6() {
        Exp<String> concat1 = concat($col("c3"), " ", $col("c4"));
        Exp<String> concat2 = concat($col("c3"), "_", $col("c4"));
        Exp<String> concat3 = concat($col("c3"), "-", $col("c4"));
        Exp<String> concat4 = concat($col("c3"), "|", $col("c4"));
        Exp<String> concat5 = concat($col("c3"), "+", $col("c4"));
        Exp<String> concat6 = concat($col("c3"), "=", $col("c4"));
        return df.cols("C1", "C2", "C3", "C4", "C5", "C6")
                .select(concat1, concat2, concat3, concat4, concat5, concat6)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectStringViaExp8() {
        Exp<String> concat1 = concat($col("c3"), " ", $col("c4"));
        Exp<String> concat2 = concat($col("c3"), "_", $col("c4"));
        Exp<String> concat3 = concat($col("c3"), "-", $col("c4"));
        Exp<String> concat4 = concat($col("c3"), "|", $col("c4"));
        Exp<String> concat5 = concat($col("c3"), "+", $col("c4"));
        Exp<String> concat6 = concat($col("c3"), "=", $col("c4"));
        Exp<String> concat7 = concat($col("c3"), "+", $col("c4"));
        Exp<String> concat8 = concat($col("c3"), "*", $col("c4"));

        return df.cols("C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8")
                .select(concat1, concat2, concat3, concat4, concat5, concat6, concat7, concat8)
                .materialize()
                .iterator();
    }
}
