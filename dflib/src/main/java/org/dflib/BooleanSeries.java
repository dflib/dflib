package org.dflib;

import org.dflib.builder.BoolAccum;
import org.dflib.op.BooleanSeriesOps;
import org.dflib.op.ReplaceOp;
import org.dflib.series.BooleanArraySeries;
import org.dflib.series.BooleanIndexedSeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;

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

    @Override
    default BooleanSeries compactBool() {
        return this;
    }

    @Override
    default Boolean get(int index) {
        return getBool(index);
    }

    boolean getBool(int index);

    @Override
    default Series<Boolean> replace(int index, Boolean with) {
        return with != null ? replaceBool(index, with) : ReplaceOp.replace(this, index, with);
    }

    /**
     * Returns a new Series with the value in the original Series at a given index replaced with the provided value.
     *
     * @since 2.0.0
     */
    default BooleanSeries replaceBool(int index, boolean with) {
        if (getBool(index) == with) {
            return this;
        }

        int len = size();

        BoolAccum accum = new BoolAccum(len);
        accum.fill(this, 0, 0, len);
        accum.fill(index, index + 1, with);

        return accum.toSeries();
    }

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

    BooleanSeries concatBool(BooleanSeries... other);

    @Override
    default BooleanSeries diff(Series<? extends Boolean> other) {
        return Diff.diffBool(this, other);
    }

    @Override
    default BooleanSeries intersect(Series<? extends Boolean> other) {
        return Intersect.intersectBool(this, other);
    }

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
     * Returns the index of a first "true" value in the Series, or -1 if all values are false.
     */
    int firstTrue();

    /**
     * Returns the index of a first "false" value in the Series, or -1 if all values are true.
     *
     * @since 1.1.0
     */
    int firstFalse();

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

    @Override
    BooleanSeries sample(int size);

    @Override
    BooleanSeries sample(int size, Random random);

    int countTrue();

    int countFalse();

    default boolean[] toBoolArray() {
        int len = size();
        boolean[] copy = new boolean[len];
        copyToBool(copy, 0, 0, len);
        return copy;
    }

    /**
     * Returns a series with produces a cumulative sum of each row from the beginning of the Series. "true" value is
     * assumed to be 1, and "false" - 0.
     *
     * @since 1.1.0
     */
    IntSeries cumSum();

    @Override
    default BooleanSeries eq(Series<?> s) {
        if (!(s instanceof BooleanSeries)) {
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
