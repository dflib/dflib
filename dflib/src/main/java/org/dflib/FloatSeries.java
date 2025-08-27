package org.dflib;

import org.dflib.builder.BoolBuilder;
import org.dflib.f.FloatPredicate;
import org.dflib.op.ReplaceOp;
import org.dflib.series.FalseSeries;
import org.dflib.series.FloatArraySeries;
import org.dflib.series.FloatIndexedSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;

/**
 * A Series optimized to store and access primitive float values without <code>java.lang.Float</code> wrapper. Can also
 * pose as "Series&lt;Float>", although this is not the most efficient way of using it.
 *
 * @since 1.1.0
 */
public interface FloatSeries extends Series<Float> {

    @Override
    default Class<Float> getNominalType() {
        return Float.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Float.TYPE;
    }

    @Override
    default FloatSeries castAsFloat() {
        return this;
    }

    @Override
    default FloatSeries compact() {
        return this;
    }

    @Override
    default FloatSeries compactFloat(float forNull) {
        return this;
    }

    @Override
    default Float get(int index) {
        return getFloat(index);
    }

    float getFloat(int index);

    @Override
    default Series<Float> replace(int index, Float with) {
        return with != null ? replaceFloat(index, with) : ReplaceOp.replace(this, index, with);
    }

    /**
     * Returns a new Series with the value in the original Series at a given index replaced with the provided value.
     *
     * @since 2.0.0
     */
    default FloatSeries replaceFloat(int index, float with) {
        if (getFloat(index) == with) {
            return this;
        }

        int len = size();

        float[] floats = new float[len];
        copyToFloat(floats, 0, 0, len);
        floats[index] = with;

        return Series.ofFloat(floats);
    }

    void copyToFloat(float[] to, int fromOffset, int toOffset, int len);

    @Override
    FloatSeries materialize();

