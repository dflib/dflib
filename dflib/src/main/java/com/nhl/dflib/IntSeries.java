package com.nhl.dflib;

import com.nhl.dflib.filter.IntPredicate;
import com.nhl.dflib.series.IntArraySeries;

/**
 * A Series optimized to store and access primitive int values without <code>java.lang.Integer</code> wrapper. Can also
 * pose as "Series&lt;Integer>", although this is not the most efficient way of using these Series.
 *
 * @since 0.6
 */
public interface IntSeries extends Series<Integer> {

    static IntSeries forInts(int... ints) {
        return new IntArraySeries(ints);
    }

    int getInt(int index);

    void copyToInt(int[] to, int fromOffset, int toOffset, int len);

    IntSeries materializeInt();

    IntSeries concatInt(IntSeries... other);

    IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive);

    IntSeries headInt(int len);

    IntSeries tailInt(int len);

    IntSeries filterInt(IntPredicate predicate);

    default IntSeries selectInt(int... positions) {
        return selectInt(new IntArraySeries(positions));
    }

    IntSeries selectInt(IntSeries positions);
}
