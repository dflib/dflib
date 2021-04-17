package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameIntOperation {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        this.df = DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.intSeq().intSeries(rows),
                ValueMaker.randomIntSeq(rows / 2).intSeries(rows));
    }

    @Benchmark
    public Object filter() {
        // filtering by "int" column is a two-step op - index, then select...
        return df.selectRows(df.getColumnAsInt("c0").indexInt(i -> i % 2 == 0))
                .materialize()
                .iterator();
    }
}
