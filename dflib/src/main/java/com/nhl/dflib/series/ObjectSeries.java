package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.ValueToRowMapper;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.series.builder.BooleanAccumulator;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.ObjectAccumulator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public abstract class ObjectSeries<T> implements Series<T> {

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public <V> Series<V> map(ValueMapper<T, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<T> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new EmptySeries<>();
        }

        return fromInclusive == 0 && toExclusive == size()
                ? this
                // RangeSeries does range checking
                : new RangeSeries<>(this, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public Series<T> concat(Series<? extends T>... other) {
        if (other.length == 0) {
            return this;
        }

        Series<T>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<T> head(int len) {
        return len >= size() ? this : rangeOpenClosed(0, len);
    }

    @Override
    public Series<T> tail(int len) {
        int size = size();
        return len >= size ? this : rangeOpenClosed(size - len, size);
    }

    @Override
    public Series<T> select(IntSeries positions) {
        return new IndexedSeries<>(this, positions);
    }

    @Override
    public Series<T> filter(ValuePredicate<T> p) {

        ObjectAccumulator<T> filtered = new ObjectAccumulator<>();

        int len = size();

        for (int i = 0; i < len; i++) {
            T value = get(i);
            if (p.test(value)) {
                filtered.add(value);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public Series<T> filter(BooleanSeries positions) {

        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        ObjectAccumulator<T> data = new ObjectAccumulator<>();

        for (int i = 0; i < size(); i++) {
            if (positions.getBoolean(i)) {
                data.add(get(i));
            }
        }

        return data.toSeries();
    }

    @Override
    public Series<T> sort(Comparator<? super T> comparator) {
        int size = size();
        T[] sorted = (T[]) new Object[size];
        copyTo(sorted, 0, 0, size);
        Arrays.sort(sorted, comparator);
        return new ArraySeries<>(sorted);
    }

    @Override
    public IntSeries index(ValuePredicate<T> predicate) {
        IntAccumulator index = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.add(i);
            }
        }

        return index.toIntSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<T> predicate) {
        int len = size();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            matches.add(predicate.test(get(i)));
        }

        return matches.toBooleanSeries();
    }

    @Override
    public Series<T> replace(BooleanSeries condition, T with) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator vals = new ObjectAccumulator(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? with : get(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(get(i));
        }

        return vals.toSeries();
    }

    @Override
    public Series<T> replaceNoMatch(BooleanSeries condition, T with) {

        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator vals = new ObjectAccumulator(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? get(i) : with);
        }

        if (s > r) {
            vals.fill(r, s, with);
        }

        return vals.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<T> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);
        for (int i = 0; i < s; i++) {
            bools.add(Objects.equals(get(i), another.get(i)));
        }

        return bools.toBooleanSeries();
    }

    @Override
    public BooleanSeries ne(Series<T> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanAccumulator bools = new BooleanAccumulator(s);
        for (int i = 0; i < s; i++) {
            bools.add(!Objects.equals(get(i), another.get(i)));
        }

        return bools.toBooleanSeries();
    }

    @Override
    public Series<T> unique() {

        int size = size();
        if (size < 2) {
            return this;
        }

        Set<Object> unique = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
        }

        return unique.size() < size() ? new ArraySeries<>(unique.toArray((T[]) new Object[unique.size()])) : this;
    }

    @Override
    public DataFrame valueCounts() {
        return ValueCounts.valueCountsMaybeNulls(this);
    }

    @Override
    public SeriesGroupBy<T> group() {
        return group(t -> t);
    }

    @Override
    public SeriesGroupBy<T> group(ValueMapper<T, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }

    @Override
    public Series<T> sample(int size) {
        return select(Sampler.sampleIndex(size, size()));
    }

    @Override
    public Series<T> sample(int size, Random random) {
        return select(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
