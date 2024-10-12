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
import org.dflib.concat.SeriesConcat;
import org.dflib.groupby.SeriesGrouper;
import org.dflib.map.Mapper;
import org.dflib.sample.Sampler;
import org.dflib.set.Diff;
import org.dflib.set.Intersect;
import org.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

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
    public int position(T value) {
        int len = size();
        for (int i = 0; i < len; i++) {
            if (Objects.equals(value, get(i))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<T> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public <S> Series<S> castAs(Class<S> type) {
        Class<?> inferredType = getInferredType();

        if (!type.isAssignableFrom(inferredType)) {
            throw new ClassCastException("Inferred type of Series elements " + inferredType.getName() + " can not be cast to " + type);
        }

        return (Series<S>) this;
    }

    @Override
    public Series<T> selectRange(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new EmptySeries<>();
        }

        return fromInclusive == 0 && toExclusive == size()
                ? this

                // RangeSeries constructor does range checking
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
    public Series<T> diff(Series<? extends T> other) {
        return Diff.diff(this, other);
    }

    @Override
    public Series<T> intersect(Series<? extends T> other) {
        return Intersect.intersect(this, other);
    }

    @Override
    public Series<T> head(int len) {

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : selectRange(0, len);
    }

    @Override
    public Series<T> tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : selectRange(size - len, size);
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
    public Series<T> select(Predicate<T> p) {

        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(get(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.of();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        ObjectAccum<T> filtered = new ObjectAccum<>(len - i);
        filtered.push(get(i));

        for (i++; i < len; i++) {
            T v = get(i);
            if (p.test(v)) {
                filtered.push(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public Series<T> select(BooleanSeries positions) {

        int len = size();

        if (len != positions.size()) {
            throw new IllegalArgumentException("Positions size " + positions.size() + " is not the same as this size " + len);
        }

        // skip as many of the matches as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (positions.getBool(i)) {
                break;
            }
        }

        if (i == len) {
            return Series.of();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        ObjectAccum<T> filtered = new ObjectAccum<>(len - i);
        filtered.push(get(i));

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.push(get(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public Series<T> sort(Sorter... sorters) {
        return new SeriesSorter<>(this).sort(sorters);
    }

    @Override
    public Series<T> sort(Comparator<? super T> comparator) {
        return new SeriesSorter<>(this).sort(comparator);
    }

    @Override
    public IntSeries index(Predicate<T> predicate) {

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
    public Series<T> replace(IntSeries positions, Series<T> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();
        ObjectAccum<T> values = new ObjectAccum<>(s);

        // copy the old values first, then replace a subset with the values from the index
        values.fill(this, 0, 0, s);

        for (int i = 0; i < rs; i++) {
            values.replace(positions.getInt(i), with.get(i));
        }

        return values.toSeries();
    }

    @Override
    public Series<T> replace(Map<T, T> oldToNewValues) {
        int len = size();
        T[] replaced = (T[]) new Object[len];

        for (int i = 0; i < len; i++) {
            T val = get(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<T> replace(BooleanSeries condition, T with) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<T> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? with : get(i));
        }

        for (int i = r; i < s; i++) {
            values.push(get(i));
        }

        return values.toSeries();
    }

    @Override
    public Series<T> replaceExcept(BooleanSeries condition, T with) {

        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<T> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? get(i) : with);
        }

        if (s > r) {
            values.fill(r, s, with);
        }

        return values.toSeries();
    }

    @Override
    public BooleanSeries isNull() {
        int s = size();

        boolean[] data = new boolean[s];
        for (int i = 0; i < s; i++) {
            data[i] = get(i) == null;
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries isNotNull() {
        int s = size();

        boolean[] data = new boolean[s];
        for (int i = 0; i < s; i++) {
            data[i] = get(i) != null;
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries in(Object... values) {

        int s = size();

        if (values == null || values.length == 0) {
            return new FalseSeries(s);
        }

        Set<?> set = new HashSet<>(Arrays.asList(values));

        boolean[] data = new boolean[s];
        for (int i = 0; i < s; i++) {
            data[i] = set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries notIn(Object... values) {

        int s = size();

        if (values == null || values.length == 0) {
            return new TrueSeries(s);
        }

        Set<?> set = new HashSet<>(Arrays.asList(values));

        boolean[] data = new boolean[s];
        for (int i = 0; i < s; i++) {
            data[i] = !set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public Series<T> unique() {

        int size = size();
        if (size < 2) {
            return this;
        }

        Set<T> unique = toSet();
        return unique.size() < size() ? new ArraySeries<>(unique.toArray(s -> (T[]) new Object[s])) : this;
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
