package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.ValueToRowMapper;
import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A base implementation of various boilerplate methods for {@link BooleanSeries}.
 *
 * @since 0.6
 */
public abstract class BooleanBaseSeries implements BooleanSeries {

    /**
     * @since 0.18
     */
    @Override
    public <S> Series<S> castAs(Class<S> type) {
        if (!type.equals(Boolean.class) && !type.equals(Boolean.TYPE)) {
            throw new ClassCastException("BooleanSeries can not be cast to " + type);
        }

        return (Series<S>) this;
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Boolean> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Boolean> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedBool(fromInclusive, toExclusive);
    }

    @Override
    public Series<Boolean> select(IntSeries positions) {

        int h = positions.size();

        boolean[] data = new boolean[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain IntSeries and have to change to Series<Boolean>...
            if (index < 0) {
                // TODO: implement NullableBooleanSeries as a Series of short[], where null, true, false are encoded as 0, 1, -1
                return selectAsObjectSeries(positions);
            }

            data[i] = getBool(index);
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries select(ValuePredicate<Boolean> p) {
        return selectAsBooleanSeries(index(p));
    }

    @Override
    public BooleanSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public BooleanSeries select(BooleanSeries positions) {
        int len = size();

        if (len != positions.size()) {
            throw new IllegalArgumentException("Positions size " + positions.size() + " is not the same as this size " + len);
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.

        // Replacing the Accum with a boolean[] gives very little performance gain, presumably due to the need for another
        // loop index, that does not allow HotSpot to vectorize the loop. So keeping the Accum.

        BoolAccum filtered = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushBool(getBool(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public BooleanSeries sort(Sorter... sorters) {
        return selectAsBooleanSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    @Override
    public BooleanSeries sort(Comparator<? super Boolean> comparator) {
        return selectAsBooleanSeries(new SeriesSorter<>(this).sortIndex(comparator));
    }

    private BooleanSeries selectAsBooleanSeries(IntSeries positions) {

        int h = positions.size();

        boolean[] data = new boolean[h];

        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getLong()" and fail there
            int index = positions.getInt(i);
            data[i] = getBool(index);
        }

        return new BooleanArraySeries(data);
    }

    private Series<Boolean> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Boolean[] data = new Boolean[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getBool(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public BooleanSeries concatBool(BooleanSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        int size = size();
        int h = size;
        for (BooleanSeries s : other) {
            h += s.size();
        }

        boolean[] data = new boolean[h];
        copyToBool(data, 0, 0, size);

        int offset = size;
        for (BooleanSeries s : other) {
            int len = s.size();
            s.copyToBool(data, 0, offset, len);
            offset += len;
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public Series<Boolean> fillNullsFromSeries(Series<? extends Boolean> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Boolean> fillNulls(Boolean value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Boolean> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<Boolean> fillNullsForward() {
        return this;
    }

    @SafeVarargs
    @Override
    public final Series<Boolean> concat(Series<? extends Boolean>... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Boolean>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Boolean get(int index) {
        return getBool(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getBool(i);
        }
    }

    @Override
    public IntSeries indexTrue() {
        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (getBool(i)) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries indexFalse() {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (!getBool(i)) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Boolean> predicate) {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public Series<Boolean> replace(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceBoolean(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Boolean> replaceNoMatch(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceNoMatchBoolean(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make bool versions of replace public?

    private BooleanSeries replaceBoolean(BooleanSeries condition, boolean with) {
        int len = size();
        int r = Math.min(len, condition.size());
        boolean[] bools = new boolean[len];

        for (int i = 0; i < r; i++) {
            bools[i] = condition.getBool(i) ? with : getBool(i);
        }

        for (int i = r; i < len; i++) {
            bools[i] = getBool(i);
        }

        return new BooleanArraySeries(bools);
    }

    private BooleanSeries replaceNoMatchBoolean(BooleanSeries condition, boolean with) {

        int len = size();
        int r = Math.min(len, condition.size());
        boolean[] bools = new boolean[len];

        for (int i = 0; i < r; i++) {
            bools[i] = condition.getBool(i) ? getBool(i) : with;
        }

        if (len > r) {
            Arrays.fill(bools, r, len, with);
        }

        return new BooleanArraySeries(bools);
    }

    private Series<Boolean> nullify(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Boolean[] data = new Boolean[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? null : getBool(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getBool(i);
        }

        return new ArraySeries<>(data);
    }

    private Series<Boolean> nullifyNoMatch(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Boolean[] data = new Boolean[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getBool(i) : null;
        }

        if (len > r) {
            Arrays.fill(data, r, len, null);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public BooleanSeries in(Object... values) {
        int len = size();

        if (values == null || values.length == 0) {
            return new FalseSeries(len);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            if (o instanceof Boolean) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new FalseSeries(len);
        }

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries notIn(Object... values) {
        int len = size();

        if (values == null || values.length == 0) {
            return new TrueSeries(len);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            if (o instanceof Boolean) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new TrueSeries(len);
        }

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = !set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public boolean isTrue() {
        int s = size();

        for (int i = 0; i < s; i++) {
            if (!getBool(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isFalse() {
        int s = size();

        // empty series is considered true
        if (s == 0) {
            return false;
        }

        for (int i = 0; i < s; i++) {
            if (getBool(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public BooleanSeries unique() {
        // unlike other primitive series, Boolean is categorical data, and there can be at most 2 values in the final
        // array... we can optimize around that

        int size = size();
        if (size < 2) {
            return this;
        }

        int iTrue = -1;
        int iFalse = -1;

        for (int i = 0; i < size && (iTrue < 0 || iFalse < 0); i++) {
            if (get(i)) {
                iTrue = i;
            } else {
                iFalse = i;
            }
        }

        if (iTrue < 0) {
            return new BooleanArraySeries(iFalse < 0 ? new boolean[0] : new boolean[]{false});
        } else {
            return new BooleanArraySeries(iFalse < 0 ? new boolean[]{true} : iTrue < iFalse ? new boolean[]{true, false} : new boolean[]{false, true});
        }
    }

    @Override
    public BooleanSeries and(BooleanSeries another) {
        return BooleanSeries.andAll(this, another);
    }

    @Override
    public BooleanSeries or(BooleanSeries another) {
        return BooleanSeries.orAll(this, another);
    }

    @Override
    public BooleanSeries not() {
        int size = size();
        if (size == 0) {
            return this;
        }

        boolean[] not = new boolean[size];
        for (int i = 0; i < size; i++) {
            not[i] = !getBool(i);
        }

        return new BooleanArraySeries(not);
    }

    @Override
    public DataFrame valueCounts() {
        return ValueCounts.valueCountsNoNulls(this);
    }

    // TODO: some optimized version of "primitive" group by ...

    @Override
    public SeriesGroupBy<Boolean> group() {
        return group(i -> i);
    }

    @Override
    public SeriesGroupBy<Boolean> group(ValueMapper<Boolean, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }

    /**
     * @since 0.7
     */
    @Override
    public BooleanSeries sample(int size) {
        return selectAsBooleanSeries(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    @Override
    public BooleanSeries sample(int size, Random random) {
        return selectAsBooleanSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
