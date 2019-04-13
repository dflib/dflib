package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;

/**
 * A series that is optimized to store primitive int values.
 *
 * @since 0.6
 */
public interface IntSeries extends Series<Integer> {


    int getInt(int index);

    void copyToInt(int[] to, int fromOffset, int toOffset, int len);

    IntSeries materializeInt();

    IntSeries concatInt(IntSeries... other);

    IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive);

    IntSeries headInt(int len);

    IntSeries tailInt(int len);

    default IntSeries selectInt(int... positions) {
        return selectInt(new IntArraySeries(positions));
    }

    IntSeries selectInt(IntSeries positions);
}
