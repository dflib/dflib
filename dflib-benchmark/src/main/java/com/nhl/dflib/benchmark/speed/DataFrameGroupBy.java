package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.aggregate.Aggregator;
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

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameGroupBy {

    @Param("1000000")
    public int rows;

    @Param("500")
    public int groups;

    private DataFrame df;
    private GroupBy gb;

    @Setup
    public void setUp() {

        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        df = DataGenerator.columnarDF(rows,
                ValueMaker.intSeq(),
                ValueMaker.stringSeq(),
                // keep the number of categories relatively low
                ValueMaker.randomIntSeq(groups),
                ValueMaker.constStringSeq(string));

        gb = df.group("c2");
    }

    @Benchmark
    public Object groupBy() {
        return df.group("c2");
    }

    @Benchmark
    public Object aggregate_by_name() {
        return gb.agg(Aggregator.sum("c0"))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggregate_by_pos() {
        return gb.agg(Aggregator.sum(0))
                .materialize()
                .iterator();
    }
}
