package com.nhl.dflib;

import com.nhl.dflib.filter.IntPredicate;
import com.nhl.dflib.filter.ValuePredicate;
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

    default IntSeries selectInt(int... positions) {
        return selectInt(new IntArraySeries(positions));
    }

    IntSeries selectInt(IntSeries positions);

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexInt(IntPredicate predicate);
}
