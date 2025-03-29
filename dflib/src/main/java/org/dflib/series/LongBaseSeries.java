package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.Sorter;
import org.dflib.ValueMapper;
import org.dflib.ValueToRowMapper;
import org.dflib.builder.IntAccum;
import org.dflib.builder.LongAccum;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.UniqueLongAccum;
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
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * A base implementation of various boilerplate methods for {@link LongSeries}.
 */
public abstract class LongBaseSeries implements LongSeries {

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
    public Series<Long> selectRange(int fromInclusive, int toExclusive) {
        return rangeLong(fromInclusive, toExclusive);
    }

    @Override
    public LongSeries select(Predicate<Long> p) {
        return selectLong(p::test);
    }

    @Override
    public LongSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public LongSeries select(BooleanSeries positions) {
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
            return Series.ofLong();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with an long[] gives very little performance gain (no vectorization). So keeping the Accum.

        LongAccum filtered = new LongAccum(len - i);
        filtered.pushLong(getLong(i));

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushLong(getLong(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public LongSeries selectLong(LongPredicate p) {

        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(getLong(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.ofLong();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        LongAccum filtered = new LongAccum(len - i);
        filtered.pushLong(getLong(i));

        for (i++; i < len; i++) {
            long v = getLong(i);
            if (p.test(v)) {
                filtered.pushLong(v);
            }
        }

        return filtered.toSeries();
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

    @Override
    public LongSeries sortLong() {
        int size = size();
        long[] sorted = new long[size];
        copyToLong(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new LongArraySeries(sorted);
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

    @Override
    public Long get(int index) {
        return getLong(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getLong(fromOffset + i);
        }
    }

    @Override
    public IntSeries indexLong(LongPredicate predicate) {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(getLong(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(Predicate<Long> predicate) {
        // reimplementing instead of calling "indexInt", as it seems to be doing less (un)boxing and is about 13% faster
        // as a result

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
    public BooleanSeries locateLong(LongPredicate predicate) {
        int len = size();

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = predicate.test(getLong(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public Series<Long> replace(Map<Long, Long> oldToNewValues) {

        int len = size();
        long[] replaced = new long[len];

        for (int i = 0; i < len; i++) {
            Long val = getLong(i);
            Long newVal = oldToNewValues.getOrDefault(val, val);

            if (newVal == null) {
                // abandon the longs collected so far, and return object series
                return replaceAsObjects(oldToNewValues);
            }

            replaced[i] = newVal;
        }

        return Series.ofLong(replaced);
    }

    private Series<Long> replaceAsObjects(Map<Long, Long> oldToNewValues) {

        int len = size();
        Long[] replaced = new Long[len];

        for (int i = 0; i < len; i++) {
            Long val = getLong(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<Long> replace(IntSeries positions, Series<Long> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();

        // Quick check for nulls in "with". May result in false positives (no nulls in Series<Long>), but does not
        // require checking each value

        if (with instanceof LongSeries) {

            LongSeries withLong = (LongSeries) with;
            LongAccum values = new LongAccum(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), withLong.getLong(i));
            }

            return values.toSeries();
        } else {
            ObjectAccum<Long> values = new ObjectAccum<>(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), with.get(i));
            }

            return values.toSeries();
        }
    }

    @Override
    public Series<Long> replace(BooleanSeries condition, Long with) {
        return with != null
                ? replaceLong(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Long> replaceExcept(BooleanSeries condition, Long with) {
        return with != null
                ? replaceNoMatchLong(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make long versions of 'replace' public?

    private LongSeries replaceLong(BooleanSeries condition, long with) {
        int len = size();
        int r = Math.min(len, condition.size());
        long[] data = new long[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? with : getLong(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getLong(i);
        }

        return new LongArraySeries(data);
    }

    private LongSeries replaceNoMatchLong(BooleanSeries condition, long with) {

        int len = size();
        int r = Math.min(len, condition.size());
        long[] data = new long[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getLong(i) : with;
        }

        if (len > r) {
            Arrays.fill(data, r, len, with);
        }

        return new LongArraySeries(data);
    }

    private Series<Long> nullify(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Long[] data = new Long[len];

        // TODO: delay "data" allocation until at least one match is encountered. This gives a chance to return "this"
        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? null : getLong(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getLong(i);
        }

        return new ArraySeries<>(data);
    }

    private Series<Long> nullifyNoMatch(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Long[] data = new Long[len];

        // TODO: delay "data" allocation until at least one no-match is encountered. This gives a chance to return "this"
        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getLong(i) : null;
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
            // TODO: convert from other numeric types
            if (o instanceof Long) {
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
            // TODO: convert from other numeric types
            if (o instanceof Long) {
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


    @Override
    public LongSeries sample(int size) {
        return selectAsLongSeries(Sampler.sampleIndex(size, size()));
    }


    @Override
    public LongSeries sample(int size, Random random) {
        return selectAsLongSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
