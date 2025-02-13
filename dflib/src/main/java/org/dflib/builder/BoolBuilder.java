package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.series.BooleanBitsetSeries;
import org.dflib.series.FalseSeries;

/**
 * Utility class that creates boolean bitset-based series.
 * <p>
 * From the functional point of view this is a specialized and optimized version of {@link BoolAccum} intended for an internal use.
 * <p>
 * Main difference with the accumulators API is that builder should know the final size of the series.
 *
 * @see BooleanBitsetSeries
 * @see BoolAccum
 * @since 2.0.0
 */
public class BoolBuilder {

    private static final int INDEX_BIT_SHIFT = 6;

    private BoolBuilder() {
    }

    /**
     * Build boolean series of a fixed size, using provided function as a value generator.
     * Generator function will be called with values from 0 to size - 1 as a parameter.
     *
     * @param generator function that generates values, must be capable of providing at least {@code size} elements
     * @param size      target size of the generated series
     * @return boolean series
     */
    public static BooleanSeries buildSeries(BoolGenerator generator, int size) {
        if (size == 0) {
            return new BooleanBitsetSeries(new long[0], 0);
        }

        long[] data = fill(generator, size);
        return data.length == 0
                ? new FalseSeries(size)
                : new BooleanBitsetSeries(data, size);
    }

    private static long[] fill(BoolGenerator generator, int size) {
        int i = 0;
        while (i < size && !generator.get(i)) {
            // noop, just skip initial `false` values
            i++;
        }
        // no values at all
        if (i == size) {
            return new long[0];
        }

        long[] data = new long[1 + ((size - 1) >> INDEX_BIT_SHIFT)];
        long element = 0L;

        // init first element out of the loop, to align boundaries in the main loop below
        int idx = i >> INDEX_BIT_SHIFT;
        for(; i < (idx + 1) << INDEX_BIT_SHIFT; i++) {
            if (generator.get(i)) {
                data[idx] |= 1L << i;
            }
        }

        for (; i < size; i += Long.SIZE) {
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

    /**
     * Function to generate values for a boolean series
     */
    @FunctionalInterface
    public interface BoolGenerator {
        boolean get(int i);
    }
}
