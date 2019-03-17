package com.nhl.dflib.benchmark.speed.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.DataGenerator;
import com.nhl.dflib.map.RowToValueMapper;
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
        df = DataGenerator.rowDFWithMixedData(rows);
    }

    @Benchmark
    public Object sort() {
        return df
                .sort(RowToValueMapper.columnReader(2))
                .materialize()
                .iterator();
    }
}
