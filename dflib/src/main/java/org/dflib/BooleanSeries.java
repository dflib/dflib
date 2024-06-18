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
import java.util.function.Predicate;

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

    @Override
    default int position(Boolean value) {
        if (value == null) {
            return -1;
        }

        boolean pval = value;
        int len = size();
        for (int i = 0; i < len; i++) {
            if (pval == getBool(i)) {
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

        boolean[] bools = new boolean[len];
        for (int i = 0; i < len; i++) {
            if (values[i] instanceof Boolean) {
                bools[i] = (Boolean) values[i];
            } else {
                return Series.super.expand(values);
            }
        }

        return expandBool(bools);
    }

    /**
     * @since 1.0.0-M21
     */
    default BooleanSeries expandBool(boolean... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        boolean[] expanded = new boolean[llen + rlen];
        this.copyToBool(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.ofBool(expanded);
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

    @Override
    default BooleanSeries head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : rangeBool(0, len);
    }

    @Override
    default BooleanSeries tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : rangeBool(size - len, size);
    }

    @Override
    BooleanSeries select(BooleanSeries positions);

    @Override
    BooleanSeries select(Predicate<Boolean> p);

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
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)},
     * only usually much faster.
     *
     * @return an IntSeries that represents positions in the Series that contain "true" values
     */
    IntSeries indexTrue();

    /**
     * Returns an IntSeries that represents positions in the Series that contain false values. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(Predicate)},
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
