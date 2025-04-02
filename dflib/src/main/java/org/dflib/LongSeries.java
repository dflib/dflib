package org.dflib;

import org.dflib.concat.SeriesConcat;
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

    @Override
    default Series<?> insert(int pos, Object... values) {

        int ilen = values.length;
        long[] longs = new long[ilen];
        for (int i = 0; i < ilen; i++) {
            if (values[i] instanceof Long) {
                longs[i] = (Long) values[i];
            } else {
                return Series.super.insert(pos, values);
            }
        }

        return insertLong(pos, longs);
    }

    /**
     * @since 1.2.0
     */
    default LongSeries insertLong(int pos, long... values) {

        if (pos < 0) {
            // TODO: treat it as offset from the end?
            throw new IllegalArgumentException("Negative insert position: " + pos);
        }

        int slen = size();
        if (pos > slen) {
            throw new IllegalArgumentException("Insert position past the end of the Series: " + pos + ", len: " + slen);
        }

        int ilen = values.length;
        if (ilen == 0) {
            return this;
        }

        long[] expanded = new long[slen + ilen];
        if (pos > 0) {
            this.copyToLong(expanded, 0, 0, pos);
        }

        System.arraycopy(values, 0, expanded, pos, ilen);

        if (pos < slen) {
            this.copyToLong(expanded, pos, pos + ilen, slen - pos);
        }

        return Series.ofLong(expanded);
    }

    @Override
    default Series<Long> concat(Series<? extends Long>... other) {

        int len = other.length;
        if (len == 0) {
            return this;
        }

        for (int i = 0; i < len; i++) {
            if (!(other[i] instanceof LongSeries)) {

                Series[] combined = new Series[len + 1];
                combined[0] = this;
                System.arraycopy(other, 0, combined, 1, len);

                return SeriesConcat.concatAsObjects(combined);
            }
        }

        LongSeries[] combined = new LongSeries[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.longConcat(combined);
    }

    default LongSeries concatLong(LongSeries... other) {
        if (other.length == 0) {
            return this;
        }

        LongSeries[] combined = new LongSeries[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.longConcat(combined);
    }

    @Override
    default LongSeries diff(Series<? extends Long> other) {
        return Diff.diffLong(this, other);
    }

    @Override
    default LongSeries intersect(Series<? extends Long> other) {
        return Intersect.intersectLong(this, other);
    }


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


    @Override
    LongSeries sample(int size);


    @Override
    LongSeries sample(int size, Random random);


    default long[] toLongArray() {
        int len = size();
        long[] copy = new long[len];
        copyToLong(copy, 0, 0, len);
        return copy;
    }


    LongSeries cumSum();


    long max();


    long min();


    // TODO: deal with overflow?
    long sum();


    double avg();


    double median();

    /**
     * Compute the standard deviation
     *
     * @param usePopulationStdDev Use the Population variant if true, Sample variant if false
     */
    default double stdDev(final boolean usePopulationStdDev) {
        final double variance = variance(usePopulationStdDev);
        return Math.sqrt(variance);
    }

    /**
     * Compute the standard deviation, using the population variant
     */
    default double stdDev() {
        return stdDev(true);
    }

    /**
     * Compute the variance
     *
     * @param usePopulationVariance Use the Population variant if true, Sample variant if false
     */
    default double variance(boolean usePopulationVariance) {
        int len = size();
        double avg = avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            final double x = this.getLong(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    /**
     * Compute the variance, using the population variant
     */
    default double variance() {
        return variance(true);
    }

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
     * Performs per-element addition between this and a constant value, returning the Series of the same length.
     */
    default LongSeries add(long v) {
        int len = size();

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) + v;
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another LongSeries.
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
     */
    default LongSeries mul(long v) {
        int len = size();

        long[] data = new long[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getLong(i) * v;
        }

        return new LongArraySeries(data);
    }

    /**
     * Performs division operation between this and another LongSeries.
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
