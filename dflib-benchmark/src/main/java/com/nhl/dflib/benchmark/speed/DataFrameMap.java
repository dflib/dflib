package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.RowMapper;
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
public class DataFrameMap {

    @Param("1000000")
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

        df = DataFrame.byColumn("c0", "c1", "c2", "c3").of(c0, c1, c2, c3);    }

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
                .convertColumn("c2", Exp.$col("c2").mapVal(v -> 1))
                .materialize()
                .iterator();
    }
}
