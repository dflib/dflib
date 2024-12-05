package org.dflib.benchmark.speed;

import org.dflib.BooleanSeries;
import org.dflib.benchmark.ValueMaker;
import org.dflib.series.BooleanArraySeries;
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
public class BooleanSeriesSelect {
    @Param("5000000")
    public int rows;

    BooleanSeries s1;
    BooleanSeries s2;
    BooleanSeries pos1;
    BooleanSeries pos2;

    @Setup
    public void setUp() {
        s1 = ValueMaker.booleanSeq().booleanSeries(rows);
        pos1 = s1.not();

        boolean[] data = new boolean[rows];
        s1.copyToBool(data, 0, 0, rows);
        s2 = new BooleanArraySeries(data);
        pos2 = s2.not();
    }

    @Benchmark
    public BooleanSeries selectBitsetWithBitset() {
        return s1.select(pos1);
    }

    @Benchmark
    public BooleanSeries selectBitsetWithArray() {
        return s1.select(pos2);
    }

    @Benchmark
    public BooleanSeries selectArrayWithArray() {
        return s2.select(pos2);
    }

    @Benchmark
    public BooleanSeries selectArrayWithBitset() {
        return s2.select(pos1);
    }

}
