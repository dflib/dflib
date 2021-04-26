package com.nhl.dflib.series;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.IntComparator;
import com.nhl.dflib.sort.IntTimSort;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * @since 0.6
 */
public abstract class BooleanBaseSeries implements BooleanSeries {

    @Override
    public <V> Series<V> map(ValueMapper<Boolean, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Boolean> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Boolean> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedBoolean(fromInclusive, toExclusive);
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

            data[i] = getBoolean(index);
        }

        return new BooleanArraySeries(data);
    }

    // TODO: should we have "filterBoolean" ?
    @Override
    public Series<Boolean> filter(ValuePredicate<Boolean> p) {
        return select(index(p));
    }

    @Override
    public BooleanSeries filterBoolean(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        BooleanAccumulator data = new BooleanAccumulator();

        for (int i = 0; i < size(); i++) {
            if (positions.getBoolean(i)) {
                data.addBoolean(getBoolean(i));
            }
        }

        return data.toSeries();
    }

    @Override
    public Series<Boolean> filter(BooleanSeries positions) {
        return filterBoolean(positions);
    }

    @Override
    public Series<Boolean> sort(Comparator<? super Boolean> comparator) {
        int size = size();
        Boolean[] sorted = new Boolean[size];
        copyTo(sorted, 0, 0, size);
        Arrays.sort(sorted, comparator);
        return new ArraySeries<>(sorted);
    }

    @Override
    public IntSeries sortIndex(Comparator<? super Boolean> comparator) {
        int[] mutableIndex = SeriesSorter.rowNumberSequence(size());
        IntComparator intComparator =  (i1, i2) -> comparator.compare(get(i1), get(i2));
        IntTimSort.sort(mutableIndex, intComparator);
        return new IntArraySeries(mutableIndex);
    }

    private BooleanSeries selectAsBooleanSeries(IntSeries positions) {

        int h = positions.size();

        boolean[] data = new boolean[h];

        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getLong()" and fail there
            int index = positions.getInt(i);
            data[i] = getBoolean(index);
        }

        return new BooleanArraySeries(data);
    }

    private Series<Boolean> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Boolean[] data = new Boolean[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getBoolean(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public BooleanSeries concatBoolean(BooleanSeries... other) {
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
        copyToBoolean(data, 0, 0, size);

        int offset = size;
        for (BooleanSeries s : other) {
            int len = s.size();
            s.copyToBoolean(data, 0, offset, len);
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

    @Override
    public Series<Boolean> head(int len) {
        return headBoolean(len);
    }

    @Override
    public Series<Boolean> tail(int len) {
        return tailBoolean(len);
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
    public Series<Boolean> materialize() {
        return materializeBoolean();
    }

    @Override
    public Boolean get(int index) {
        return getBoolean(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getBoolean(i);
        }
    }

    @Override
    public IntSeries indexTrue() {
        IntAccumulator filtered = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (getBoolean(i)) {
                filtered.addInt(i);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries indexFalse() {
        IntAccumulator filtered = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (!getBoolean(i)) {
                filtered.addInt(i);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Boolean> predicate) {
        IntAccumulator index = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.addInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<Boolean> predicate) {
        int len = size();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            matches.addBoolean(predicate.test(get(i)));
        }

        return matches.toSeries();
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
        int s = size();
        int r = Math.min(s, condition.size());
        BooleanAccumulator bools = new BooleanAccumulator(s);

        for (int i = 0; i < r; i++) {
            bools.addBoolean(condition.getBoolean(i) ? with : getBoolean(i));
        }

        for (int i = r; i < s; i++) {
            bools.addBoolean(getBoolean(i));
        }

        return bools.toSeries();
    }

    private BooleanSeries replaceNoMatchBoolean(BooleanSeries condition, boolean with) {

        int s = size();
        int r = Math.min(s, condition.size());
        BooleanAccumulator bools = new BooleanAccumulator(s);

        for (int i = 0; i < r; i++) {
            bools.addBoolean(condition.getBoolean(i) ? getBoolean(i) : with);
        }

        if (s > r) {
            bools.fill(r, s, with);
        }

        return bools.toSeries();
    }

    private Series<Boolean> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Boolean> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? null : getBoolean(i));
        }

        for (int i = r; i < s; i++) {
            values.add(getBoolean(i));
        }

        return values.toSeries();
    }

    private Series<Boolean> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Boolean> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? getBoolean(i) : null);
        }

        if (s > r) {
            values.fill(r, s, null);
        }

        return values.toSeries();
    }


    @Override
    public BooleanSeries eq(Series<?> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);

        if (another instanceof BooleanSeries) {
            BooleanSeries anotherBool = (BooleanSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getBoolean(i) == anotherBool.getBoolean(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.addBoolean(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries ne(Series<?> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);
        if (another instanceof BooleanSeries) {
            BooleanSeries anotherBool = (BooleanSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getBoolean(i) != anotherBool.getBoolean(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.addBoolean(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    public BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    public boolean isTrue() {
        int s = size();

        for (int i = 0; i < s; i++) {
            if (!getBoolean(i)) {
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
            if (getBoolean(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Series<Boolean> unique() {
        return uniqueBoolean();
    }

    @Override
    public BooleanSeries uniqueBoolean() {
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
            not[i] = !getBoolean(i);
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
