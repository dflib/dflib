package com.nhl.dflib;

import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.series.*;
import com.nhl.dflib.sort.SeriesSorter;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * A wrapper around an array of values of a certain type.
 *
 * @param <T>
 */
public interface Series<T> extends Iterable<T> {

    @SafeVarargs
    static <T> Series<T> forData(T... data) {
        return data != null && data.length > 0 ? new ArraySeries<>(data) : new EmptySeries<>();
    }

    /**
     * @since 0.7
     */
    static <T> Series<T> forData(Iterable<T> data) {
        if (data instanceof List) {
            return new ListSeries<>((List<T>) data);
        }

        List<T> list = new ArrayList<>();
        for (T t : data) {
            list.add(t);
        }
        return list.size() > 0 ? new ListSeries<>(list) : new EmptySeries<>();
    }

    /**
     * @deprecated since 0.8 in favor of {@link #getNominalType()}.
     */
    @Deprecated
    default Class<?> getType() {
        return getNominalType();
    }

    /**
     * Returns a "nominal" type of elements this Series object. Since most Series do not carry the type around, this
     * may be the "Object.class" in all cases except for primitive Series. Use a more expensive
     * {@link #getInferredType()} to check the real type of series values.
     *
     * @return the nominal type of values in the series
     * @since 0.6
     */
    Class<?> getNominalType();

    /**
     * Returns the most specific common superclass of the values in this Series object. If all values are null, returns
     * Object.class. First invocation of this method may be quite slow in many cases, as it entails a full scan of the
     * Series values.
     *
     * @return the most specific common superclass of the values in this Series object.
     * @since 0.8
     */
    Class<?> getInferredType();

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * @param mapper a function that maps each Series value to some other value
     * @param <V>    value type produced by the mapper
     * @return a Series produced by applying a mapper to this Series
     * @since 0.6
     */
    <V> Series<V> map(ValueMapper<T, V> mapper);

    /**
     * A map function over the Series that generates a DataFrame of the same height as the length of the Series,
     * which each row produced by applying the map function to each Series value.
     *
     * @return a new DataFrame built from Series values.
     * @since 0.7
     */
    DataFrame map(Index resultColumns, ValueToRowMapper<T> mapper);

    /**
     * Returns a {@link Series} that contains a range of data from this series. If the "toExclusive" parameter
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @since 0.6
     */
    Series<T> rangeOpenClosed(int fromInclusive, int toExclusive);

    // TODO: alternative names instead of "materialize" :
    //  * "compact" - as we often trim unused data
    //  * "optimize" - which may be anything, depending on the Series structure.. Ambiguous - optimize for storage or access?
    Series<T> materialize();

    Series<T> fillNulls(T value);

    /**
     * @param values a Series to take null replacement values from
     * @return a new Series with nulls replaced with values from another Series with matching positions
     * @since 0.6
     */
    Series<T> fillNullsFromSeries(Series<? extends T> values);

    Series<T> fillNullsBackwards();

    Series<T> fillNullsForward();

    Series<T> concat(Series<? extends T>... other);

    Series<T> head(int len);

    Series<T> tail(int len);

    default Series<T> select(int... positions) {
        return select(new IntArraySeries(positions));
    }

    Series<T> select(IntSeries positions);

    /**
     * @since 0.11
     */
    Series<T> select(ValuePredicate<T> p);

    /**
     * @since 0.11
     */
    Series<T> select(BooleanSeries positions);

    /**
     * @since 0.6
     * @deprecated since 0.11 in favor of {@link #select(ValuePredicate)}
     */
    @Deprecated
    default Series<T> filter(ValuePredicate<T> p) {
        return select(p);
    }

    /**
     * @since 0.6
     * @deprecated since 0.11 in favor of {@link #select(BooleanSeries)}
     */
    @Deprecated
    default Series<T> filter(BooleanSeries positions) {
        return select(positions);
    }

    // TODO: can't have "select(boolean...)" as it conflicts with "select(int...)". Should we change to "select(int, int...)" ?

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries index(ValuePredicate<T> predicate);

    /**
     * Returns a sorted copy of this Series using provided Comparator.
     *
     * @return sorted copy of this series.
     * @since 0.6
     */
    Series<T> sort(Comparator<? super T> comparator);

    /**
     * Calculates and returns an IntSeries representing element indices from the original Series in the order dictated
     * by the comparator. This operation is useful when we want to sort another Series based on the ordering of this
     * Series.
     * <p>Note that calling "sortIndexInt" on the result of this method produces a Series of sort-aware row numbers.</p>
     *
     * @param comparator value comparator for sorting¬
     * @return an IntSeries representing element indices from the original Series
     * @since 0.8
     */
    default IntSeries sortIndex(Comparator<? super T> comparator) {
        return SeriesSorter.sortedPositions(this, comparator);
    }

    /**
     * @param another a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     * @since 0.6
     */
    BooleanSeries eq(Series<?> another);

    /**
     * @param another a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     * @since 0.6
     */
    BooleanSeries ne(Series<?> another);

    /**
     * @since 0.11
     */
    BooleanSeries isNull();

    /**
     * @since 0.11
     */
    BooleanSeries isNotNull();

