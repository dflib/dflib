package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
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

import static org.dflib.Exp.$int;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class GroupBy {

    @Param("1000000")
    public int rows;

    @Param("500")
    public int groups;

    private DataFrame df;
    private org.dflib.GroupBy gb;

    @Setup
    public void setUp() {

        df = DataFrame.byColumn("e0", "c0", "c1", "c2").of(
                ValueMaker.randomIntSeq(groups).series(rows),
                ValueMaker.intSeq().series(rows),
                ValueMaker.intSeq().series(rows),
                ValueMaker.reverseIntSeq().series(rows)
        );
        gb = df.group("e0");
    }

    @Benchmark
    public Object groupBy() {
        return df.group("e0");
    }

    @Benchmark
    public Object aggSumByName() {
        return gb.agg($int("e0").sum())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggSumByPos() {
        return gb.agg($int(0).sum())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggFirst() {
        return gb.agg($int(0).first())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggColumn1() {
        return gb.agg(
                $int("c0").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object aggColumn3() {
        return gb.agg(
                $int("c0").sum(),
                $int("c1").sum(),
                $int("c2").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object aggColumn6() {
        return gb.agg(
                $int("c0").sum(),
                $int("c1").sum(),
                $int("c2").sum(),
                $int("c0").sum(),
                $int("c1").sum(),
                $int("c2").sum()
        ).materialize().iterator();
    }
}
