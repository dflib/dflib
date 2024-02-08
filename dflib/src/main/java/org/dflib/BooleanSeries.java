package org.dflib;

import org.dflib.op.BooleanSeriesOps;
import org.dflib.series.BooleanArraySeries;
import org.dflib.series.BooleanIndexedSeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;

/**
 * @since 0.6
 */
public interface BooleanSeries extends Series<Boolean> {

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
     * @since 0.16
     */
    void copyToBool(boolean[] to, int fromOffset, int toOffset, int len);

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

    @Override
    default BooleanSeries diff(Series<? extends Boolean> other) {
        return Diff.diffBool(this, other);
    }

    @Override
    default BooleanSeries intersect(Series<? extends Boolean> other) {
        return Intersect.intersectBool(this, other);
    }

    /**
     * @since 1.0.0-M19
     */
    BooleanSeries rangeBool(int fromInclusive, int toExclusive);

    /**
     * @since 0.16
     * @deprecated in favor of {@link #rangeBool(int, int)}
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    default BooleanSeries rangeOpenClosedBool(int fromInclusive, int toExclusive) {
        return rangeBool(fromInclusive, toExclusive);
    }

    @Override
    default BooleanSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeBool(0, len);
    }

    /**
     * @since 0.16
     * @deprecated in favor of {@link #head(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries headBool(int len) {
        return head(len);
    }

    @Override
    default BooleanSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeBool(size - len, size);
    }

    /**
     * @since 0.16
     * @deprecated in favor of {@link #tail(int)}
     */
    @Deprecated(since = "0.18", forRemoval = true)
    default BooleanSeries tailBool(int len) {
        return tail(len);
    }

    @Override
    BooleanSeries select(BooleanSeries positions);

    @Override
    BooleanSeries select(ValuePredicate<Boolean> p);

    @Override
    BooleanSeries select(Condition condition);

    @Override
    default Series<Boolean> select(IntSeries positions) {
        return BooleanIndexedSeries.of(this, positions);
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
