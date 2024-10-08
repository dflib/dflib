package org.dflib;

import org.dflib.series.BooleanArraySeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.LongArraySeries;
import org.dflib.series.LongIndexedSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * A Series optimized to store and access primitive long values without <code>java.lang.Long</code> wrapper. Can also
 * pose as "Series&lt;Long>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface LongSeries extends Series<Long> {

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

    @Override
    LongSeries materialize();

    @Override
    default int position(Long value) {
        if (value == null) {
            return -1;
        }

        long pval = value;
        int len = size();
        for (int i = 0; i < len; i++) {
            if (pval == getLong(i)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    default Series<?> expand(Object... values) {
        int len = values.length;
        if (len == 0) {
            return this;
        }

        long[] primitives = new long[len];
        for (int i = 0; i < len; i++) {
            if (values[i] instanceof Long) {
                primitives[i] = (Long) values[i];
            } else {
                return Series.super.expand(values);
            }
        }

        return expandLong(primitives);
    }

    /**
     * @since 1.0.0-M21
     */
    default LongSeries expandLong(long... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        long[] expanded = new long[llen + rlen];
        this.copyToLong(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.ofLong(expanded);
    }

    LongSeries concatLong(LongSeries... other);

    @Override
    default LongSeries diff(Series<? extends Long> other) {
        return Diff.diffLong(this, other);
    }

    @Override
    default LongSeries intersect(Series<? extends Long> other) {
        return Intersect.intersectLong(this, other);
    }

    /**
     * @since 1.0.0-M19
     */
    LongSeries rangeLong(int fromInclusive, int toExclusive);

    @Override
    default LongSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeLong(0, len);
    }

    @Override
    default LongSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeLong(size - len, size);
    }

    @Override
    LongSeries select(Predicate<Long> p);

    @Override
    LongSeries select(Condition condition);

    @Override
    LongSeries select(BooleanSeries positions);

    @Override
    default Series<Long> select(IntSeries positions) {
        return LongIndexedSeries.of(this, positions);
    }

    /**
     * @since 0.11
     */
    LongSeries selectLong(LongPredicate p);

    @Override
    LongSeries sort(Comparator<? super Long> comparator);

    @Override
    LongSeries sort(Sorter... sorters);

    LongSeries sortLong();

    // TODO: implement 'sortLong(LongComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)},
     * only much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexLong(LongPredicate predicate);

    BooleanSeries locateLong(LongPredicate predicate);

    @Override
    default BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    default BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    LongSeries unique();

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

    @Override
    default BooleanSeries eq(Series<?> s) {
        if (!(s instanceof LongSeries)) {
            return Series.super.eq(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        LongSeries anotherInt = (LongSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getLong(i) == anotherInt.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    @Override
    default BooleanSeries ne(Series<?> s) {
        if (!(s instanceof LongSeries)) {
            return Series.super.ne(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        LongSeries anotherInt = (LongSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getLong(i) != anotherInt.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * Performs per-element addition between this and another LongSeries, returning the Series of the same
     * length.
     *
     * @since 0.11
     */
    default LongSeries add(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) + s.getLong(i);
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs per-element addition between this and a constant value, returning the Series of the same
     * length.
     *
     * @since 1.0.0-M23
     */
    default LongSeries add(final long v) {
        final int len = size();

        final long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) + v;
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries sub(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) - s.getLong(i);
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries mul(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) * s.getLong(i);
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and a constant value.
     *
     * @since 1.0.0-M23
     */
    default LongSeries mul(final long v) {
        final int len = size();

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) * v;
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs division operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries div(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) / s.getLong(i);
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs modulo operation between this and another LongSeries.
     *
     * @since 0.11
     */
    default LongSeries mod(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) % s.getLong(i);
        }

        return new LongArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries lt(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) < s.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) <= s.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) > s.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) >= s.getLong(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 1.0.0-M19
     */
    default BooleanSeries between(LongSeries from, LongSeries to) {
        int len = size();
        if (len != from.size()) {
            throw new IllegalArgumentException("'from' Series size " + from.size() + " is not the same as this size " + len);
        } else if (len != to.size()) {
            throw new IllegalArgumentException("'to' Series size " + to.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            long v = this.getLong(i);
            data[i] = v >= from.getLong(i) && v <= to.getLong(i);
        }

        return new BooleanArraySeries(data);
    }
}
