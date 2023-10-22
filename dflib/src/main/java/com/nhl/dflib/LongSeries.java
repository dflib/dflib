package com.nhl.dflib;

import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.builder.LongAccum;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Comparator;
import java.util.Random;

/**
 * A Series optimized to store and access primitive long values without <code>java.lang.Long</code> wrapper. Can also
 * pose as "Series&lt;Long>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface LongSeries extends Series<Long> {

    /**
     * @deprecated in favor of {@link Series#ofLong(long...)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static LongSeries forLongs(long... longs) {
        return Series.ofLong(longs);
    }

    /**
     * @since 0.7
     * @deprecated since 0.16 in favor of {@link #mapAsLong(LongValueMapper)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static <V> LongSeries forSeries(Series<V> series, LongValueMapper<? super V> converter) {
        return series.mapAsLong(converter);
    }

    @Override
    default Class<Long> getNominalType() {
        return Long.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Long.TYPE;
    }

    @Override
    default LongSeries castAsLong() {
        return this;
    }

    long getLong(int index);

    void copyToLong(long[] to, int fromOffset, int toOffset, int len);

    LongSeries materializeLong();

    /**
     * @since 0.18
     */
    @Override
    default Series<?> add(Object value) {
        return value instanceof Long
                ? addLong((Long) value)
                : Series.super.add(value);
    }

    /**
     * Creates a new Series with a provided int appended to the end of this Series.
     *
     * @since 0.18
     */
    default LongSeries addLong(long val) {
        int s = size();

        long[] data = new long[s + 1];
        this.copyToLong(data, 0, 0, s);
        data[s] = val;
        return new LongArraySeries(data);
    }

    LongSeries concatLong(LongSeries... other);

    LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive);

    @Override
    LongSeries head(int len);

    /**
     * @deprecated since 0.18 in favor of {@link #head(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default LongSeries headLong(int len) {
        return head(len);
    }

    @Override
    LongSeries tail(int len);

    /**
     * @deprecated since 0.18 in favor of {@link #tail(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default LongSeries tailLong(int len) {
        return tail(len);
    }

    /**
     * @since 0.11
     */
    LongSeries selectLong(Condition condition);

    /**
     * @since 0.11
     */
    LongSeries selectLong(LongPredicate p);

    /**
     * @since 0.11
     */
    LongSeries selectLong(BooleanSeries positions);

    @Override
    LongSeries sort(Comparator<? super Long> comparator);

    @Override
    LongSeries sort(Sorter... sorters);

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
     * @since 0.14
     */
    LongSeries cumSum();

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
     * @since 0.11
     */
    double avg();

    /**
     * @since 0.7
     */
    double median();

    /**
     * @since 0.11
     */
    default LongSeries add(LongSeries s) {
        int len = size();
        LongAccum accumulator = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushLong(this.getLong(i) + s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs subtraction operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries sub(LongSeries s) {
        int len = size();
        LongAccum accumulator = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushLong(this.getLong(i) - s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs multiplication operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries mul(LongSeries s) {
        int len = size();
        LongAccum accumulator = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushLong(this.getLong(i) * s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs division operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries div(LongSeries s) {
        int len = size();
        LongAccum accumulator = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushLong(this.getLong(i) / s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs modulo operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries mod(LongSeries s) {
        int len = size();
        LongAccum accumulator = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushLong(this.getLong(i) % s.getLong(i));
        }

        return accumulator.toSeries();
    }


    /**
     * @since 0.11
     */
    default BooleanSeries lt(LongSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getLong(i) < s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(LongSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getLong(i) <= s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(LongSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getLong(i) > s.getLong(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(LongSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getLong(i) >= s.getLong(i));
        }

        return accumulator.toSeries();
    }
}
