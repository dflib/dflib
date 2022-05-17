package com.nhl.dflib;

import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.FloatAccumulator;
import com.nhl.dflib.series.FloatArraySeries;

import java.util.Comparator;
import java.util.Random;

/**
 * A Series optimized to store and access primitive float values without <code>java.lang.Float</code> wrapper. Can also
 * pose as "Series&lt;Float>", although this is not the most efficient way of using it.
 *
 * @since 0.6
 */
public interface FloatSeries extends Series<Float> {

    static FloatSeries forFloats(float... floats) {
        return new FloatArraySeries(floats);
    }

    /**
     * @since 0.7
     */
    static <V> FloatSeries forSeries(Series<V> series, FloatValueMapper<? super V> converter) {
        int len = series.size();
        FloatAccumulator a = new FloatAccumulator(len);
        for (int i = 0; i < len; i++) {
            a.addFloat(converter.map(series.get(i)));
        }

        return a.toSeries();
    }

    @Override
    default Class<Float> getNominalType() {
        return Float.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Float.TYPE;
    }

    float getFloat(int index);

    void copyToFloat(float[] to, int fromOffset, int toOffset, int len);

    FloatSeries materializeFloat();

    FloatSeries concatFloat(FloatSeries... other);

    FloatSeries rangeOpenClosedFloat(int fromInclusive, int toExclusive);

    FloatSeries headFloat(int len);

    FloatSeries tailFloat(int len);

    /**
     * @since 0.11
     */
    FloatSeries selectFloat(Condition condition);

    /**
     * @since 0.11
     */
    FloatSeries selectFloat(FloatPredicate p);

    /**
     * @since 0.11
     */
    FloatSeries selectFloat(BooleanSeries positions);

    /**
     * @deprecated since 0.11 in favor of {@link #selectFloat(FloatPredicate)}
     */
    @Deprecated
    default FloatSeries filterFloat(FloatPredicate p) {
        return selectFloat(p);
    }

    /**
     * @deprecated since 0.11 in favor of {@link #selectFloat(BooleanSeries)}
     */
    @Deprecated
    default FloatSeries filterFloat(BooleanSeries positions) {
        return selectFloat(positions);
    }


    @Override
    FloatSeries sort(Sorter... sorters);

    @Override
    FloatSeries sort(Comparator<? super Float> comparator);

    FloatSeries sortFloat();

    // TODO: implement 'sortFloat(FloatComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries indexFloat(FloatPredicate predicate);

    BooleanSeries locateFloat(FloatPredicate predicate);

    /**
     * @return a FloatSeries that contains non-repeating values from this Series.
     */
    FloatSeries uniqueFloat();

    /**
     * @since 0.7
     */
    @Override
    FloatSeries sample(int size);

    /**
     * @since 0.7
     */
    @Override
    FloatSeries sample(int size, Random random);

    /**
     * @since 0.7
     */
    default float[] toFloatArray() {
        int len = size();
        float[] copy = new float[len];
        copyToFloat(copy, 0, 0, len);
        return copy;
    }

    /**
     * @since 0.7
     */
    float max();

    /**
     * @since 0.7
     */
    float min();

    /**
     * @since 0.7
     */
    // TODO: deal with overflow?
    float sum();

    /**
     * @deprecated since 0.11 in favor of shorter {@link #avg()}
     */
    @Deprecated
    default float average() {
        return avg();
    }

    /**
     * @since 0.11
     */
    float avg();

    /**
     * @since 0.7
     */
    float median();

    /**
     * @since 0.11
     */
    default FloatSeries add(FloatSeries s) {
        int len = size();
        FloatAccumulator accumulator = new FloatAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addFloat(this.getFloat(i) + s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs subtraction operation between this and another FloatSeries.
     *
     * @since 0.11
     */
    default FloatSeries sub(FloatSeries s) {
        int len = size();
        FloatAccumulator accumulator = new FloatAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addFloat(this.getFloat(i) - s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs multiplication operation between this and another FloatSeries.
     *
     * @since 0.11
     */
    default FloatSeries mul(FloatSeries s) {
        int len = size();
        FloatAccumulator accumulator = new FloatAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addFloat(this.getFloat(i) * s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * Performs division operation between this and another FloatSeries.
     *
     * @since 0.11
     */
    default FloatSeries div(FloatSeries s) {
        int len = size();
        FloatAccumulator accumulator = new FloatAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addFloat(this.getFloat(i) / s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default FloatSeries mod(FloatSeries s) {
        int len = size();
        FloatAccumulator accumulator = new FloatAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addFloat(this.getFloat(i) % s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries lt(FloatSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getFloat(i) < s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries le(FloatSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getFloat(i) <= s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries gt(FloatSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getFloat(i) > s.getFloat(i));
        }

        return accumulator.toSeries();
    }

    /**
     * @since 0.11
     */
    default BooleanSeries ge(FloatSeries s) {
        int len = size();
        BooleanAccumulator accumulator = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            accumulator.addBoolean(this.getFloat(i) >= s.getFloat(i));
        }

        return accumulator.toSeries();
    }
}
