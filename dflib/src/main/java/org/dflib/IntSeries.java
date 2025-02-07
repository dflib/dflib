package org.dflib;

import org.dflib.builder.BoolBuilder;
import org.dflib.op.ReplaceOp;
import org.dflib.series.FalseSeries;
import org.dflib.series.IntArraySeries;
import org.dflib.series.IntIndexedSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;
import org.dflib.sort.IntComparator;

import java.util.Comparator;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * A Series optimized to store and access primitive int values without <code>java.lang.Integer</code> wrapper. Can also
 * pose as "Series&lt;Integer>", although this is not the most efficient way of using it.
 */
public interface IntSeries extends Series<Integer> {

    @Override
    default Class<Integer> getNominalType() {
        return Integer.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Integer.TYPE;
    }

    @Override
    default IntSeries castAsInt() {
        return this;
    }

    @Override
    default IntSeries compactInt(int forNull) {
        return this;
    }

    @Override
    default Integer get(int index) {
        return getInt(index);
    }

    int getInt(int index);

    @Override
    default Series<Integer> replace(int index, Integer with) {
        return with != null ? replaceInt(index, with) : ReplaceOp.replace(this, index, with);
    }

    /**
     * Returns a new Series with the value in the original Series at a given index replaced with the provided value.
     *
     * @since 2.0.0
     */
    default IntSeries replaceInt(int index, int with) {
        if (getInt(index) == with) {
            return this;
        }

        int len = size();

        int[] ints = new int[len];
        copyToInt(ints, 0, 0, len);
        ints[index] = with;

        return Series.ofInt(ints);
    }

    void copyToInt(int[] to, int fromOffset, int toOffset, int len);

    @Override
    IntSeries materialize();

    @Override
    default int position(Integer value) {
        if (value == null) {
            return -1;
        }

        int ival = value;
        int len = size();
        for (int i = 0; i < len; i++) {
            if (ival == getInt(i)) {
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

        int[] primitives = new int[len];
        for (int i = 0; i < len; i++) {
            if (values[i] instanceof Integer) {
                primitives[i] = (Integer) values[i];
            } else {
                return Series.super.expand(values);
            }
        }

        return expandInt(primitives);
    }

    /**
     * Creates a new Series with a provided values appended to the end of this Series.
     */
    default IntSeries expandInt(int... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        int[] expanded = new int[llen + rlen];
        this.copyToInt(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.ofInt(expanded);
    }

    @Override
    default Series<?> insert(int pos, Object... values) {

        int ilen = values.length;
        int[] ints = new int[ilen];
        for (int i = 0; i < ilen; i++) {
            if (values[i] instanceof Integer) {
                ints[i] = (Integer) values[i];
            } else {
                return Series.super.insert(pos, values);
            }
        }

        return insertInt(pos, ints);
    }

    /**
     * @since 1.2.0
     */
    default IntSeries insertInt(int pos, int... values) {

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

        int[] expanded = new int[slen + ilen];
        if (pos > 0) {
            this.copyToInt(expanded, 0, 0, pos);
        }

        System.arraycopy(values, 0, expanded, pos, ilen);

        if (pos < slen) {
            this.copyToInt(expanded, pos, pos + ilen, slen - pos);
        }

        return Series.ofInt(expanded);
    }

    IntSeries concatInt(IntSeries... other);

    @Override
    default IntSeries diff(Series<? extends Integer> other) {
        return Diff.diffInt(this, other);
    }

    @Override
    default IntSeries intersect(Series<? extends Integer> other) {
        return Intersect.intersectInt(this, other);
    }


    IntSeries rangeInt(int fromInclusive, int toExclusive);

    @Override
    default IntSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeInt(0, len);
    }

    @Override
    default IntSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeInt(size - len, size);
    }

    @Override
    IntSeries select(Predicate<Integer> p);

    @Override
    IntSeries select(Condition condition);

    @Override
    IntSeries select(BooleanSeries positions);

    @Override
    default Series<Integer> select(IntSeries positions) {
        return IntIndexedSeries.of(this, positions);
    }


    IntSeries selectInt(IntPredicate p);

    @Override
    IntSeries sort(Sorter... sorters);

    @Override
    IntSeries sort(Comparator<? super Integer> comparator);

    IntSeries sortInt();

    IntSeries sortInt(IntComparator comparator);

    IntSeries sortIndexInt();

    IntSeries sortIndexInt(IntComparator comparator);

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)}
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate. Negative values denote
     * null values.
     */
    IntSeries indexInt(IntPredicate predicate);

    BooleanSeries locateInt(IntPredicate predicate);

    @Override
    default BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    default BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    IntSeries unique();


    @Override
    IntSeries sample(int size);


    @Override
    IntSeries sample(int size, Random random);


    default int[] toIntArray() {
        int len = size();
        int[] copy = new int[len];
        copyToInt(copy, 0, 0, len);
        return copy;
    }

    LongSeries cumSum();

    int max();

    int min();

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
    default double stdDev(boolean usePopulationStdDev) {
        double variance = variance(usePopulationStdDev);
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
            final double x = this.getInt(i);
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
        if (!(s instanceof IntSeries)) {
            return Series.super.eq(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntSeries anotherInt = (IntSeries) s;
        return BoolBuilder.buildSeries(i -> getInt(i) == anotherInt.getInt(i), len);
    }

    @Override
    default BooleanSeries ne(Series<?> s) {
        if (!(s instanceof IntSeries)) {
            return Series.super.ne(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntSeries anotherInt = (IntSeries) s;
        return BoolBuilder.buildSeries(i -> getInt(i) != anotherInt.getInt(i), len);
    }

    /**
     * Performs per-element addition between this and another IntSeries, returning the Series of the same
     * length.
     */
    default IntSeries add(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) + s.getInt(i);
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs per-element addition between this and a constant value, returning the Series of the same
     * length.
     */
    default IntSeries add(int v) {
        int len = size();

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) + v;
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another IntSeries.
     */
    default IntSeries sub(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) - s.getInt(i);
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and another IntSeries.
     */
    default IntSeries mul(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) * s.getInt(i);
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and another IntSeries.
     */
    default IntSeries mul(int v) {
        int len = size();

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) * v;
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs division operation between this and another IntSeries.
     */
    default IntSeries div(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) / s.getInt(i);
        }

        return new IntArraySeries(data);
    }

    /**
     * Performs modulo operation between this and another IntSeries.
     */
    default IntSeries mod(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getInt(i) % s.getInt(i);
        }

        return new IntArraySeries(data);
    }


    default BooleanSeries lt(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getInt(i) < s.getInt(i), len);
    }


    default BooleanSeries le(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getInt(i) <= s.getInt(i), len);
    }


    default BooleanSeries gt(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getInt(i) > s.getInt(i), len);
    }


    default BooleanSeries ge(IntSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getInt(i) >= s.getInt(i), len);
    }


    default BooleanSeries between(IntSeries from, IntSeries to) {
        int len = size();
        if (len != from.size()) {
            throw new IllegalArgumentException("'from' Series size " + from.size() + " is not the same as this size " + len);
        } else if (len != to.size()) {
            throw new IllegalArgumentException("'to' Series size " + to.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> {
            int v = this.getInt(i);
            return v >= from.getInt(i) && v <= to.getInt(i);
        }, len);
    }
}
