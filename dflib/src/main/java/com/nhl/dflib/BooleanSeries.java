package com.nhl.dflib;

import com.nhl.dflib.op.BooleanSeriesOps;
import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.series.FalseSeries;
import com.nhl.dflib.series.TrueSeries;
import com.nhl.dflib.set.Diff;
import com.nhl.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;

/**
 * @since 0.6
 */
public interface BooleanSeries extends Series<Boolean> {

    /**
     * @deprecated in favor of {@link Series#ofBool(boolean...)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static BooleanSeries forBooleans(boolean... bools) {
        return Series.ofBool(bools);
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #mapAsBool(BoolValueMapper)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
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

    @Override
    default BooleanSeries castAsBool() {
        return this;
    }

    /**
     * @since 0.16
     */
    boolean getBool(int index);

    /**
     * @deprecated in favor of {@link #getBool(int)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
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
    @Deprecated(since = "0.16", forRemoval = true)
    default void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len) {
        copyToBool(to, fromOffset, toOffset, len);
    }

    @Override
    BooleanSeries materialize();

    /**
     * @since 0.16
     * @deprecated in favor of {@link #materialize()}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries materializeBool() {
        return materialize();
    }

    /**
     * @deprecated in favor of {@link #materialize()}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries materializeBoolean() {
        return materialize();
    }

    /**
     * @since 0.18
     */
    @Override
    default Series<?> add(Object value) {
        return value instanceof Boolean
                ? addBool((Boolean) value)
                : Series.super.add(value);
    }

    /**
     * Creates a new Series with a provided int appended to the end of this Series.
     *
     * @since 0.18
     */
    default BooleanSeries addBool(boolean val) {
        int s = size();

        boolean[] data = new boolean[s + 1];
        this.copyToBool(data, 0, 0, s);
        data[s] = val;
        return new BooleanArraySeries(data);
    }

    /**
     * @since 0.16
     */
    BooleanSeries concatBool(BooleanSeries... other);

    /**
     * @deprecated in favor of {@link #concatBool(BooleanSeries...)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries concatBoolean(BooleanSeries... other) {
        return concatBool(other);
    }

    @Override
    default BooleanSeries diff(Series<? extends Boolean> other) {
        return Diff.diffBool(this, other);
    }

    @Override
    default BooleanSeries intersect(Series<? extends Boolean> other) {
        return Intersect.intersectBool(this, other);
    }

    /**
     * @since 0.16
     */
    BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive);

    /**
     * @deprecated in favor of {@link #rangeOpenClosedBool(int, int)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive) {
        return rangeOpenClosedBool(fromInclusive, toExclusive);
    }

    @Override
    BooleanSeries head(int len);

    /**
     * @since 0.16
     * @deprecated in favor of {@link #head(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries headBool(int len) {
        return head(len);
    }

    /**
     * @deprecated in favor of {@link #head(int)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries headBoolean(int len) {
        return head(len);
    }

    @Override
    BooleanSeries tail(int len);

    /**
     * @since 0.16
     * @deprecated in favor of {@link #tail(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries tailBool(int len) {
        return tail(len);
    }

    /**
     * @deprecated in favor of {@link #tail(int)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries tailBoolean(int len) {
        return tail(len);
    }

    @Override
    BooleanSeries select(BooleanSeries positions);

    @Override
    BooleanSeries select(ValuePredicate<Boolean> p);

    @Override
    BooleanSeries select(Condition condition);

    /**
     * @since 0.16
     * @deprecated in favor of {@link #select(Condition)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries selectBool(Condition condition) {
        return select(condition);
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #select(Condition)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries selectBoolean(Condition condition) {
        return selectBool(condition);
    }

    /**
     * @since 0.16
     * @deprecated in favor of {@link #select(BooleanSeries)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries selectBool(BooleanSeries positions) {
        return select(positions);
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #select(BooleanSeries)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
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

    @Override
    default BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    default BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    BooleanSeries unique();

    /**
     * @return a BooleanSeries that contains non-repeating values from this Series.
     * @since 0.16
     * @deprecated in favor of {@link #unique()}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries uniqueBool() {
        return unique();
    }

    /**
     * @deprecated in favor of {@link #unique()}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    default BooleanSeries uniqueBoolean() {
        return unique();
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
    @Deprecated(since = "0.16", forRemoval = true)
    default boolean[] toBooleanArray() {
        return toBoolArray();
    }

    @Override
    default BooleanSeries eq(Series<?> s) {
        if (!(s instanceof LongSeries)) {
            return Series.super.eq(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        BooleanSeries anotherBool = (BooleanSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getBool(i) == anotherBool.getBool(i);
        }

        return new BooleanArraySeries(data);
    }

    @Override
    default BooleanSeries ne(Series<?> s) {
        if (!(s instanceof BooleanSeries)) {
            return Series.super.ne(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        boolean[] data = new boolean[len];
        BooleanSeries anotherBool = (BooleanSeries) s;

        for (int i = 0; i < len; i++) {
            data[i] = getBool(i) != anotherBool.getBool(i);
        }

        return new BooleanArraySeries(data);
    }
}
