package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
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
public class DataFrameMisc {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {
        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        Series<Integer> c0 = ValueMaker.intSeq().series(rows);
        Series<String> c1 = ValueMaker.stringSeq().series(rows);
        Series<Integer> c2 = ValueMaker.randomIntSeq((rows) / 2).series(rows);
        Series<String> c3 = ValueMaker.constStringSeq(string).series(rows);

        df = DataFrame.byColumn("c0", "c1", "c2", "c3").array(c0, c1, c2, c3);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Object head() {
        return df
                .head(100)
                .materialize()
                .iterator();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public long height() {
        return df.height();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public long width() {
        return df.width();
    }
}


