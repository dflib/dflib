package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.benchmark.DataGenerator;
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

        this.df = DataGenerator.df(rows,
                ValueMaker.randomIntSeq(rows / 2),
                ValueMaker.semiRandomStringSeq("x", rows / 2),
                ValueMaker.intSeq(),
                ValueMaker.stringSeq());
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
