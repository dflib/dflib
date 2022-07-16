package com.nhl.dflib.series;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.accumulator.UniqueIntAccumulator;
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
 * A base implementation of various boilerplate methods for {@link IntSeries}.
 *
 * @since 0.6
 */
public abstract class IntBaseSeries implements IntSeries {

    @Override
    public Series<Integer> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedInt(fromInclusive, toExclusive);
    }

    @Override
    public <V> Series<V> map(ValueMapper<Integer, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Integer> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Integer> select(IntSeries positions) {

        int h = positions.size();

        int[] data = new int[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain IntSeries and have to change to Series<Integer>...
            if (index < 0) {
                // TODO: implement NullableIntSeries as a Series of long[], where NULLs are encoded as Integer.MAX_VALUE + 1
                return selectAsObjectSeries(positions);
            }

            data[i] = getInt(index);
        }

        return new IntArraySeries(data);
    }

    @Override
    public Series<Integer> select(Condition condition) {
        return selectInt(condition);
    }

    @Override
    public Series<Integer> select(ValuePredicate<Integer> p) {
        return selectInt(p::test);
    }

    @Override
    public IntSeries selectInt(Condition condition) {
        return selectInt(condition.eval(this));
    }

    @Override
    public IntSeries selectInt(IntPredicate p) {
        IntAccumulator filtered = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            int v = getInt(i);
            if (p.test(v)) {
                filtered.addInt(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries selectInt(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        IntAccumulator data = new IntAccumulator();

        for (int i = 0; i < size(); i++) {
            if (positions.getBoolean(i)) {
                data.addInt(getInt(i));
            }
        }

        return data.toSeries();
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

    @Override
    public Series<Integer> select(BooleanSeries positions) {
        return selectInt(positions);
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
    public IntSeries concatInt(IntSeries... other) {

        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat
        int size = size();
        int h = size;
        for (IntSeries s : other) {
            h += s.size();
        }

        int[] data = new int[h];
        copyToInt(data, 0, 0, size);

        int offset = size;
        for (IntSeries s : other) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return new IntArraySeries(data);
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
    public Series<Integer> head(int len) {
        return headInt(len);
    }

    @Override
    public Series<Integer> tail(int len) {
        return tailInt(len);
    }

    @SafeVarargs
    @Override
    public final Series<Integer> concat(Series<? extends Integer>... other) {
        // concatenating as Integer... to concat as IntSeries, "concatInt" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Integer>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<Integer> materialize() {
        return materializeInt();
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getInt(i);
        }
    }

    @Override
    public IntSeries indexInt(IntPredicate predicate) {
        IntAccumulator index = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getInt(i))) {
                index.addInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Integer> predicate) {
        return indexInt(predicate::test);
    }

    @Override
    public BooleanSeries locateInt(IntPredicate predicate) {
        int len = size();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            matches.addBoolean(predicate.test(getInt(i)));
        }

        return matches.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<Integer> predicate) {
        return locateInt(predicate::test);
    }

    @Override
    public Series<Integer> replace(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceInt(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Integer> replaceNoMatch(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceNoMatchInt(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make int versions of replace public?

    private IntSeries replaceInt(BooleanSeries condition, int with) {
        int s = size();
        int r = Math.min(s, condition.size());
        IntAccumulator ints = new IntAccumulator(s);

        for (int i = 0; i < r; i++) {
            ints.addInt(condition.getBoolean(i) ? with : getInt(i));
        }

        for (int i = r; i < s; i++) {
            ints.addInt(getInt(i));
        }

        return ints.toSeries();
    }

    private IntSeries replaceNoMatchInt(BooleanSeries condition, int with) {

        int s = size();
        int r = Math.min(s, condition.size());
        IntAccumulator ints = new IntAccumulator(s);

        for (int i = 0; i < r; i++) {
            ints.addInt(condition.getBoolean(i) ? getInt(i) : with);
        }

        if (s > r) {
            ints.fill(r, s, with);
        }

        return ints.toSeries();
    }

    private Series<Integer> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Integer> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? null : getInt(i));
        }

        for (int i = r; i < s; i++) {
            values.add(getInt(i));
        }

        return values.toSeries();
    }

    private Series<Integer> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Integer> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? getInt(i) : null);
        }

        if (s > r) {
            values.fill(r, s, null);
        }

        return values.toSeries();
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
    public BooleanSeries eq(Series<?> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);

        if (another instanceof IntSeries) {
            IntSeries anotherInt = (IntSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getInt(i) == anotherInt.getInt(i));
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
        if (another instanceof IntSeries) {
            IntSeries anotherInt = (IntSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getInt(i) != anotherInt.getInt(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.addBoolean(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public Series<Integer> unique() {
        return uniqueInt();
    }

    @Override
    public IntSeries uniqueInt() {
        int size = size();
        if (size < 2) {
            return this;
        }

        IntAccumulator unique = new UniqueIntAccumulator();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
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

    /**
     * @since 0.7
     */
    @Override
    public IntSeries sample(int size) {
        return selectAsIntSeries(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    @Override
    public IntSeries sample(int size, Random random) {
        return selectAsIntSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
