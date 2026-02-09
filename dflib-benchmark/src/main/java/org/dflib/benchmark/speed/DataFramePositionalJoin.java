package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
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
public class DataFramePositionalJoin {

    @Param("1000000")
    public int rows;

    private DataFrame df1;
    private DataFrame df2;

    @Setup
    public void setUp() {
        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        Series<Integer> c10 = ValueMaker.intSeq().series(rows + 5);
        Series<String> c11 = ValueMaker.stringSeq().series(rows + 5);
        Series<Integer> c12 = ValueMaker.randomIntSeq((rows + 5) / 2).series(rows + 5);
        Series<String> c13 = ValueMaker.constSeq(string).series(rows + 5);

        df1 = DataFrame.byColumn("c0", "c1", "c2", "c3")
                .of(c10, c11, c12, c13);

        Series<Integer> c20 = ValueMaker.intSeq().series(rows - 5);
        Series<String> c21 = ValueMaker.stringSeq().series(rows - 5);
        Series<Integer> c22 = ValueMaker.randomIntSeq((rows - 5) / 2).series(rows - 5);
        Series<String> c23 = ValueMaker.constSeq(string).series(rows - 5);

        df2 = DataFrame.byColumn("c0", "c1", "c2", "c3")
                .of(c20, c21, c22, c23);
    }

    @Deprecated
    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Object hConcat() {
        return df1
                .hConcat(df2)
                .materialize().iterator();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Object positionalJoin() {
        return df1
                .innerJoin(df2).positional()
                .select()
                .materialize().iterator();
    }
}
