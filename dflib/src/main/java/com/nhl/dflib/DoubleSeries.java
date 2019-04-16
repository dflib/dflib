package com.nhl.dflib;

import com.nhl.dflib.filter.DoublePredicate;
import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.series.IntArraySeries;

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

    double getDouble(int index);

    void copyToDouble(double[] to, int fromOffset, int toOffset, int len);

    DoubleSeries materializeDouble();

    DoubleSeries concatDouble(DoubleSeries... other);

    DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive);

    DoubleSeries headDouble(int len);

    DoubleSeries tailDouble(int len);

    default DoubleSeries selectDouble(int... positions) {
        return selectDouble(new IntArraySeries(positions));
    }

    DoubleSeries selectDouble(IntSeries positions);

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexDouble(DoublePredicate predicate);
}
