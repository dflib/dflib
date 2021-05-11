package com.nhl.dflib.series;

import com.nhl.dflib.*;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public abstract class ObjectSeries<T> implements Series<T> {

    protected Class<?> nominalType;
    protected Class<?> inferredType;

    protected ObjectSeries(Class<?> nominalType) {
        this.nominalType = Objects.requireNonNull(nominalType);
    }

    @Override
    public Class<?> getNominalType() {
        return nominalType;
    }

    @Override
    public Class<?> getInferredType() {
        return inferredType != null ? inferredType : (inferredType = inferType());
    }

    protected Class<?> inferType() {

        // Check value types and use the most specific common superclass...
        Class<?> type = null;
        Class<?> terminal = Object.class;
        int size = size();

        for (int i = 0; i < size && type != terminal; i++) {
            Object v = get(i);
            if (v != null) {
                type = type != null
                        ? findMostSpecificCommonSuperclass(type, v.getClass())
                        : v.getClass();
            }
        }

        return type != null ? type : Object.class;
    }

    private Class<?> findMostSpecificCommonSuperclass(Class<?> t1, Class<?> t2) {

        if (t1 == t2) {
            return t1;
        }

        // randomly assume t1 is more likely to be higher in the hierarchy
        // (so the caller should pass assumed superclass as the first arg to take advantage of this)
        if (t1.isAssignableFrom(t2)) {
            return t1;
        }

        while ((t1 = t1.getSuperclass()) != null) {
            if (t1.isAssignableFrom(t2)) {
                return t1;
            }
        }

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

    @SafeVarargs
    @Override
    public final Series<T> concat(Series<? extends T>... other) {
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
    public Series<T> select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public Series<T> select(IntSeries positions) {
        return new IndexedSeries<>(this, positions);
    }

    @Override
    public Series<T> select(ValuePredicate<T> p) {

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
    public Series<T> select(BooleanSeries positions) {

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
                index.addInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<T> predicate) {
        int len = size();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            matches.addBoolean(predicate.test(get(i)));
        }

        return matches.toSeries();
    }

    @Override
    public Series<T> replace(BooleanSeries condition, T with) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<T> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? with : get(i));
        }

        for (int i = r; i < s; i++) {
            values.add(get(i));
        }

        return values.toSeries();
    }

    @Override
    public Series<T> replaceNoMatch(BooleanSeries condition, T with) {

        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<T> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? get(i) : with);
        }

        if (s > r) {
            values.fill(r, s, with);
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
        for (int i = 0; i < s; i++) {
            bools.addBoolean(Objects.equals(get(i), another.get(i)));
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
        for (int i = 0; i < s; i++) {
            bools.addBoolean(!Objects.equals(get(i), another.get(i)));
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries isNull() {
        int s = size();

        BooleanAccumulator bools = new BooleanAccumulator(s);
        for (int i = 0; i < s; i++) {
            bools.addBoolean(get(i) == null);
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries isNotNull() {
        int s = size();

        BooleanAccumulator bools = new BooleanAccumulator(s);
        for (int i = 0; i < s; i++) {
            bools.addBoolean(get(i) != null);
        }

        return bools.toSeries();
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
