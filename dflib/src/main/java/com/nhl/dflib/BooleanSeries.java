package com.nhl.dflib;

import com.nhl.dflib.op.BooleanSeriesOps;

import java.util.Comparator;
import java.util.Random;

/**
 * @since 0.6
 */
public interface BooleanSeries extends Series<Boolean> {

    /**
     * @deprecated in favor of {@link Series#ofBool(boolean...)}
     */
    @Deprecated(since = "0.16")
    static BooleanSeries forBooleans(boolean... bools) {
        return Series.ofBool(bools);
    }

    /**
     * @since 0.7
     * @deprecated since 0.16 in favor of {@link #mapAsBool(BooleanValueMapper)}
     */
    @Deprecated(since = "0.16")
    static <V> BooleanSeries forSeries(Series<V> series, BooleanValueMapper<? super V> converter) {
        return series.mapAsBool(converter);
    }

    static BooleanSeries andAll(BooleanSeries... series) {
        return BooleanSeriesOps.andAll(series);
    }

    static BooleanSeries orAll(BooleanSeries... series) {
        return BooleanSeriesOps.orAll(series);
    }


    @Override
    default Class<Boolean> getNominalType() {
        return Boolean.TYPE;
    }

    @Override
    default Class<?> getInferredType() {
        return Boolean.TYPE;
    }

    boolean getBoolean(int index);

    void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len);

    BooleanSeries materializeBoolean();

    BooleanSeries concatBoolean(BooleanSeries... other);

    BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive);

    BooleanSeries headBoolean(int len);

    BooleanSeries tailBoolean(int len);

    /**
     * @since 0.11
     */
    BooleanSeries selectBoolean(Condition condition);

    /**
     * @since 0.11
     */
    BooleanSeries selectBoolean(BooleanSeries positions);

    @Override
    BooleanSeries sort(Sorter... sorters);

    @Override
    BooleanSeries sort(Comparator<? super Boolean> comparator);

    /**
     * Returns the index of a first "true" value in the series, or -1 if all values are false.
     *
     * @since 0.11
     */
    int firstTrue();

    /**
     * Returns an IntSeries that represents positions in the Series that contain true values. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @return an IntSeries that represents positions in the Series that contain "true" values
     */
    IntSeries indexTrue();

    /**
     * Returns an IntSeries that represents positions in the Series that contain false values. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @return an IntSeries that represents positions in the Series that contain "false" values
     */
    IntSeries indexFalse();

    /**
     * @return true if all the elements in the series are "true"
     */
    boolean isTrue();

    /**
     * @return true if all the elements in the series are "false"
     */
    boolean isFalse();

    /**
     * @return a BooleanSeries that contains non-repeating values from this Series.
     */
    BooleanSeries uniqueBoolean();

    BooleanSeries and(BooleanSeries another);

    BooleanSeries or(BooleanSeries another);

    BooleanSeries not();

    /**
     * @since 0.7
     */
    @Override
    BooleanSeries sample(int size);

    /**
     * @since 0.7
     */
    @Override
    BooleanSeries sample(int size, Random random);

    /**
     * @since 0.7
     */
    int countTrue();

    /**
     * @since 0.7
     */
    int countFalse();

    /**
     * @since 0.7
     */
    default boolean[] toBooleanArray() {
        int len = size();
        boolean[] copy = new boolean[len];
        copyToBoolean(copy, 0, 0, len);
        return copy;
    }
}
