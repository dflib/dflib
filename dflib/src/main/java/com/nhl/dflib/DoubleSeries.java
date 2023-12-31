package com.nhl.dflib;

import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.series.FalseSeries;
import com.nhl.dflib.series.TrueSeries;
import com.nhl.dflib.set.Diff;
import com.nhl.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;

/**
 * A Series optimized to store and access primitive double values without <code>java.lang.Double</code> wrapper. Can also
 * pose as "Series&lt;Double>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface DoubleSeries extends Series<Double> {

    /**
     * @deprecated in favor of {@link Series#ofDouble(double...)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static DoubleSeries forDoubles(double... doubles) {
        return Series.ofDouble(doubles);
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #mapAsDouble(DoubleValueMapper)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static <V> DoubleSeries forSeries(Series<V> series, DoubleValueMapper<? super V> converter) {
        return series.mapAsDouble(converter);
    }

    @Override
    default Class<Double> getNominalType() {
        return Double.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Double.TYPE;
    }

    @Override
    default DoubleSeries castAsDouble() {
        return this;
    }

    double getDouble(int index);

    void copyToDouble(double[] to, int fromOffset, int toOffset, int len);

    @Override
    DoubleSeries materialize();

    /**
     * @deprecated in favor of {@link #materialize()}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries materializeDouble() {
        return materialize();
    }

    /**
     * @since 0.18
     */
    @Override
    default Series<?> add(Object value) {
        return value instanceof Double
                ? addDouble((Double) value)
                : Series.super.add(value);
    }

    /**
     * Creates a new Series with a provided int appended to the end of this Series.
     *
     * @since 0.18
     */
    default DoubleSeries addDouble(double val) {
        int s = size();

        double[] data = new double[s + 1];
        this.copyToDouble(data, 0, 0, s);
        data[s] = val;
        return new DoubleArraySeries(data);
    }

    DoubleSeries concatDouble(DoubleSeries... other);

    @Override
    default DoubleSeries diff(Series<? extends Double> other) {
        return Diff.diffDouble(this, other);
    }

    @Override
    default DoubleSeries intersect(Series<? extends Double> other) {
        return Intersect.intersectDouble(this, other);
    }

    DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive);

    @Override
    default DoubleSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeOpenClosedDouble(0, len);
    }

    /**
     * @deprecated in favor of {@link #head(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries headDouble(int len) {
        return head(len);
    }

    @Override
    default DoubleSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeOpenClosedDouble(size - len, size);
    }

    /**
     * @deprecated in favor of {@link #tail(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries tailDouble(int len) {
        return tail(len);
    }

    @Override
    DoubleSeries select(Condition condition);

    @Override
    DoubleSeries select(ValuePredicate<Double> p);

    @Override
    DoubleSeries select(BooleanSeries positions);

    /**
     * @since 0.11
     * @deprecated in favor of {@link #select(Condition)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries selectDouble(Condition condition) {
        return select(condition);
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #select(ValuePredicate)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries selectDouble(BooleanSeries positions) {
        return select(positions);
    }

    /**
     * @since 0.11
     */
    DoubleSeries selectDouble(DoublePredicate p);

    @Override
    DoubleSeries sort(Sorter... sorters);

    @Override
    DoubleSeries sort(Comparator<? super Double> comparator);

    DoubleSeries sortDouble();

    // TODO: implement 'sortDouble(DoubleComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexDouble(DoublePredicate predicate);

    BooleanSeries locateDouble(DoublePredicate predicate);

    @Override
    default BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    default BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    DoubleSeries unique();

    /**
     * @deprecated in favor of {@link #unique()}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default DoubleSeries uniqueDouble() {
        return unique();
    }

    /**
     * @since 0.7
     */
    @Override
    DoubleSeries sample(int size);

    /**
     * @since 0.7
     */
    @Override
    DoubleSeries sample(int size, Random random);

    /**
     * @since 0.7
     */
    default double[] toDoubleArray() {
        int len = size();
        double[] copy = new double[len];
        copyToDouble(copy, 0, 0, len);
        return copy;
    }

    /**
     * @since 0.14
     */
    DoubleSeries cumSum();

    /**
     * @since 0.7
     */
    double max();

    /**
     * @since 0.7
     */
    double min();

    /**
     * @since 0.7
     */
    // TODO: deal with overflow?
    double sum();

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
        if (!(s instanceof DoubleSeries)) {
            return Series.super.eq(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        DoubleSeries as = (DoubleSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getDouble(i) == as.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    @Override
    default BooleanSeries ne(Series<?> s) {
        if (!(s instanceof DoubleSeries)) {
            return Series.super.ne(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        DoubleSeries as = (DoubleSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getDouble(i) != as.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default DoubleSeries add(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) + s.getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries sub(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) - s.getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries mul(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) * s.getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Performs division operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries div(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) / s.getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default DoubleSeries mod(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) % s.getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries lt(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) < s.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) <= s.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) > s.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(DoubleSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) >= s.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }

    /**
     * @since 1.0.0-M19
     */
    default BooleanSeries between(DoubleSeries from, DoubleSeries to) {
        int len = size();
        if (len != from.size()) {
            throw new IllegalArgumentException("'from' Series size " + from.size() + " is not the same as this size " + len);
        } else if (len != to.size()) {
            throw new IllegalArgumentException("'to' Series size " + to.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            double d = this.getDouble(i);
            data[i] = d >= from.getDouble(i) && d <= to.getDouble(i);
        }

        return new BooleanArraySeries(data);
    }
}
