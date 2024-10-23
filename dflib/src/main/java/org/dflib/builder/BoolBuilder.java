package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.series.BooleanBitsetSeries;

public class BoolBuilder {

    private static final int INDEX_BIT_SHIFT = 6;

    private BoolBuilder() {
    }

    public static BooleanSeries buildSeries(Generator generator, int size) {
        if (size == 0) {
            return Series.ofBool();
        }
        return new BooleanBitsetSeries(fill(generator, size), size);
    }

    private static long[] fill(Generator generator, int size) {
        long[] data = new long[1 + ((size - 1) >> INDEX_BIT_SHIFT)];
        long element = 0L;
        for (int i = 0; i < size; i += Long.SIZE) {
            for (int j = i; j < i + Long.SIZE && j < size; j++) {
                if (generator.get(j)) {
                    element |= 1L << j;
                }
            }
            data[i >> INDEX_BIT_SHIFT] = element;
            element = 0L;
        }
        return data;
    }

    @FunctionalInterface
    public interface Generator {
        boolean get(int i);
    }
}