    @Override
    default int position(Float value) {
        if (value == null) {
            return -1;
        }

        float pval = value;
        int len = size();
        for (int i = 0; i < len; i++) {
            if (pval == getFloat(i)) {
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

        float[] primitives = new float[len];
        for (int i = 0; i < len; i++) {
            if (values[i] instanceof Float) {
                primitives[i] = (Float) values[i];
            } else {
                return Series.super.expand(values);
            }
        }

        return expandFloat(primitives);
    }

    /**
     * Creates a new Series with a provided values appended to the end of this Series.
     */
    default FloatSeries expandFloat(float... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        float[] expanded = new float[llen + rlen];
        this.copyToFloat(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.ofFloat(expanded);
    }

    @Override
    default Series<?> insert(int pos, Object... values) {

        int ilen = values.length;
        float[] floats = new float[ilen];
        for (int i = 0; i < ilen; i++) {
            if (values[i] instanceof Float) {
                floats[i] = (Float) values[i];
            } else {
                return Series.super.insert(pos, values);
            }
        }

        return insertFloat(pos, floats);
    }

    /**
     * @since 1.2.0
     */
    default FloatSeries insertFloat(int pos, float... values) {

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

        float[] expanded = new float[slen + ilen];
        if (pos > 0) {
            this.copyToFloat(expanded, 0, 0, pos);
        }

        System.arraycopy(values, 0, expanded, pos, ilen);

        if (pos < slen) {
            this.copyToFloat(expanded, pos, pos + ilen, slen - pos);
        }

        return Series.ofFloat(expanded);
    }

    @Override
    default Series<Float> concat(Series<? extends Float>... other) {

        int olen = other.length;
        if (olen == 0) {
            return this;
        }

        for (int i = 0; i < olen; i++) {
            if (!(other[i] instanceof FloatSeries)) {
                return Series.super.concat(other);
            }
        }

        int size = size();

        int h = size;
        for (Series<? extends Float> s : other) {
            h += s.size();
        }

        float[] data = new float[h];
        copyToFloat(data, 0, 0, size);

        int offset = size;
        for (Series<? extends Float> s : other) {
            int len = s.size();
            ((FloatSeries) s).copyToFloat(data, 0, offset, len);
            offset += len;
        }

        return Series.ofFloat(data);
    }

    default FloatSeries concatFloat(FloatSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int size = size();

        int h = size;
        for (FloatSeries s : other) {
            h += s.size();
        }

        float[] data = new float[h];
        copyToFloat(data, 0, 0, size);

        int offset = size;
        for (FloatSeries s : other) {
            int len = s.size();
            s.copyToFloat(data, 0, offset, len);
            offset += len;
        }

        return Series.ofFloat(data);
    }

    @Override
    default FloatSeries diff(Series<? extends Float> other) {
        return Diff.diffFloat(this, other);
    }

    @Override
    default FloatSeries intersect(Series<? extends Float> other) {
        return Intersect.intersectFloat(this, other);
    }

    FloatSeries rangeFloat(int fromInclusive, int toExclusive);

    @Override
    default FloatSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeFloat(0, len);
    }

    @Override
    default FloatSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeFloat(size - len, size);
    }

    @Override
    FloatSeries select(Condition condition);

    @Override
    FloatSeries select(Predicate<Float> p);

    @Override
    FloatSeries select(BooleanSeries positions);

    @Override
    default Series<Float> select(IntSeries positions) {
        return FloatIndexedSeries.of(this, positions);
    }

    FloatSeries selectFloat(FloatPredicate p);

    @Override
    default FloatSeries sort(String sorters, Object... params) {
        return sort(Sorter.parseSorterArray(sorters, params));
    }

    @Override
    FloatSeries sort(Sorter... sorters);

    @Override
    FloatSeries sort(Comparator<? super Float> comparator);

    FloatSeries sortFloat();

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)},
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexFloat(FloatPredicate predicate);

    BooleanSeries locateFloat(FloatPredicate predicate);

    @Override
    default BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    default BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    FloatSeries unique();

    @Override
    FloatSeries sample(int size);

    @Override
    FloatSeries sample(int size, Random random);

    default float[] toFloatArray() {
        int len = size();
        float[] copy = new float[len];
        copyToFloat(copy, 0, 0, len);
        return copy;
    }

    DoubleSeries cumSum();

    float max();

    float min();

    double sum();

    float avg();

    default float median() {
        return quantile(0.5);
    }

    /**
     * Returns a value of the specified quantile. If the argument is "0.5", the result is the same as calling
     * {@link #median()}
     *
     * @since 2.0.0
     */
    float quantile(double q);

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
        float avg = avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            float x = this.getFloat(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    /**
     * Compute the variance, using the population variant.
     */
    default double variance() {
        return variance(true);
    }

    @Override
    default BooleanSeries eq(Series<?> s) {
        if (!(s instanceof FloatSeries)) {
            return Series.super.eq(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatSeries as = (FloatSeries) s;
        return BoolBuilder.buildSeries(i -> getFloat(i) == as.getFloat(i), len);
    }

    @Override
    default BooleanSeries ne(Series<?> s) {
        if (!(s instanceof FloatSeries)) {
            return Series.super.ne(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatSeries as = (FloatSeries) s;
        return BoolBuilder.buildSeries(i -> getFloat(i) != as.getFloat(i), len);
    }

    /**
     * Performs per-element addition between this and another FloatSeries, returning the Series of the same length.
     */
    default FloatSeries add(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        float[] data = new float[len];

        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) + s.getFloat(i);
        }

        return new FloatArraySeries(data);
    }

    /**
     * Performs per-element addition between this and a constant value returning the Series of the same length.
     */
    default FloatSeries add(float v) {
        int len = size();

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) + v;
        }

        return new FloatArraySeries(data);
    }

    /**
     * Performs subtraction operation between this and another FloatSeries.
     */
    default FloatSeries sub(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) - s.getFloat(i);
        }

        return new FloatArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and another FloatSeries
     */
    default FloatSeries mul(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) * s.getFloat(i);
        }

        return new FloatArraySeries(data);
    }

    /**
     * Performs multiplication operation between this and a constant value.
     */
    default FloatSeries mul(float v) {
        int len = size();

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) * v;
        }

        return new FloatArraySeries(data);
    }

    /**
     * Performs division operation between this and another FloatSeries.
     */
    default FloatSeries div(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) / s.getFloat(i);
        }

        return new FloatArraySeries(data);
    }


    default FloatSeries mod(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = this.getFloat(i) % s.getFloat(i);
        }

        return new FloatArraySeries(data);
    }

    default BooleanSeries lt(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getFloat(i) < s.getFloat(i), len);
    }

    default BooleanSeries le(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getFloat(i) <= s.getFloat(i), len);
    }

    default BooleanSeries gt(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getFloat(i) > s.getFloat(i), len);
    }


    default BooleanSeries ge(FloatSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> getFloat(i) >= s.getFloat(i), len);
    }

    default BooleanSeries between(FloatSeries from, FloatSeries to) {
        int len = size();
        if (len != from.size()) {
            throw new IllegalArgumentException("'from' Series size " + from.size() + " is not the same as this size " + len);
        } else if (len != to.size()) {
            throw new IllegalArgumentException("'to' Series size " + to.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> {
            float v = this.getFloat(i);
            return v >= from.getFloat(i) && v <= to.getFloat(i);
        }, len);
    }
}