    /**
     * Returns a boolean series indicating whether each original Series position matched the predicate
     *
     * @param predicate match condition
     * @return a BooleanSeries with true/false elements corresponding whether a given position in "this" Series matched
     * the predicate.
     * @since 0.6
     */
    BooleanSeries locate(ValuePredicate<T> predicate);

    /**
     * @param condition a BooleanSeries that determines which cells need to be replaced.
     * @param with      a value to replace matching cells with
     * @return a new series with replaced values
     * @since 0.6
     */
    Series<T> replace(BooleanSeries condition, T with);

    /**
     * @param condition a BooleanSeries that determines which cells need not be replaced.
     * @param with      a value to replace non-matching cells with
     * @return a new series with replaced values
     * @since 0.6
     */
    Series<T> replaceNoMatch(BooleanSeries condition, T with);

    /**
     * @return a Series that contains non-repeating values from this Series.
     * @since 0.6
     */
    Series<T> unique();

    /**
     * Returns a 2-column DataFrame that provides counts of distinct values present in the Series. Null values are
     * not included in the counts.
     *
     * @since 0.6
     */
    DataFrame valueCounts();

    /**
     * Groups Series by its values.
     *
     * @since 0.6
     */
    SeriesGroupBy<T> group();

    /**
     * Groups Series, using the provided function to calculate grouping key.
     *
     * @since 0.6
     */
    SeriesGroupBy<T> group(ValueMapper<T, ?> by);

    /**
     * Returns a scalar value that is a result of applying provided aggregator to Series.
     *
     * @since 0.6
     */
    default <R> R agg(SeriesAggregator<? super T, R> aggregator) {
        return aggregator.aggregate(this);
    }

    /**
     * Returns a Series, with each value corresponding to the result of aggregation with each aggregator out of the
     * provided array of aggregators.
     *
     * @since 0.6
     */
    default Series<?> aggMultiple(SeriesAggregator<? super T, ?>... aggregators) {

        int len = aggregators.length;
        ObjectAccumulator<Object> accum = new ObjectAccumulator<>(len);

        for (SeriesAggregator<? super T, ?> aggregator : aggregators) {
            accum.add(aggregator.aggregate(this));
        }

        return accum.toSeries();
    }

    /**
     * @since 0.7
     */
    default T first() {
        return agg(SeriesAggregator.first());
    }

    /**
     * @since 0.7
     */
    default String concat(String delimiter) {
        // TODO: implement concat directly instead of going through aggregator
        return agg(SeriesAggregator.concat(delimiter));
    }

    /**
     * @since 0.7
     */
    default String concat(String delimiter, String prefix, String suffix) {
        // TODO: implement concat directly instead of going through aggregator
        return agg(SeriesAggregator.concat(delimiter, prefix, suffix));
    }

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size. If you are
     * doing sampling in a high concurrency application, consider using {@link #sample(int, Random)}, as this method
     * is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the size of this Series.
     * @return a Series object that is a sample of values from this object
     * @since 0.7
     */
    Series<T> sample(int size);

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the size of this Series.
     * @param random a custom random number generator
     * @return a Series object that is a sample of values from this object
     * @since 0.7
     */
    Series<T> sample(int size, Random random);

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with nulls.
     *
     * @since 0.9
     */
    default Series<T> shift(int offset) {
        return shift(offset, null);
    }

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with the provided filler value.
     *
     * @since 0.9
     */
    default Series<T> shift(int offset, T filler) {
        if (offset > 0) {
            return new OffsetLeadSeries<>(this, offset, filler);
        } else if (offset < 0) {
            return new OffsetLagSeries<>(this, offset, filler);
        } else {
            return this;
        }
    }

    /**
     * @since 0.7
     */
    @Override
    default Iterator<T> iterator() {

        return new Iterator<T>() {

            final int len = Series.this.size();
            int i;

            @Override
            public boolean hasNext() {
                return i < len;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator: " + i);
                }

                return Series.this.get(i++);
            }
        };
    }

    /**
     * Returns a List with this Series values. Returned list is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new List with data from this Series
     * @since 0.7
     */
    default List<T> toList() {
        int len = size();
        Object[] copy = new Object[len];
        copyTo(copy, 0, 0, len);
        return asList((T[]) copy);
    }

    /**
     * Returns a Set with this Series values. Returned set is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new Set with data from this Series
     * @since 0.7
     */
    default Set<T> toSet() {
        int len = size();

        Set<T> set = new HashSet<>();
        for (int i = 0; i < len; i++) {
            set.add(get(i));
        }

        return set;
    }

    /**
     * Returns an array with this Series values. Returned array contains a copy of this Series data, so modifying it
     * will not affect the underlying Series.
     *
     * @param a the array into which the elements of this collection are to be stored, if it is big enough; otherwise,
     *          a new array of the same  runtime type is allocated for this purpose. This is similar to
     *          {@link java.util.Collection#toArray(Object[])} behavior.
     * @return a new array with data from this Series
     * @since 0.7
     */
    default T[] toArray(T[] a) {
        int len = size();

        // TODO: wish we could use Series.getType(), but its default implementation returns Object.class, and is useless
        //  here, so we need to rely on the passed array prototype
        T[] copy = a.length < len
                ? (T[]) Array.newInstance(a.getClass().getComponentType(), len)
                : a;

        copyTo(copy, 0, 0, len);
        return copy;
    }
}
