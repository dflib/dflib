package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.aggregate.Aggregator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameOperation {

    private static final int DATASET_SIZE = 5_000_000;

    private DataFrame df;

    @Setup
    public void setUp() {
        Index index = Index.withNames("id", "data", "rev_id", "text");
        df = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(DATASET_SIZE), false)
        );
    }

    @Benchmark
    public Object median() {
        return df.agg(Aggregator.median("id"));
    }

    @Benchmark
    public Object filter() {
        return df
                .filterByColumn("id", (Integer i) -> i % 2 == 0)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapColumn() {
        return df
                .mapColumn("data", (String s) -> s.split("_"))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object groupBy() {
        return df
                .groupBy("rev_id")
                .agg(Aggregator.sum("id"))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object medianWithFilter() {
        return df
                .filterByColumn("id", (Integer i) -> i % 2 == 0)
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


