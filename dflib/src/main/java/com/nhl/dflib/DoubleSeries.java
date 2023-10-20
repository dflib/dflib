package com.nhl.dflib;

import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.builder.DoubleAccum;

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

    double getDouble(int index);

    void copyToDouble(double[] to, int fromOffset, int toOffset, int len);

    DoubleSeries materializeDouble();

    DoubleSeries concatDouble(DoubleSeries... other);

    DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive);

    DoubleSeries headDouble(int len);

    DoubleSeries tailDouble(int len);

    /**
     * @since 0.11
     */
    DoubleSeries selectDouble(Condition condition);

    /**
     * @since 0.11
     */
    DoubleSeries selectDouble(DoublePredicate p);

    /**
     * @since 0.11
     */
    DoubleSeries selectDouble(BooleanSeries positions);

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

    /**
     * @return a DoubleSeries that contains non-repeating values from this Series.
     */
    DoubleSeries uniqueDouble();

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

    /**
     * @since 0.11
     */
    default DoubleSeries add(DoubleSeries s) {
        int len = size();
        DoubleAccum accumulator = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushDouble(this.getDouble(i) + s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs subtraction operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries sub(DoubleSeries s) {
        int len = size();
        DoubleAccum accumulator = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushDouble(this.getDouble(i) - s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs multiplication operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries mul(DoubleSeries s) {
        int len = size();
        DoubleAccum accumulator = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushDouble(this.getDouble(i) * s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs division operation between this and another DoubleSeries.
     *
     * @since 0.11
     */
    default DoubleSeries div(DoubleSeries s) {
        int len = size();
        DoubleAccum accumulator = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushDouble(this.getDouble(i) / s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default DoubleSeries mod(DoubleSeries s) {
        int len = size();
        DoubleAccum accumulator = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushDouble(this.getDouble(i) % s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries lt(DoubleSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getDouble(i) < s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(DoubleSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getDouble(i) <= s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(DoubleSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getDouble(i) > s.getDouble(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(DoubleSeries s) {
        int len = size();
        BoolAccum accumulator = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            accumulator.pushBool(this.getDouble(i) >= s.getDouble(i));
        }

        return accumulator.toSeries();
    }
}
