package org.dflib.benchmark.speed;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.benchmark.ValueMaker;
import org.dflib.print.Printer;
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
public class TabularPrinter {

    @Param("5000000")
    public int rows;

    private DataFrame df;
    private Printer truncatedRowsPrinter;
    private Printer truncatedRowsAndValsPrinter;


    @Setup
    public void setUp() {

        IntSeries c1 = ValueMaker.randomIntSeq(rows / 2).intSeries(rows);
        Series<Integer> c2 = Series.ofIterable(c1);
        Series<String> c3 = ValueMaker.stringSeq().series(rows);

        df = DataFrame.byColumn("c1", "c2", "c3").of(c1, c2, c3);
        truncatedRowsPrinter = Printers.tabular(rows / 2, 100);
        truncatedRowsAndValsPrinter = Printers.tabular(rows / 2, 4);
    }

    @Benchmark
    public Object truncatedRows() {
        return truncatedRowsPrinter.print(df);
    }

    @Benchmark
    public Object truncatedRowsAndVals() {
        return truncatedRowsAndValsPrinter.print(df);
    }
}
