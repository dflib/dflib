package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

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

        this.df = DataFrame.newFrame("c0", "c1", "c2", "c3")
                .columns(c0, c1, c2, c3);
    }

    @Benchmark
    public Object sortIntByRowValueMapper() {
        return df.sort(RowToValueMapper.columnReader(0))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortIntByColumn() {
        return df.sort(0, true)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortStringByRowValueMapper() {
        return df.sort(RowToValueMapper.columnReader(1))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sortStringByColumn() {
        return df.sort(1, true)
                .materialize()
                .iterator();
    }
}
