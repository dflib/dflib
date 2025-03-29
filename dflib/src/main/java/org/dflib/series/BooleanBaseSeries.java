package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.Sorter;
import org.dflib.ValueMapper;
import org.dflib.ValueToRowMapper;
import org.dflib.builder.BoolAccum;
import org.dflib.builder.BoolBuilder;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;
import org.dflib.concat.SeriesConcat;
import org.dflib.groupby.SeriesGrouper;
import org.dflib.map.Mapper;
import org.dflib.sample.Sampler;
import org.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A base implementation of various boilerplate methods for {@link BooleanSeries}.
 */
public abstract class BooleanBaseSeries implements BooleanSeries {

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
    public Series<Boolean> selectRange(int fromInclusive, int toExclusive) {
        return rangeBool(fromInclusive, toExclusive);
    }

    @Override
    public BooleanSeries select(Predicate<Boolean> p) {
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

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (positions.getBool(i)) {
                break;
            }
        }

        if (i == len) {
            return Series.ofBool();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with a boolean[] gives very little performance gain (no vectorization). So keeping the Accum.

        BoolAccum filtered = new BoolAccum(len - i);
        filtered.pushBool(getBool(i));

        for (i++; i < len; i++) {
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

        // unlike SelectSeries, we do not expect negative ints in the index.
        // So if it happens, let it fall through to "getLong()" and fail there
        return BoolBuilder.buildSeries(i -> getBool(positions.getInt(i)), positions.size());
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

        Series<Boolean>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getBool(fromOffset + i);
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
    public IntSeries index(Predicate<Boolean> predicate) {

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
    public Series<Boolean> replace(Map<Boolean, Boolean> oldToNewValues) {

        int len = size();
        boolean[] replaced = new boolean[len];

        for (int i = 0; i < len; i++) {
            Boolean val = getBool(i);
            Boolean newVal = oldToNewValues.getOrDefault(val, val);

            if (newVal == null) {
                // abandon the booleans collected so far, and return object series
                return replaceAsObjects(oldToNewValues);
            }

            replaced[i] = newVal;
        }

        return Series.ofBool(replaced);
    }

    private Series<Boolean> replaceAsObjects(Map<Boolean, Boolean> oldToNewValues) {

        int len = size();
        Boolean[] replaced = new Boolean[len];

        for (int i = 0; i < len; i++) {
            Boolean val = getBool(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<Boolean> replace(IntSeries positions, Series<Boolean> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();

        // Quick check for nulls in "with". May result in false positives (no nulls in Series<Boolean>), but does not
        // require checking each value

        if (with instanceof BooleanSeries) {

            BooleanSeries withBool = (BooleanSeries) with;
            BoolAccum values = new BoolAccum(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), withBool.getBool(i));
            }

            return values.toSeries();
        } else {
            ObjectAccum<Boolean> values = new ObjectAccum<>(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), with.get(i));
            }

            return values.toSeries();
        }
    }

    @Override
    public Series<Boolean> replace(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceBoolean(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Boolean> replaceExcept(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceNoMatchBoolean(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make bool versions of replace public?

    private BooleanSeries replaceBoolean(BooleanSeries condition, boolean with) {
        int len = size();
        int r = Math.min(len, condition.size());

        return BoolBuilder.buildSeries(i -> i < r && condition.getBool(i) ? with : getBool(i), len);
    }

    private BooleanSeries replaceNoMatchBoolean(BooleanSeries condition, boolean with) {
        int len = size();
        int r = Math.min(len, condition.size());

        return BoolBuilder.buildSeries(i -> i < r && condition.getBool(i) ? getBool(i) : with, len);
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

        return BoolBuilder.buildSeries(i -> set.contains(get(i)), len);
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

        return BoolBuilder.buildSeries(i -> !set.contains(get(i)), len);
    }

    @Override
    public boolean isTrue() {
        // empty series is neither true nor false
        return size() > 0 && firstFalse() == -1;
    }

    @Override
    public boolean isFalse() {
        // empty series is neither true nor false
        return size() > 0 && firstTrue() == -1;
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
            return iFalse < 0 ? Series.ofBool() : Series.ofBool(false);
        } else {
            return iFalse < 0
                    ? Series.ofBool(true)
                    : iTrue < iFalse ? Series.ofBool(true, false) : Series.ofBool(false, true);
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

        return BoolBuilder.buildSeries(i -> !getBool(i), size);
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


    @Override
    public BooleanSeries sample(int size) {
        return selectAsBooleanSeries(Sampler.sampleIndex(size, size()));
    }


    @Override
    public BooleanSeries sample(int size, Random random) {
        return selectAsBooleanSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
