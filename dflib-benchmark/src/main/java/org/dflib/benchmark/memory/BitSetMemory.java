package org.dflib.benchmark.memory;

import org.dflib.benchmark.memory.benchmark.MemoryTest;
import org.dflib.series.FixedSizeBitSet;

import java.util.BitSet;

public class BitSetMemory extends MemoryTest {

    private static final int size = 5_000_000;

    public static void main(String[] args) {
        BitSetMemory test = new BitSetMemory();

        test.run(" FixedSizeBitSet", test::createBitSet, size);
        test.run("java.util.BitSet", test::createJavaBitSet, size);
        test.run("       boolean[]", test::createBooleanArray, size);
    }

    FixedSizeBitSet createBitSet() {
        return new FixedSizeBitSet(new long[size >> 6], size);
    }

    boolean[] createBooleanArray() {
        return new boolean[size];
    }

    BitSet createJavaBitSet() {
        return new BitSet(size);
    }
}
