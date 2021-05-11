package com.nhl.dflib;

import com.nhl.dflib.seriesexp.ExpSorter;
import com.nhl.dflib.seriesexp.RenamedExp;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;
import com.nhl.dflib.seriesexp.filter.PreFilterFirstMatchSeriesExp;
import com.nhl.dflib.seriesexp.filter.PreFilteredSeriesExp;
import com.nhl.dflib.seriesexp.agg.SeriesExpAggregator;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;
import com.nhl.dflib.seriesexp.condition.UnarySeriesCondition;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * An expression that produces a Series out of either a DataFrame or a Series. {@link SeriesExp} expressions are
 * created using {@link Exp} static factory methods. Non-aggregating expressions produce Series that are the same size
 * as the source data structure, aggregating - a Series with fewer elements (usually just one element).
 *
 * @see Exp
 * @since 0.11
 */
public interface SeriesExp<T> {

    /**
     * Returns the name of the result Series. The name can be used to add the result as a column to a DataFrame.
     *
     * @see #as(String)
     */
    String getName(DataFrame df);

    /**
     * Returns the type of the result Series.
     */
    Class<T> getType();

    /**
     * Evaluates expression against a DataFrame argument, returning a Series.
     */
    Series<T> eval(DataFrame df);

    /**
     * Returns a sorter that will use this expression for an ascending sort.
     */
    default Sorter asc() {
        return new ExpSorter(this, true);
    }

    /**
     * Returns a sorter that will use this expression for an descending sort.
     */
    default Sorter desc() {
        return new ExpSorter(this, false);
    }

    /**
     * Creates a copy of this expression with assigned name.
     */
    default SeriesExp<T> as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new RenamedExp<>(name, this);
    }

    default SeriesCondition eq(SeriesExp<?> exp) {
        return new BinarySeriesCondition<>("eq", this, exp, Series::eq);
    }

    default SeriesCondition ne(SeriesExp<?> exp) {
        return new BinarySeriesCondition<>("ne", this, exp, Series::ne);
    }

    default SeriesCondition eq(Object value) {
        return value != null
                ? new BinarySeriesCondition<>("eq", this, Exp.$val(value), Series::eq)
                : isNull();
    }

    default SeriesCondition ne(Object value) {
        return value != null
                ? new BinarySeriesCondition<>("ne", this, Exp.$val(value), Series::ne)
                : isNotNull();
    }

    default SeriesCondition isNull() {
        return new UnarySeriesCondition<>("isNull", this, Series::isNull);
    }

    default SeriesCondition isNotNull() {
        return new UnarySeriesCondition<>("isNotNull", this, Series::isNotNull);
    }

    default <A> SeriesExp<A> agg(Function<Series<T>, A> aggregator) {
        return new SeriesExpAggregator<>(this, aggregator);
    }

    default <A> SeriesExp<A> agg(SeriesCondition filter, Function<Series<T>, A> aggregator) {
        return new PreFilteredSeriesExp<>(filter, agg(aggregator));
    }

    default SeriesExp<T> first() {
        return agg(AggregatorFunctions.first());
    }

    default SeriesExp<T> first(SeriesCondition filter) {
        // special handling of "first" that avoids full condition eval
        return new PreFilterFirstMatchSeriesExp<>(filter, first());
    }

    /**
     * Aggregating operation that returns a single-value Series with a String of concatenated values separated by
     * the delimiter.
     */
    default SeriesExp<String> vConcat(String delimiter) {
        return agg(AggregatorFunctions.concat(delimiter));
    }

    default SeriesExp<String> vConcat(SeriesCondition filter, String delimiter) {
        return agg(filter, AggregatorFunctions.concat(delimiter));
    }

    /**
     * Aggregating operation that returns a single-value Series with a String of concatenated values separated by the
     * delimiter, preceded by the prefix and followed by the suffix.
     */
    default SeriesExp<String> vConcat(String delimiter, String prefix, String suffix) {
        return agg(AggregatorFunctions.concat(delimiter, prefix, suffix));
    }

    default SeriesExp<String> vConcat(SeriesCondition filter, String delimiter, String prefix, String suffix) {
        return agg(filter, AggregatorFunctions.concat(delimiter, prefix, suffix));
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single Set.
     */
    default SeriesExp<Set<T>> set() {
        return agg(Series::toSet);
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single List.
     */
    default SeriesExp<List<T>> list() {
        return agg(Series::toList);
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single List.
     */
    default SeriesExp<T[]> array(T[] template) {
        return agg(s -> s.toArray(template));
    }
}
