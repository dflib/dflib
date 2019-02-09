package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;
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

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameMap {

    @Param("1000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        Index index = Index.withNames("id", "data", "rev_id", "text");
        df = DataFrame.fromStream(
                index,
                StreamSupport.stream(new DataSetSpliterator(rows), false)
        );
    }

    @Benchmark
    public Object map() {
        return df
                .map(RowMapper.copy())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapWithChange() {
        return df
                .map(RowMapper.copy().and((c, s, t) -> c.set(t, 2, 1)))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapColumn() {
        return df
                // using cheap "map" function to test benchmark DF overhead
                .mapColumn("rev_id", (Integer i) -> 1)
                .materialize()
                .iterator();
    }
}
