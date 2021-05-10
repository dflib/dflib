package com.nhl.dflib;

import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.LongAccumulator;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Random;

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

    /**
     * @since 0.7
     */
    static <V> LongSeries forSeries(Series<V> series, LongValueMapper<? super V> converter) {
        int len = series.size();
        LongAccumulator a = new LongAccumulator(len);
        for (int i = 0; i < len; i++) {
            a.addLong(converter.map(series.get(i)));
        }

        return a.toSeries();
    }

    @Override
    default Class<Long> getNominalType() {
        return Long.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Long.TYPE;
    }

    long getLong(int index);

    void copyToLong(long[] to, int fromOffset, int toOffset, int len);

    LongSeries materializeLong();

    LongSeries concatLong(LongSeries... other);

    LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive);

    LongSeries headLong(int len);

    LongSeries tailLong(int len);

    /**
     * @since 0.11
     */
    LongSeries selectLong(LongPredicate p);

    /**
     * @since 0.11
     */
    LongSeries selectLong(BooleanSeries positions);

    /**
     * @deprecated since 0.11 in favor of {@link #selectLong(LongPredicate)}
     */
    @Deprecated
    default LongSeries filterLong(LongPredicate p) {
        return selectLong(p);
    }

    /**
     * @deprecated since 0.11 in favor of {@link #selectLong(BooleanSeries)}
     */
    @Deprecated
    default LongSeries filterLong(BooleanSeries positions) {
        return selectLong(positions);
    }

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

    /**
     * @since 0.7
     */
    @Override
    LongSeries sample(int size);

    /**
     * @since 0.7
     */
    @Override
    LongSeries sample(int size, Random random);

    /**
     * @since 0.7
     */
    default long[] toLongArray() {
        int len = size();
        long[] copy = new long[len];
        copyToLong(copy, 0, 0, len);
        return copy;
    }

    /**
     * @since 0.7
     */
    long max();

    /**
     * @since 0.7
     */
    long min();

    /**
     * @since 0.7
     */
    // TODO: deal with overflow?
    long sum();

    /**
     * @since 0.7
     */
    double average();

    /**
     * @since 0.7
     */
    double median();

    /**
     * @since 0.11
     */
    default LongSeries add(LongSeries s) {
        int len = size();
        LongAccumulator accumulator = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addLong(this.getLong(i) + s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default LongSeries subtract(LongSeries s) {
        int len = size();
        LongAccumulator accumulator = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addLong(this.getLong(i) - s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default LongSeries multiply(LongSeries s) {
        int len = size();
        LongAccumulator accumulator = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addLong(this.getLong(i) * s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default LongSeries divide(LongSeries s) {
        int len = size();
        LongAccumulator accumulator = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addLong(this.getLong(i) / s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default LongSeries mod(LongSeries s) {
        int len = size();
        LongAccumulator accumulator = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addLong(this.getLong(i) % s.getLong(i));
        }

        return accumulator.toSeries();
    }


    /**
     * @since 0.11
     */
    default BooleanSeries lt(LongSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getLong(i) < s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(LongSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getLong(i) <= s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(LongSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getLong(i) > s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(LongSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getLong(i) >= s.getLong(i));
        }

        return accumulator.toSeries();
    }
}
