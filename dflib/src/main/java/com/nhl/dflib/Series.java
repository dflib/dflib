package com.nhl.dflib;

import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.IntArraySeries;

/**
 * A wrapper around an array of values of a certain type.
 *
 * @param <T>
 */
public interface Series<T> {

    static <T> Series<T> forData(T... data) {
        return new ArraySeries<>(data);
    }

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * Returns a {@link Series} that contains a range of data from this series. If the "toExclusive" parameter
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @since 0.6
     */
    Series<T> rangeOpenClosed(int fromInclusive, int toExclusive);

    /**
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     * @deprecated since 0.6 in favor for {@link #rangeOpenClosed(int, int)} for consistency.
     */
    @Deprecated
    default Series<T> openClosedRange(int fromInclusive, int toExclusive) {
        return rangeOpenClosed(fromInclusive, toExclusive);
    }

    // TODO: alternative names instead of "materialize" :
    //  * "compact" - as we often trim unused data
    //  * "optimize" - which may be anything, depending on the Series structure.. Ambiguos - optimize for storage or access?
    Series<T> materialize();

    Series<T> fillNulls(T value);

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
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries index(ValuePredicate<T> predicate);

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
}
