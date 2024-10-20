package org.dflib.benchmark.speed;

import org.dflib.series.BitSet;
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

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Thread)
public class BitSetOperations {

    @Param("500000")
    public int size;

    public int sizeInLong;

    @Param("123456")
    public int index;

    @Param("123556")
    public int indexEmpty;

    BitSet bitSet;
    boolean[] boolSet;
    java.util.BitSet javaBitSet;

    @Setup
    public void setUp() {
        sizeInLong = ((size - 1) >> 6) + 1;

        boolSet = new boolean[size];
        javaBitSet = new java.util.BitSet(size);

        boolSet[index + 1] = true;
        javaBitSet.set(index + 1);

        bitSet = new BitSet(javaBitSet.toLongArray(), size);
    }

    @Benchmark
    public BitSet bitSet_create() {
        return new BitSet(new long[sizeInLong], size);
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
    public java.util.BitSet javaBitSet_create() {
        return new java.util.BitSet(size);
    }
}
