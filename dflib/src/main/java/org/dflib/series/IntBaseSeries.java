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
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.UniqueIntAccum;
import org.dflib.groupby.SeriesGrouper;
import org.dflib.map.Mapper;
import org.dflib.sample.Sampler;
import org.dflib.sort.IntComparator;
import org.dflib.sort.IntTimSort;
import org.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * A base implementation of various boilerplate methods for {@link IntSeries}.
 */
public abstract class IntBaseSeries implements IntSeries {

    @Override
    public <S> Series<S> castAs(Class<S> type) {
        if (!type.isAssignableFrom(Integer.class) && !type.equals(Integer.TYPE)) {
            throw new ClassCastException("IntSeries can not be cast to " + type);
        }

        return (Series<S>) this;
    }

    @Override
    public Series<Integer> selectRange(int fromInclusive, int toExclusive) {
        return rangeInt(fromInclusive, toExclusive);
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Integer> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public IntSeries select(Predicate<Integer> p) {
        return selectInt(p::test);
    }

    @Override
    public IntSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public IntSeries select(BooleanSeries positions) {
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
            return Series.ofInt();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with an int[] gives very little performance gain (no vectorization). So keeping the Accum.

        IntAccum filtered = new IntAccum(len - i);
        filtered.pushInt(getInt(i));

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushInt(getInt(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries selectInt(IntPredicate p) {

        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(getInt(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.ofInt();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        IntAccum filtered = new IntAccum(len - i);
        filtered.pushInt(getInt(i));

        for (i++; i < len; i++) {
            int v = getInt(i);
            if (p.test(v)) {
                filtered.pushInt(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries sort(Sorter... sorters) {
        // TODO: can't use "sortInt(IntComparator), as "Comparators.of(s, sorters)" is not compatible.
        //   Need to analyze why.
        return selectAsIntSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    @Override
    public IntSeries sort(Comparator<? super Integer> comparator) {
        return sortInt(comparator::compare);
    }

    @Override
    public IntSeries sortInt() {
        int size = size();
        int[] sorted = new int[size];
        copyToInt(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new IntArraySeries(sorted);
    }

    @Override
    public IntSeries sortInt(IntComparator comparator) {
        int size = size();
        int[] sorted = new int[size];
        copyToInt(sorted, 0, 0, size);
        IntTimSort.sort(sorted, comparator);
        return new IntArraySeries(sorted);
    }

    @Override
    public IntSeries sortIndex(Comparator<? super Integer> comparator) {
        return sortIndexInt(comparator::compare);
    }

    @Override
    public IntSeries sortIndexInt() {
        return doSortIndexInt((i1, i2) -> getInt(i1) - getInt(i2));
    }

    @Override
    public IntSeries sortIndexInt(IntComparator comparator) {
        return doSortIndexInt((i1, i2) -> comparator.compare(getInt(i1), getInt(i2)));
    }

    private IntSeries doSortIndexInt(IntComparator comparator) {
        int[] mutableIndex = SeriesSorter.rowNumberSequence(size());
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }

    private IntSeries selectAsIntSeries(IntSeries positions) {

        int h = positions.size();

        int[] data = new int[h];

        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getInt()" and fail there
            int index = positions.getInt(i);
            data[i] = getInt(index);
        }

        return new IntArraySeries(data);
    }

    private Series<Integer> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Integer[] data = new Integer[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getInt(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsFromSeries(Series<? extends Integer> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getInt(fromOffset + i);
        }
    }

    @Override
    public IntSeries indexInt(IntPredicate predicate) {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(getInt(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(Predicate<Integer> predicate) {

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
    public BooleanSeries locateInt(IntPredicate predicate) {
        int len = size();

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = predicate.test(getInt(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public Series<Integer> replace(Map<Integer, Integer> oldToNewValues) {

        int len = size();
        int[] replaced = new int[len];

        for (int i = 0; i < len; i++) {
            Integer val = getInt(i);
            Integer newVal = oldToNewValues.getOrDefault(val, val);

            if (newVal == null) {
                // abandon the ints collected so far, and return object series
                return replaceAsObjects(oldToNewValues);
            }

            replaced[i] = newVal;
        }

        return Series.ofInt(replaced);
    }

    private Series<Integer> replaceAsObjects(Map<Integer, Integer> oldToNewValues) {

        int len = size();
        Integer[] replaced = new Integer[len];

        for (int i = 0; i < len; i++) {
            Integer val = getInt(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<Integer> replace(IntSeries positions, Series<Integer> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();

        // Quick check for nulls in "with". May result in false positives (no nulls in Series<Integer), but does not
        // require checking each value

        if (with instanceof IntSeries) {

            IntSeries withInt = (IntSeries) with;
            IntAccum values = new IntAccum(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), withInt.getInt(i));
            }

            return values.toSeries();
        } else {
            ObjectAccum<Integer> values = new ObjectAccum<>(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), with.get(i));
            }

            return values.toSeries();
        }
    }

    @Override
    public Series<Integer> replace(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceInt(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Integer> replaceExcept(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceNoMatchInt(condition, with)
                : nullifyNoMatch(condition);
    }

    private IntSeries replaceInt(BooleanSeries condition, int with) {
        int len = size();
        int r = Math.min(len, condition.size());
        int[] data = new int[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? with : getInt(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getInt(i);
        }

        return new IntArraySeries(data);
    }

    private IntSeries replaceNoMatchInt(BooleanSeries condition, int with) {

        int len = size();
        int r = Math.min(len, condition.size());
        int[] data = new int[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getInt(i) : with;
        }

        if (len > r) {
            Arrays.fill(data, r, len, with);
        }

        return new IntArraySeries(data);
    }

    private Series<Integer> nullify(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Integer[] data = new Integer[len];

        // TODO: delay "data" allocation until at least one match is encountered. This gives a chance to return "this"
        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? null : getInt(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getInt(i);
        }

        return new ArraySeries<>(data);
    }

    private Series<Integer> nullifyNoMatch(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Integer[] data = new Integer[len];

        // TODO: delay "data" allocation until at least one no-match is encountered. This gives a chance to return "this"
        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getInt(i) : null;
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
            if (o instanceof Integer) {
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
            if (o instanceof Integer) {
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
    public IntSeries unique() {
        int size = size();
        if (size < 2) {
            return this;
        }

        IntAccum unique = new UniqueIntAccum();
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
    public SeriesGroupBy<Integer> group() {
        return group(i -> i);
    }

    @Override
    public SeriesGroupBy<Integer> group(ValueMapper<Integer, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }


    @Override
    public IntSeries sample(int size) {
        return selectAsIntSeries(Sampler.sampleIndex(size, size()));
    }


    @Override
    public IntSeries sample(int size, Random random) {
        return selectAsIntSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
