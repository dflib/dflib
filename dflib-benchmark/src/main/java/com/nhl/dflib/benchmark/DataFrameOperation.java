package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.benchmark.data.RowByRowSequence;
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
public class DataFrameOperation {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        df = RowByRowSequence.dfWithMixedData(rows);
    }

    @Benchmark
    public Object median() {
        return df.agg(Aggregator.median("c0"));
    }

    @Benchmark
    public Object filter() {
        return df
                .filterByColumn("c0", (Integer i) -> i % 2 == 0)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object medianWithFilter() {
        return df
                .filterByColumn("c0", (Integer i) -> i % 2 == 0)
                .agg(Aggregator.median(0));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Object head() {
        return df
                .head(100)
                .materialize()
                .iterator();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public long height() {
        return df.height();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public long width() {
        return df.width();
    }
}


