package org.dflib.benchmark.speed;

import org.dflib.Series;
import org.dflib.builder.BoolAccum;
import org.dflib.series.BooleanBitsetSeries;
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
    public int indexTrue;

    @Param("123556")
    public int indexFalse;

    BooleanBitsetSeries bitSet;
    boolean[] boolSet;
    java.util.BitSet javaBitSet;

    @Setup
    public void setUp() {

        // see "BoolAccum.sizeInLongs(int)"
        sizeInLong = ((size - 1) >> 6) + 1;

        boolSet = new boolean[size];
        javaBitSet = new java.util.BitSet(size);

        boolSet[indexTrue] = true;
        javaBitSet.set(indexTrue);

        BoolAccum accum = new BoolAccum(size);
        accum.fill(Series.ofBool(boolSet), 0, 0, size);
        bitSet = (BooleanBitsetSeries) accum.toSeries();
    }

    @Benchmark
    public BooleanBitsetSeries bitSet_create() {
        return new BooleanBitsetSeries(new long[sizeInLong], size);
    }

    @Benchmark
    public boolean bitSet_getTrue() {
        return bitSet.get(indexTrue);
    }

    @Benchmark
    public boolean bitSet_getFalse() {
        return bitSet.get(indexFalse);
    }

    @Benchmark
    public boolean[] boolArray_create() {
        return new boolean[size];
    }

    @Benchmark
    public void boolArray_set() {
        boolSet[indexTrue] = true;
    }

    @Benchmark
    public boolean boolArray_getTrue() {
        return boolSet[indexTrue];
    }

    @Benchmark
    public void javaBitSet_set() {
        javaBitSet.set(indexTrue, true);
    }

    @Benchmark
    public boolean javaBitSet_getTrue() {
        return javaBitSet.get(indexTrue);
    }

    @Benchmark
    public boolean javaBitSet_getFalse() {
        return javaBitSet.get(indexFalse);
    }

    @Benchmark
    public java.util.BitSet javaBitSet_create() {
        return new java.util.BitSet(size);
    }
}
