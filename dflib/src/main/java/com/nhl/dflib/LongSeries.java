package com.nhl.dflib;

import com.nhl.dflib.series.LongArraySeries;

/**
 * A Series optimized to store and access primitive long values without <code>java.lang.Long</code> wrapper. Can also
 * pose as "Series&lt;Long>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface LongSeries extends Series<Long> {

    static LongSeries forLongs(long... longs) {
        return new LongArraySeries(longs);
    }

    @Override
    default Class<Long> getType() {
        return Long.TYPE;
    }

    long getLong(int index);

    void copyToLong(long[] to, int fromOffset, int toOffset, int len);

    LongSeries materializeLong();

    LongSeries concatLong(LongSeries... other);

    LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive);

    LongSeries headLong(int len);

    LongSeries tailLong(int len);

    LongSeries filterLong(LongPredicate p);

    LongSeries filterLong(BooleanSeries positions);

    LongSeries sortLong();

    // TODO: implement 'sortLong(LongComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexLong(LongPredicate predicate);

    BooleanSeries locateLong(LongPredicate predicate);

    /**
     * @return a LongSeries that contains non-repeating values from this Series.
     */
    LongSeries uniqueLong();
}
