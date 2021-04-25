package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Series;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

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

        Series<Integer> c0 = ValueMaker.intSeq().series(rows);
        Series<String> c1 = ValueMaker.stringSeq().series(rows);
        // keep the number of categories relatively low
        Series<Integer> c2 = ValueMaker.randomIntSeq(groups).series(rows);
        Series<String> c3 = ValueMaker.constStringSeq(string).series(rows);

        df = DataFrame.newFrame("c0", "c1", "c2", "c3")
                .columns(c0, c1, c2, c3);

        gb = df.group("c2");
    }

    @Benchmark
    public Object groupBy() {
        return df.group("c2");
    }

    @Benchmark
    public Object sumLongByName() {
        return gb.agg(Aggregator.sumLong("c0"))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object sumLongByPos() {
        return gb.agg(Aggregator.sumLong(0))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object aggregateFirst() {
        return gb.agg(Aggregator.first(0))
                .materialize()
                .iterator();
    }
}
