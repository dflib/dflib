package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.DataGenerator;
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
        df = DataGenerator.columnarDFWithMixedData(rows);
    }

    @Benchmark
    public Object map() {
        return df
                .map(df.getColumnsIndex(), RowMapper.copy())
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object mapWithChange() {
        return df
                .map(df.getColumnsIndex(), RowMapper.copy().and((s, t) -> t.set(2, 1)))
                .materialize()
                .iterator();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Object convertColumn() {
        return df
                // using cheap "map" function to test benchmark DF overhead
                .convertColumn("c2", (Integer i) -> 1)
                .materialize()
                .iterator();
    }
}
