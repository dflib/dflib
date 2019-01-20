package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
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
import java.util.stream.StreamSupport;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameConcat {

    @Param("1000000")
    public int rows;

    private DataFrame df1;
    private DataFrame df2;

    @Setup
    public void setUp() {
        Index index = Index.withNames("id", "data", "rev_id", "text");
        df1 = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(rows), false)
        );
        df2 = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(rows), false)
        );
    }

    @Benchmark
    public Object hConcat() {
        return df1
                .hConcat(df2)
                .materialize().iterator();
    }

    @Benchmark
    public Object vConcat() {
        return df1
                .vConcat(df2)
                .materialize().iterator();
    }

}
