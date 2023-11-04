package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.*;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

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
    private com.nhl.dflib.GroupBy gb;

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
    public Object sumByName() {
        return gb.agg(Exp.$int("e0").sum())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sumByPos() {
        return gb.agg(Exp.$int(0).sum())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggregateFirst() {
        return gb.agg(Exp.$int(0).first())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object oneColumn() {
        return gb.agg(
                Exp.$int("c0").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object threeColumn() {
        return gb.agg(
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object sixColumn() {
        return gb.agg(
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum(),
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum()
        ).materialize().iterator();
    }
}
