package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.dflib.Exp.$int;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameSelectRows {

    @Param("5000000")
    public int rows;

    private DataFrame df;

    @Setup
    public void setUp() {

        IntSeries c0 = ValueMaker.intSeq().intSeries(rows);
        Series<Integer> c1 = Series.ofIterable(c0);
        Series<String> c2 = ValueMaker.stringSeq().series(rows);
        Series<String> c3 = ValueMaker.constStringSeq("abc").series(rows);

        df = DataFrame
                .byColumn("c0", "c1", "c2", "c3")
                .of(c0, c1, c2, c3);
    }

    @Benchmark
    public Object selectRowsIntValuePredicate() {
        return df.selectRows("c0", (Integer i) -> i % 2 == 0)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectRowsIntegerValuePredicate() {
        return df.selectRows("c1", (Integer i) -> i % 2 == 0)
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectRowsIntConditionViaExp() {
        return df.selectRows($int("c0").mod(2).eq(0))
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object selectRowsIntegerConditionViaExp() {
        return df.selectRows($int("c1").mod(2).eq(0))
                .materialize()
                .iterator();
    }
}


