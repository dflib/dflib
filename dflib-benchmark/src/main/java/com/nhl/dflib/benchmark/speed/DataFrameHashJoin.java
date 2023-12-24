package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.benchmark.ValueMaker;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class DataFrameHashJoin {

    @Param("1000000")
    public int rows;

    @Param("500000")
    public int joinGroups;

    private DataFrame df1;
    private DataFrame df2;

    @Setup
    public void setUp() {

        // this column is used in join. Must be predictable and overlap with keys in df2
        Series<Integer> c10 = ValueMaker.intSeq(joinGroups / 2, (int) (joinGroups * 1.5)).series(rows);
        Series<String> c11 = ValueMaker.stringSeq().series(rows);
        // keep the number of categories relatively low
        Series<Integer> c12 = ValueMaker.randomIntSeq(rows / 2).series(rows);
        Series<String> c13 = ValueMaker.constStringSeq("abcd").series(rows);

        df1 = DataFrame.byColumn("c0", "c1", "c2", "c3").of(c10, c11, c12, c13);

        Series<Integer> c20 = ValueMaker.intSeq().series(rows);
        Series<String> c21 = ValueMaker.stringSeq().series(rows);
        // this column is used in join. Must be predictable and overlap with keys in df1
        Series<Integer> c22 = ValueMaker.intSeq(1, joinGroups).series(rows);
        Series<String> c23 = ValueMaker.constStringSeq("abcd").series(rows);


        df2 = DataFrame.byColumn("c0", "c1", "c2", "c3").of(c20, c21, c22, c23);
    }

    @Benchmark
    public Object leftJoin() {
        return df1
                .leftJoin(df2)
                .on("c0", "c2")
                .select()
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object leftJoin_ByPosition() {
        return df1
                .leftJoin(df2)
                .on(0, 2)
                .select()
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object rightJoin() {
        return df1
                .rightJoin(df2)
                .on("c0", "c2")
                .select()
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object innerJoin() {
        return df1
                .innerJoin(df2)
                .on("c0", "c2")
                .select()
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object innerJoin_Subset() {
        return df1
                .innerJoin(df2)
                .on("c0", "c2")
                .select("c0", "c1_", "c2")
                .materialize()
                .iterator();
    }

    @Benchmark
    public Object fullJoin() {
        return df1
                .fullJoin(df2)
                .on("c0", "c2")
                .select()
                .materialize()
                .iterator();
    }
}
