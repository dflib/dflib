package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.KeyMapper;
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

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameMerge {

    private static final int DATASET_SIZE = 1_000_000;

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
    public Object indexJoinLeft() {
        return df1
                .join(df2, KeyMapper.keyColumn("id"), KeyMapper.keyColumn("rev_id"), JoinType.left)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object indexJoinRight() {
        return df1
                .join(df2, KeyMapper.keyColumn("id"), KeyMapper.keyColumn("rev_id"), JoinType.right)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object indexJoinInner() {
        return df1
                .join(df2, KeyMapper.keyColumn("id"), KeyMapper.keyColumn("rev_id"), JoinType.inner)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object indexJoinFull() {
        return df1
                .join(df2, KeyMapper.keyColumn("id"), KeyMapper.keyColumn("rev_id"), JoinType.full)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object hConcat() {
        return df1
                .hConcat(df2)
                .materialize().iterator();
    }

}
