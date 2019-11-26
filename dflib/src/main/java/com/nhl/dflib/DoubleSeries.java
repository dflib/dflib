package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.series.builder.DoubleAccumulator;

import java.util.Random;

/**
 * A Series optimized to store and access primitive double values without <code>java.lang.Double</code> wrapper. Can also
 * pose as "Series&lt;Double>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface DoubleSeries extends Series<Double> {

    static DoubleSeries forDoubles(double... doubles) {
        return new DoubleArraySeries(doubles);
    }

    /**
     * @since 0.7
     */
    static <V> DoubleSeries forSeries(Series<V> series, DoubleValueMapper<? super V> converter) {
        int len = series.size();
        DoubleAccumulator a = new DoubleAccumulator(len);
        for (int i = 0; i < len; i++) {
            a.add(converter.map(series.get(i)));
        }

        return a.toDoubleSeries();
    }

    @Override
    default Class<Double> getType() {
        return Double.TYPE;
    }

    double getDouble(int index);

    void copyToDouble(double[] to, int fromOffset, int toOffset, int len);

    DoubleSeries materializeDouble();

    DoubleSeries concatDouble(DoubleSeries... other);

    DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive);

    DoubleSeries headDouble(int len);

    DoubleSeries tailDouble(int len);

    DoubleSeries filterDouble(DoublePredicate p);

    DoubleSeries filterDouble(BooleanSeries positions);

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
     * @since 0.7
     */
    double average();

    /**
     * @since 0.7
     */
    double median();
}
