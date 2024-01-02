package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.Exp;
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
public class DataFrameAgg {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        df = DataFrame.byColumn("c0", "c1", "c2").of(
                ValueMaker.intSeq().series(rows),
                ValueMaker.intSeq().series(rows),
                ValueMaker.reverseIntSeq().series(rows)
        );
    }

    @Benchmark
    public Object median() {
        return df.agg(Exp.$int("c0").median());
    }

    @Benchmark
    public Object medianWithFilter() {
        return df
                .selectRows(Exp.$int("c0").mod(2).eq(0))
                .agg(Exp.$int(0).median());
    }

    @Benchmark
    public Object oneColumn() {
        return df.agg(
                Exp.$int("c0").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object threeColumn() {
        return df.agg(
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum()
        ).materialize().iterator();
    }

    @Benchmark
    public Object sixColumn() {
        return df.agg(
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum(),
                Exp.$int("c0").sum(),
                Exp.$int("c1").sum(),
                Exp.$int("c2").sum()
        ).materialize().iterator();
    }
}


