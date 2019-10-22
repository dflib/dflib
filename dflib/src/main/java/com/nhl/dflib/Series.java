package com.nhl.dflib;

import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.EmptySeries;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.series.ListSeries;
import com.nhl.dflib.series.builder.ObjectAccumulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A wrapper around an array of values of a certain type.
 *
 * @param <T>
 */
public interface Series<T> extends Iterable<T> {

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
     * @return the type of values in the series.
     * @since 0.6
     */
    // TODO: change type from ? to a specific T, but estimate the impact of carrying an explicit type around with every
    //  series object that is a subclass of ObjectSeries
    Class<?> getType();

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * @param mapper
     * @param <V>
     * @return a Series produced by applying a mapper to this Series
     * @since 0.6
     */
    <V> Series<V> map(ValueMapper<T, V> mapper);

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
    //  * "optimize" - which may be anything, depending on the Series structure.. Ambiguos - optimize for storage or access?
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
     * @since 0.6
     */
    Series<T> filter(ValuePredicate<T> p);

    /**
     * @since 0.6
     */
    Series<T> filter(BooleanSeries positions);

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
     * @param another a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparision of this Series with
     * another.
     * @since 0.6
     */
    BooleanSeries eq(Series<T> another);

    /**
     * @param another a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparision of this Series with
     * another.
     * @since 0.6
     */
    BooleanSeries ne(Series<T> another);

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

        for (int i = 0; i < len; i++) {
            accum.add(aggregators[i].aggregate(this));
        }

        return accum.toSeries();
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
}
