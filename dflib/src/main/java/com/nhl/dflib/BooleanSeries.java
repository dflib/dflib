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
     * @deprecated since 0.16 in favor of {@link #mapAsBool(BoolValueMapper)}
     */
    @Deprecated(since = "0.16")
    static <V> BooleanSeries forSeries(Series<V> series, BoolValueMapper<? super V> converter) {
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

    /**
     * @since 0.16
     */
    boolean getBool(int index);

    /**
     * @deprecated in favor of {@link #getBool(int)}
     */
    @Deprecated(since = "0.16")
    default boolean getBoolean(int index) {
        return getBool(index);
    }

    /**
     * @since 0.16
     */
    void copyToBool(boolean[] to, int fromOffset, int toOffset, int len);

    /**
     * @deprecated in favor of {@link #copyToBool(boolean[], int, int, int)}
     */
    @Deprecated(since = "0.16")
    default void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len) {
        copyToBool(to, fromOffset, toOffset, len);
    }

    /**
     * @since 0.16
     */
    BooleanSeries materializeBool();

    /**
     * @deprecated in favor of {@link #materializeBool()}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries materializeBoolean() {
        return materializeBool();
    }

    /**
     * @since 0.16
     */
    BooleanSeries concatBool(BooleanSeries... other);

    /**
     * @deprecated in favor of {@link #concatBool(BooleanSeries...)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries concatBoolean(BooleanSeries... other) {
        return concatBool(other);
    }

    /**
     * @since 0.16
     */
    BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive);

    /**
     * @deprecated in favor of {@link #rangeOpenClosedBool(int, int)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive) {
        return rangeOpenClosedBool(fromInclusive, toExclusive);
    }

    /**
     * @since 0.16
     */
    BooleanSeries headBool(int len);

    /**
     * @deprecated in favor of {@link #headBool(int)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries headBoolean(int len) {
        return headBool(len);
    }

    /**
     * @since 0.16
     */
    BooleanSeries tailBool(int len);

    /**
     * @deprecated in favor of {@link #tailBool(int)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries tailBoolean(int len) {
        return tailBool(len);
    }

    /**
     * @since 0.16
     */
    BooleanSeries selectBool(Condition condition);

    /**
     * @since 0.11
     * @deprecated in favor of {@link #selectBool(Condition)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries selectBoolean(Condition condition) {
        return selectBool(condition);
    }

    /**
     * @since 0.16
     */
    BooleanSeries selectBool(BooleanSeries positions);

    /**
     * @since 0.11
     * @deprecated in favor of {@link #selectBool(BooleanSeries)}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries selectBoolean(BooleanSeries positions) {
        return selectBool(positions);
    }

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
     * @since 0.16
     */
    BooleanSeries uniqueBool();

    /**
     * @deprecated in favor of {@link #uniqueBool()}
     */
    @Deprecated(since = "0.16")
    default BooleanSeries uniqueBoolean() {
        return uniqueBool();
    }

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
     * @since 0.16
     */
    default boolean[] toBoolArray() {
        int len = size();
        boolean[] copy = new boolean[len];
        copyToBool(copy, 0, 0, len);
        return copy;
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #toBoolArray()}
     */
    @Deprecated(since = "0.16")
    default boolean[] toBooleanArray() {
        return toBoolArray();
    }
}
