package org.dflib.builder;

import org.dflib.series.BitSet;
import org.dflib.series.BooleanBitsetSeries;

public class BoolBuilder {

    private static final int INDEX_BIT_SHIFT = 6;

    private final int size;
    private final Generator generator;

    public BoolBuilder(Generator generator, int size) {
        this.size = size;
        this.generator = generator;
    }

    public BooleanBitsetSeries buildSeries() {
        BitSet bitSet = size == 0
                ? BitSet.EMPTY
                : fill();
        return new BooleanBitsetSeries(bitSet);
    }

    private BitSet fill() {
        long[] data = new long[1 + ((size - 1) >> INDEX_BIT_SHIFT)];
        long element = 0L;
        for(int i = 0; i < size; i += Long.SIZE) {
            for(int j = i;  j < i + Long.SIZE && j < size; j++) {
                if(generator.get(j)) {
                    element |= 1L << j;
                }
            }
            data[i >> INDEX_BIT_SHIFT] = element;
            element = 0L;
        }

        return new BitSet(data, size);
    }

    @FunctionalInterface
    public interface Generator {
        boolean get(int i);
    }
}
