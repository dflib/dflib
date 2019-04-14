package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.benchmark.DataGenerator;
import com.nhl.dflib.benchmark.ValueMaker;
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

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameIntOperation {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        Index columns = Index.forLabels("c0", "c1");
        df = DataFrame.forColumns(columns,
                DataGenerator.intSeries(rows, ValueMaker.intSeq()),
                DataGenerator.intSeries(rows, ValueMaker.randomIntSeq(rows / 2)));
    }

    @Benchmark
    public Object filter() {
        // filtering by "int" column is a two-step op - index, then select...
        return df.select(df.getColumnAsInt("c0").indexInt(i -> i % 2 == 0))
                .materialize()
                .iterator();
    }
}
