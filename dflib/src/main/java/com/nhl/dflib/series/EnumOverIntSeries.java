package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.EnumSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.filter.IntPredicate;
import com.nhl.dflib.filter.ValuePredicate;

/**
 * A wrapper around IntSeries that presents data as a Series of enums. Does not allow nulls.
 *
 * @param <T>
 * @since 0.6
 */
public class EnumOverIntSeries<T extends Enum<T>> implements EnumSeries<T> {

    private Class<T> type;
    private IntSeries intData;
    private T[] allValues;

    public EnumOverIntSeries(Class<T> type, IntSeries intData) {
        this(type, intData, type.getEnumConstants());
    }

    protected EnumOverIntSeries(Class<T> type, IntSeries intData, T[] allValues) {
        this.type = type;
        this.intData = intData;
        this.allValues = allValues;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public int size() {
        return intData.size();
    }

    @Override
    public T get(int index) {
        return allValues[intData.getInt(index)];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = get(i);
        }
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
    public Series<T> materialize() {
        return this;
    }

    @Override
    public Series<T> fillNulls(T value) {
        // this series has no nulls
        return this;
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        // this series has no nulls
        return this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        // this series has no nulls
        return this;
    }

    @Override
    public Series<T> fillNullsForward() {
        // this series has no nulls
        return this;
    }

    @Override
    public Series<T> concat(Series<? extends T>... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<T>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<T> head(int len) {
        return len < size() ? new EnumOverIntSeries<>(type, intData.headInt(len), allValues) : this;
    }

    @Override
    public Series<T> tail(int len) {
        return len < size() ? new EnumOverIntSeries<>(type, intData.tailInt(len), allValues) : this;
    }

    @Override
    public Series<T> select(IntSeries positions) {
        Series<Integer> newIntData = intData.select(positions);
        if (newIntData instanceof IntSeries) {
            return new EnumOverIntSeries<>(type, (IntSeries) newIntData, allValues);
        }

        // likely there were nulls, fallback to nullable object series
        int len = newIntData.size();
        T[] data = (T[]) new Enum[len];

        for (int i = 0; i < len; i++) {
            Integer v = newIntData.get(i);
            data[i] = v != null ? allValues[v] : null;
        }

        return new ArraySeries<>(data);
    }

    @Override
    public IntSeries index(ValuePredicate<T> predicate) {
        IntPredicate intPredicate = i -> predicate.test(get(i));
        return intData.indexInt(intPredicate);
    }

    @Override
    public BooleanSeries eq(Series<T> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);
        for (int i = 0; i < s; i++) {
            // enums can be compared directly
            bools.add(get(i) == another.get(i));
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

        BooleanMutableList bools = new BooleanMutableList(s);
        for (int i = 0; i < s; i++) {
            // enums can be compared directly
            bools.add(get(i) != another.get(i));
        }

        return bools.toBooleanSeries();
    }

    @Override
    public Series<T> replace(BooleanSeries condition, T with) {
        return with != null
                // TODO: implement "replaceInt" to avoid cast here
                ? new EnumOverIntSeries<>(type, (IntSeries) intData.replace(condition, with.ordinal()), allValues)
                : nullify(condition);
    }

    @Override
    public Series<T> replaceNoMatch(BooleanSeries condition, T with) {
        return with != null
                // TODO: implement "replaceNoMatchInt" to avoid cast here
                ? new EnumOverIntSeries<>(type, (IntSeries) intData.replaceNoMatch(condition, with.ordinal()), allValues)
                : nullifyNoMatch(condition);
    }

    @Override
    public Series<T> unique() {
        IntSeries unique = intData.uniqueInt();
        return unique.size() < size() ? new EnumOverIntSeries<>(type, unique, allValues) : this;
    }

    private Series<T> nullify(BooleanSeries condition) {

        // TODO: return "this" if condition is all false
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : get(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(get(i));
        }

        return vals.toSeries();
    }

    private Series<T> nullifyNoMatch(BooleanSeries condition) {
        // TODO: return "this" if condition is all true
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? get(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }
}
