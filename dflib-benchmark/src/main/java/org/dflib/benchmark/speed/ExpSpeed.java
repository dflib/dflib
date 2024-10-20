package org.dflib.benchmark.speed;

import org.dflib.DateExp;
import org.dflib.Exp;
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

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class ExpSpeed {

    @Param("5000000")
    public int rows;

    private Series<LocalDate> s1;
    private DateExp addOneDay = Exp.$date(0).plusDays(1);
    private DateExp addOneMonth = Exp.$date(0).plusMonths(1);
    private DateExp addOneYear = Exp.$date(0).plusYears(1);

    @Setup
    public void setUp() {
        s1 = ValueMaker.dateSeq().dateSeries(rows);
    }

    @Benchmark
    public Object addOneDay() {
        return addOneDay.eval(s1);
    }

    @Benchmark
    public Object addOneMonth() {
        return addOneMonth.eval(s1);
    }

    @Benchmark
    public Object addOneYear() {
        return addOneYear.eval(s1);
    }
}
