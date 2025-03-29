package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.benchmark.ValueMaker;
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
public class DataFrameVConcat {

    @Param("1000000")
    public int rows;

    private DataFrame df1;
    private DataFrame df2;
    private DataFrame df3;

    private DataFrame df4;
    private DataFrame df5;
    private DataFrame df6;

    @Setup
    public void setUp() {
        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        Series<Integer> c10 = ValueMaker.intSeq().series(rows + 5);
        Series<String> c11 = ValueMaker.stringSeq().series(rows + 5);
        Series<String> c13 = ValueMaker.constSeq(string).series(rows + 5);

        df1 = DataFrame
                .byColumn("c0", "c1", "c2")
                .of(c10, c11, c13);

        Series<Integer> c20 = ValueMaker.intSeq().series(rows - 5);
        Series<String> c21 = ValueMaker.stringSeq().series(rows - 5);
        Series<String> c23 = ValueMaker.constSeq(string).series(rows - 5);

        df2 = DataFrame
                .byColumn("c0", "c1", "c2")
                .of(c20, c21, c23);

        Series<Integer> c30 = ValueMaker.intSeq().series(rows - 5);
        Series<String> c31 = ValueMaker.stringSeq().series(rows - 5);
        Series<String> c34 = ValueMaker.constSeq(string).series(rows - 5);

        df3 = DataFrame
                .byColumn("c0", "c1", "c4")
                .of(c30, c31, c34);

        DoubleSeries c40 = ValueMaker.doubleSeq().doubleSeries(rows + 5);
        IntSeries c41 = ValueMaker.intSeq().intSeries(rows + 5);

        df4 = DataFrame
                .byColumn("c0", "c1")
                .of(c40, c41);

        DoubleSeries c50 = ValueMaker.doubleSeq().doubleSeries(rows - 5);
        IntSeries c51 = ValueMaker.intSeq().intSeries(rows - 5);

        df5 = DataFrame
                .byColumn("c0", "c1")
                .of(c50, c51);

        DoubleSeries c60 = ValueMaker.doubleSeq().doubleSeries(rows - 5);
        IntSeries c61 = ValueMaker.intSeq().intSeries(rows - 5);

        df6 = DataFrame
                .byColumn("c0", "c2")
                .of(c60, c61);
    }

    @Benchmark
    public Object vConcat() {
        return df1
                .vConcat(df3)
                .materialize().iterator();
    }

    @Benchmark
    public Object vConcat_SameCols() {
        return df1
                .vConcat(df2)
                .materialize().iterator();
    }

    @Benchmark
    public Object vConcat_Primitives() {
        return df4
                .vConcat(df6)
                .materialize().iterator();
    }

    @Benchmark
    public Object vConcat_Primitives_SameCols() {
        return df4
                .vConcat(df5)
                .materialize().iterator();
    }
}
