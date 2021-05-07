package com.nhl.dflib;

import com.nhl.dflib.seriesexp.ExpSorter;
import com.nhl.dflib.seriesexp.RenamedExp;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;
import com.nhl.dflib.seriesexp.condition.UnarySeriesCondition;

import java.util.Objects;

/**
 * An expression that produces a Series out of a DataFrame, with Series size equal to the DataFrame height.
 * {@link SeriesExp} expressions are created using {@link Exp} static factory methods.
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
}
