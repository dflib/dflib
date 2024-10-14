package org.dflib.benchmark.speed;

import org.dflib.series.FixedSizeBitSet;
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

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Thread)
public class BitSetOperations {

    @Param("500000")
    public int size;

    @Param("123456")
    public int index;

    @Param("123556")
    public int indexEmpty;

    FixedSizeBitSet bitSet;
    boolean[] boolSet;
    BitSet javaBitSet;

    @Setup
    public void setUp() {
        boolSet = new boolean[size];
        bitSet = new FixedSizeBitSet(size);
        javaBitSet = new BitSet(size);

        boolSet[index + 1] = true;
        bitSet.set(index + 1);
        javaBitSet.set(index + 1);
    }

    @Benchmark
    public FixedSizeBitSet bitSet_create() {
        return new FixedSizeBitSet(size);
    }

    @Benchmark
    public void bitSet_set() {
        bitSet.set(index, true);
    }

    @Benchmark
    public boolean bitSet_get() {
        return bitSet.get(index);
    }

    @Benchmark
    public boolean bitSet_getEmpty() {
        return bitSet.get(indexEmpty);
    }

    @Benchmark
    public boolean[] boolArray_create() {
        return new boolean[size];
    }

    @Benchmark
    public void boolArray_set() {
        boolSet[index] = true;
    }

    @Benchmark
    public boolean boolArray_get() {
        return boolSet[index];
    }

    @Benchmark
    public void javaBitSet_set() {
        javaBitSet.set(index, true);
    }

    @Benchmark
    public boolean javaBitSet_get() {
        return javaBitSet.get(index);
    }

    @Benchmark
    public boolean javaBitSet_getEmpty() {
        return javaBitSet.get(indexEmpty);
    }

    @Benchmark
    public BitSet javaBitSet_create() {
        return new BitSet(size);
    }
}

/*
 * Benchmark                          (index)  (size)  Mode  Cnt     Score     Error  Units
 * BitSetOperations.bitSet_create      123456  500000  avgt    6   500.422 ±  47.380  ns/op
 * BitSetOperations.bitSet_get         123456  500000  avgt    6     0.747 ±   0.013  ns/op
 * BitSetOperations.bitSet_set         123456  500000  avgt    6     0.748 ±   0.016  ns/op
 * BitSetOperations.boolArray_create   123456  500000  avgt    6  3810.983 ± 188.951  ns/op
 * BitSetOperations.boolArray_get      123456  500000  avgt    6     0.669 ±   0.037  ns/op
 * BitSetOperations.boolArray_set      123456  500000  avgt    6     0.574 ±   0.058  ns/op
 *
 * Memory bytes/index
 *    BitSet   0.10
 * boolean[]   1.68
 *
 * Benchmark                             (size)  Mode  Cnt     Score    Error  Units
 * BitSetOperations.bitSet_create        500000  avgt    6   485.833 ±  2.659  ns/op
 * BitSetOperations.bitSet_get           500000  avgt    6     0.755 ±  0.030  ns/op
 * BitSetOperations.bitSet_getEmpty      500000  avgt    6     0.746 ±  0.002  ns/op
 * BitSetOperations.bitSet_set           500000  avgt    6     0.772 ±  0.100  ns/op
 * BitSetOperations.boolArray_create     500000  avgt    6  3659.246 ± 49.309  ns/op
 * BitSetOperations.boolArray_get        500000  avgt    6     0.646 ±  0.001  ns/op
 * BitSetOperations.boolArray_set        500000  avgt    6     0.561 ±  0.043  ns/op
 * BitSetOperations.javaBitSet_create    500000  avgt    6   487.792 ±  3.683  ns/op
 * BitSetOperations.javaBitSet_get       500000  avgt    6     0.840 ±  0.029  ns/op
 * BitSetOperations.javaBitSet_getEmpty  500000  avgt    6     0.554 ±  0.001  ns/op
 * BitSetOperations.javaBitSet_set       500000  avgt    6     0.831 ±  0.003  ns/op
 */