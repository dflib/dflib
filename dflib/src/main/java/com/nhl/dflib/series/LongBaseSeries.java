package com.nhl.dflib.series;

import com.nhl.dflib.*;
import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.builder.LongAccum;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.builder.UniqueLongAccum;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * @since 0.6
 */
public abstract class LongBaseSeries implements LongSeries {

    /**
     * @since 0.18
     */
    @Override
    public <S> Series<S> castAs(Class<S> type) {
        if (!type.isAssignableFrom(Long.class) && !type.equals(Long.TYPE)) {
            throw new ClassCastException("LongSeries can not be cast to " + type);
        }

        return (Series<S>) this;
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Long> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Long> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedLong(fromInclusive, toExclusive);
    }

    @Override
    public Series<Long> select(IntSeries positions) {

        int h = positions.size();

        long[] data = new long[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain primitive and have to change to Series<Long>...
            if (index < 0) {
                return selectAsObjectSeries(positions);
            }

            data[i] = getLong(index);
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries select(ValuePredicate<Long> p) {
        return selectLong(p::test);
    }

    @Override
    public LongSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public LongSeries select(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        LongAccum data = new LongAccum();

        for (int i = 0; i < size(); i++) {
            if (positions.getBool(i)) {
                data.pushLong(getLong(i));
            }
        }

        return data.toSeries();
    }

    @Override
    public LongSeries selectLong(LongPredicate p) {
        LongAccum filtered = new LongAccum();

        int len = size();

        for (int i = 0; i < len; i++) {
            long v = getLong(i);
            if (p.test(v)) {
                filtered.pushLong(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public LongSeries sortLong() {
        int size = size();
        long[] sorted = new long[size];
        copyToLong(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new LongArraySeries(sorted);
    }

    @Override
    public LongSeries sort(Sorter... sorters) {
        return selectAsLongSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    // TODO: implement 'sortLong(LongComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"
    //   Reimplement this method to delegate to 'sortLong'
    @Override
    public LongSeries sort(Comparator<? super Long> comparator) {
        return selectAsLongSeries(new SeriesSorter<>(this).sortIndex(comparator));
    }

    private LongSeries selectAsLongSeries(IntSeries positions) {

        int h = positions.size();

        long[] data = new long[h];

        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getLong()" and fail there
            int index = positions.getInt(i);
            data[i] = getLong(index);
        }

        return new LongArraySeries(data);
    }

    private Series<Long> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Long[] data = new Long[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getLong(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public LongSeries concatLong(LongSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        int size = size();
        int h = size;
        for (LongSeries s : other) {
            h += s.size();
        }

        long[] data = new long[h];
        copyToLong(data, 0, 0, size);

        int offset = size;
        for (LongSeries s : other) {
            int len = s.size();
            s.copyToLong(data, 0, offset, len);
            offset += len;
        }

        return new LongArraySeries(data);
    }

    @Override
    public Series<Long> fillNulls(Long value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsFromSeries(Series<? extends Long> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @SafeVarargs
    @Override
    public final Series<Long> concat(Series<? extends Long>... other) {
        // concatenating as Double... to concat as DoubleSeries, "concatDouble" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Long>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Long get(int index) {
        return getLong(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getLong(i);
        }
    }

    @Override
    public IntSeries indexLong(LongPredicate predicate) {
        IntAccum index = new IntAccum();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getLong(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Long> predicate) {
        return indexLong(predicate::test);
    }

    @Override
    public BooleanSeries locateLong(LongPredicate predicate) {
        int len = size();

        BoolAccum matches = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            matches.pushBool(predicate.test(getLong(i)));
        }

        return matches.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<Long> predicate) {
        return locateLong(predicate::test);
    }

    @Override
    public Series<Long> replace(BooleanSeries condition, Long with) {
        return with != null
                ? replaceLong(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Long> replaceNoMatch(BooleanSeries condition, Long with) {
        return with != null
                ? replaceNoMatchLong(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make long versions of 'replace' public?

    private LongSeries replaceLong(BooleanSeries condition, long with) {
        int s = size();
        int r = Math.min(s, condition.size());
        LongAccum longs = new LongAccum(s);

        for (int i = 0; i < r; i++) {
            longs.pushLong(condition.getBool(i) ? with : getLong(i));
        }

        for (int i = r; i < s; i++) {
            longs.pushLong(getLong(i));
        }

        return longs.toSeries();
    }

    private LongSeries replaceNoMatchLong(BooleanSeries condition, long with) {

        int s = size();
        int r = Math.min(s, condition.size());
        LongAccum longs = new LongAccum(s);

        for (int i = 0; i < r; i++) {
            longs.pushLong(condition.getBool(i) ? getLong(i) : with);
        }

        if (s > r) {
            longs.fill(r, s, with);
        }

        return longs.toSeries();
    }

    private Series<Long> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<Long> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? null : getLong(i));
        }

        for (int i = r; i < s; i++) {
            values.push(getLong(i));
        }

        return values.toSeries();
    }

    private Series<Long> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<Long> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? getLong(i) : null);
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

        BoolAccum bools = new BoolAccum(s);

        if (another instanceof LongSeries) {
            LongSeries anotherLong = (LongSeries) another;

            for (int i = 0; i < s; i++) {
                bools.pushBool(getLong(i) == anotherLong.getLong(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.pushBool(Objects.equals(get(i), another.get(i)));
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

        BoolAccum bools = new BoolAccum(s);
        if (another instanceof LongSeries) {
            LongSeries anotherLong = (LongSeries) another;

            for (int i = 0; i < s; i++) {
                bools.pushBool(getLong(i) != anotherLong.getLong(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.pushBool(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries in(Object... values) {
        int s = size();

        if (values == null || values.length == 0) {
            return new FalseSeries(s);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Long) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new FalseSeries(s);
        }

        BoolAccum bools = new BoolAccum(s);
        for (int i = 0; i < s; i++) {
            bools.pushBool(set.contains(get(i)));
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries notIn(Object... values) {
        int s = size();

        if (values == null || values.length == 0) {
            return new TrueSeries(s);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Long) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new TrueSeries(s);
        }

        BoolAccum bools = new BoolAccum(s);
        for (int i = 0; i < s; i++) {
            bools.pushBool(!set.contains(get(i)));
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
    public LongSeries unique() {
        int size = size();
        if (size < 2) {
            return this;
        }

        LongAccum unique = new UniqueLongAccum();
        for (int i = 0; i < size; i++) {
            unique.push(get(i));
        }

        return unique.size() < size() ? unique.toSeries() : this;
    }

    @Override
    public DataFrame valueCounts() {
        return ValueCounts.valueCountsNoNulls(this);
    }

    // TODO: some optimized version of "primitive" group by ...

    @Override
    public SeriesGroupBy<Long> group() {
        return group(l -> l);
    }

    @Override
    public SeriesGroupBy<Long> group(ValueMapper<Long, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }

    /**
     * @since 0.7
     */
    @Override
    public LongSeries sample(int size) {
        return selectAsLongSeries(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    @Override
    public LongSeries sample(int size, Random random) {
        return selectAsLongSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
