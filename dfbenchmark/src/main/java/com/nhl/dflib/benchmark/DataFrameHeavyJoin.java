package com.nhl.dflib.benchmark;

import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.join.JoinPredicate;
import com.nhl.dflib.join.JoinSemantics;
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

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameHeavyJoin {

    private static final int DATASET_SIZE = 5_000;

    private DataFrame df1;
    private DataFrame df2;

    @Setup
    public void setUp() {
        Index index = Index.withNames("id", "data", "rev_id", "text");
        df1 = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(DATASET_SIZE), false)
        );
        df2 = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(DATASET_SIZE), false)
        );
    }

    @Benchmark
    public Object leftJoin() {
        return df1
                .join(df2, JoinPredicate.on("rev_id", "id"), JoinSemantics.left)
                .materialize().iterator();
    }

    @Benchmark
    public Object rightJoin() {
        return df1
                .join(df2, JoinPredicate.on("rev_id", "id"), JoinSemantics.right)
                .materialize().iterator();
    }

    @Benchmark
    public Object innerJoin() {
        return df1
                .join(df2, JoinPredicate.on("rev_id", "id"), JoinSemantics.inner)
                .materialize().iterator();
    }

    @Benchmark
    public Object fullJoin() {
        return df1
                .join(df2, JoinPredicate.on("rev_id", "id"), JoinSemantics.inner)
                .materialize().iterator();
    }

}
