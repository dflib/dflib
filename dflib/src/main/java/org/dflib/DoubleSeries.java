package org.dflib;

import org.dflib.concat.SeriesConcat;
import org.dflib.series.BooleanArraySeries;
import org.dflib.series.DoubleArraySeries;
import org.dflib.series.DoubleIndexedSeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * A Series optimized to store and access primitive double values without <code>java.lang.Double</code> wrapper. Can also
 * pose as "Series&lt;Double>", although this is not the most efficient way of using it.
 */
public interface DoubleSeries extends Series<Double> {

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

    @Override
    default int position(Double value) {
        if (value == null) {
            return -1;
        }

        double pval = value;
        int len = size();
        for (int i = 0; i < len; i++) {
            if (pval == getDouble(i)) {
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

        double[] primitives = new double[len];
        for (int i = 0; i < len; i++) {
            if (values[i] instanceof Double) {
                primitives[i] = (Double) values[i];
            } else {
                return Series.super.expand(values);
            }
        }

        return expandDouble(primitives);
    }

    /**
     * Creates a new Series with a provided values appended to the end of this Series.
     */
    default DoubleSeries expandDouble(double... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        double[] expanded = new double[llen + rlen];
        this.copyToDouble(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.ofDouble(expanded);
    }

    @Override
    default Series<?> insert(int pos, Object... values) {

        int ilen = values.length;
        double[] doubles = new double[ilen];
        for (int i = 0; i < ilen; i++) {
            if (values[i] instanceof Double) {
                doubles[i] = (Double) values[i];
            } else {
                return Series.super.insert(pos, values);
            }
        }

        return insertDouble(pos, doubles);
    }

    /**
     * @since 1.2.0
     */
    default DoubleSeries insertDouble(int pos, double... values) {

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

        double[] expanded = new double[slen + ilen];
        if (pos > 0) {
            this.copyToDouble(expanded, 0, 0, pos);
        }

        System.arraycopy(values, 0, expanded, pos, ilen);

        if (pos < slen) {
            this.copyToDouble(expanded, pos, pos + ilen, slen - pos);
        }

        return Series.ofDouble(expanded);
    }

    @Override
    default Series<Double> concat(Series<? extends Double>... other) {

        int len = other.length;
        if (len == 0) {
            return this;
        }

        for (int i = 0; i < len; i++) {
            if (!(other[i] instanceof DoubleSeries)) {

                Series[] combined = new Series[len + 1];
                combined[0] = this;
                System.arraycopy(other, 0, combined, 1, len);

                return SeriesConcat.concatAsObjects(combined);
            }
        }

        DoubleSeries[] combined = new DoubleSeries[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.doubleConcat(combined);
    }

    default DoubleSeries concatDouble(DoubleSeries... other) {
        if (other.length == 0) {
            return this;
        }

        DoubleSeries[] combined = new DoubleSeries[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.doubleConcat(combined);
    }

    @Override
    default DoubleSeries diff(Series<? extends Double> other) {
        return Diff.diffDouble(this, other);
    }

    @Override
    default DoubleSeries intersect(Series<? extends Double> other) {
        return Intersect.intersectDouble(this, other);
    }


    DoubleSeries rangeDouble(int fromInclusive, int toExclusive);

    @Override
    default DoubleSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeDouble(0, len);
    }

    @Override
    default DoubleSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeDouble(size - len, size);
    }

    @Override
    DoubleSeries select(Condition condition);

    @Override
    DoubleSeries select(Predicate<Double> p);

    @Override
    DoubleSeries select(BooleanSeries positions);

    @Override
    default Series<Double> select(IntSeries positions) {
        return DoubleIndexedSeries.of(this, positions);
    }

    DoubleSeries selectDouble(DoublePredicate p);

    @Override
    DoubleSeries sort(Sorter... sorters);

    @Override
    DoubleSeries sort(Comparator<? super Double> comparator);

    DoubleSeries sortDouble();

    // TODO: implement 'sortDouble(DoubleComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)},
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

    @Override
    DoubleSeries sample(int size);

    @Override
    DoubleSeries sample(int size, Random random);


    default double[] toDoubleArray() {
        int len = size();
        double[] copy = new double[len];
        copyToDouble(copy, 0, 0, len);
        return copy;
    }

    DoubleSeries cumSum();

    double max();

    double min();

    // TODO: deal with overflow?
    double sum();

    double avg();

    double median();

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
            final double x = this.getDouble(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    /**
     * Compute the variance, using the population variantC1
     */
    default double variance() {
        return variance(true);
    }

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
     * Performs per-element addition between this and another DoubleSeries, returning the Series of the same length.
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
     * Performs per-element addition between this and a constant value returning the Series of the same length.
     */
    default DoubleSeries add(double v) {
        int len = size();

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) + v;
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another DoubleSeries.
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
     * Performs multiplication operation between this and another DoubleSeries
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
     * Performs multiplication operation between this and a constant value.
     */
    default DoubleSeries mul(double v) {
        int len = size();

        double[] data = new double[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getDouble(i) * v;
        }

        return new DoubleArraySeries(data);
    }

    /**
     * Performs division operation between this and another DoubleSeries.
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
