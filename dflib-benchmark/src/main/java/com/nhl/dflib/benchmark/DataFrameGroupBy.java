package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
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
public class DataFrameGroupBy {

    private static final int DATASET_SIZE = 1_000_000;

    private DataFrame df;
    private GroupBy gb;

    @Setup
    public void setUp() {
        Index index = Index.withNames("id", "data", "rev_id", "text");
        df = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(DATASET_SIZE), false)
        );

        gb = df.groupBy("rev_id");
    }

    @Benchmark
    public Object groupBy() {
        return df.groupBy("rev_id");
    }

    @Benchmark
    public Object aggregate() {
        return gb.agg(Aggregator.sum("id"))
                .materialize()
                .iterator();
    }
}
