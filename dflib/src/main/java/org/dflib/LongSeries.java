package org.dflib;

import org.dflib.builder.BoolBuilder;
import org.dflib.op.ReplaceOp;
import org.dflib.series.FalseSeries;
import org.dflib.series.LongArraySeries;
import org.dflib.series.LongIndexedSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;
import org.dflib.sort.Sorters;

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

    @Override
    default LongSeries compact() {
        return this;
    }

    @Override
    default LongSeries compactLong(long forNull) {
        return this;
    }

    @Override
    default Long get(int index) {
        return getLong(index);
    }

    long getLong(int index);

    @Override
    default Series<Long> replace(int index, Long with) {
        return with != null ? replaceLong(index, with) : ReplaceOp.replace(this, index, with);
    }

    /**
     * Returns a new Series with the value in the original Series at a given index replaced with the provided value.
     *
     * @since 2.0.0
     */
    default LongSeries replaceLong(int index, long with) {
        if (getLong(index) == with) {
            return this;
        }

        int len = size();

        long[] longs = new long[len];
        copyToLong(longs, 0, 0, len);
        longs[index] = with;

        return Series.ofLong(longs);
    }

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

        int olen = other.length;
        if (olen == 0) {
            return this;
        }

        for (int i = 0; i < olen; i++) {
            if (!(other[i] instanceof LongSeries)) {
                return Series.super.concat(other);
            }
        }

        int size = size();

        int h = size;
        for (Series<? extends Long> s : other) {
            h += s.size();
        }

        long[] data = new long[h];
        copyToLong(data, 0, 0, size);

        int offset = size;
        for (Series<? extends Long> s : other) {
            int len = s.size();
            ((LongSeries) s).copyToLong(data, 0, offset, len);
            offset += len;
        }

        return Series.ofLong(data);
    }

    default LongSeries concatLong(LongSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int size = size();

        int h = size;
        for (LongSeries s : other) {
            h += s.size();
        }

        long[] data = new long[h];
        copyToLong(data, 0, 0, size);

        int offset = size;
        for (LongSeries s : other) {
            int len = s.size();
            s.copyToLong(data, 0, offset, len);
            offset += len;
        }

        return Series.ofLong(data);
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
    default LongSeries sort(String... sorters) {
        return sort(Sorters.asSorters(sorters));
    }

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


    default double median() {
        return quantile(0.5);
    }

    /**
     * Returns a value of the specified quantile. If the argument is "0.5", the result is the same as calling
     * {@link #median()}
     *
     * @since 2.0.0
     */
    double quantile(double q);

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

        LongSeries anotherLong = (LongSeries) s;
        return BoolBuilder.buildSeries(i -> getLong(i) == anotherLong.getLong(i), len);
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

        LongSeries anotherLong = (LongSeries) s;
        return BoolBuilder.buildSeries(i -> getLong(i) != anotherLong.getLong(i), len);
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

        return BoolBuilder.buildSeries(i -> getLong(i) < s.getLong(i), len);
    }


    default BooleanSeries le(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getLong(i) <= s.getLong(i), len);
    }


    default BooleanSeries gt(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getLong(i) > s.getLong(i), len);
    }


    default BooleanSeries ge(LongSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getLong(i) >= s.getLong(i), len);
    }


    default BooleanSeries between(LongSeries from, LongSeries to) {
        int len = size();
        if (len != from.size()) {
            throw new IllegalArgumentException("'from' Series size " + from.size() + " is not the same as this size " + len);
        } else if (len != to.size()) {
            throw new IllegalArgumentException("'to' Series size " + to.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> {
           long v = getLong(i);
           return v >= from.getLong(i) && v <= to.getLong(i);
        }, len);
    }
}
