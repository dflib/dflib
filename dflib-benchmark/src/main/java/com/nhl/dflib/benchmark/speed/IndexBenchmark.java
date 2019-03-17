package com.nhl.dflib.benchmark.speed;

import com.nhl.dflib.Index;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Thread)
public class IndexBenchmark {

    private Index index;
    private Object[] data;

    @Setup
    public void setUp() {
        // TODO: variable number of columns
        index = Index.withNames("id", "data", "rev_id", "text");
        data = new Object[]{1, "string", 5, "another string"};
    }

    @Benchmark
    public Object getByName() {

        index.get(data, "id");
        index.get(data, "data");
        index.get(data, "rev_id");
        index.get(data, "text");

        return true;
    }

    @Benchmark
    public Object getByPosition() {
        index.get(data, 0);
        index.get(data, 1);
        index.get(data, 2);
        index.get(data, 3);
        return true;
    }
}
