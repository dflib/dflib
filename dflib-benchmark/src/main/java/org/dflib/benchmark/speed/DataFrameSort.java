package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.dflib.Exp.$int;

// in my tests time for iteration #3 time spikes (why?), so make sure it happens during warmup
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameSort {

    @Param("1000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {

        Series<Integer> c0 = ValueMaker.randomIntSeq(rows / 2).series(rows);
        Series<String> c1 = ValueMaker.semiRandomStringSeq("x", rows / 2).series(rows);
        Series<Integer> c2 = ValueMaker.intSeq().series(rows);
        Series<String> c3 = ValueMaker.stringSeq().series(rows);
        IntSeries c4 = ValueMaker.randomIntSeq(rows / 2).intSeries(rows);

        this.df = DataFrame.byColumn("c0", "c1", "c2", "c3", "c4").of(c0, c1, c2, c3, c4);
    }

    @Benchmark
    public Object sortIntegerByColumn() {
        return df.sort(0, true)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortStringByColumn() {
        return df.sort(1, true)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortIntegerByExp() {
        return df.sort($int("c0").asc())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortIntByExp() {
        return df.sort($int("c4").asc()).materialize().iterator();
    }
}
