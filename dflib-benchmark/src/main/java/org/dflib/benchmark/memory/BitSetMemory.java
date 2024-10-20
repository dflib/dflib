package org.dflib.benchmark.memory;

import org.dflib.benchmark.memory.benchmark.MemoryTest;
import org.dflib.series.BitSet;

public class BitSetMemory extends MemoryTest {

    private static final int size = 5_000_000;

    public static void main(String[] args) {
        BitSetMemory test = new BitSetMemory();

        test.run(" FixedSizeBitSet", test::createBitSet, size);
        test.run("java.util.BitSet", test::createJavaBitSet, size);
        test.run("       boolean[]", test::createBooleanArray, size);
    }

    BitSet createBitSet() {
        return new BitSet(new long[size >> 6], size);
    }

    boolean[] createBooleanArray() {
        return new boolean[size];
    }

    java.util.BitSet createJavaBitSet() {
        return new java.util.BitSet(size);
    }
}
