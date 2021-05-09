package com.nhl.dflib;

import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.seriesexp.SingleValueSeriesExp;
import com.nhl.dflib.seriesexp.agg.CountExp;
import com.nhl.dflib.seriesexp.condition.AndSeriesCondition;
import com.nhl.dflib.seriesexp.condition.BooleanColumn;
import com.nhl.dflib.seriesexp.condition.OrSeriesCondition;
import com.nhl.dflib.seriesexp.filter.PreFilteredCountExp;
import com.nhl.dflib.seriesexp.func.ConcatFunction;
import com.nhl.dflib.seriesexp.func.IfNullFunction;
import com.nhl.dflib.seriesexp.num.DecimalColumn;
import com.nhl.dflib.seriesexp.num.DoubleColumn;
import com.nhl.dflib.seriesexp.num.IntColumn;
import com.nhl.dflib.seriesexp.num.LongColumn;

import java.math.BigDecimal;

/**
 * Contains static factory methods to create various types of expressions. By convention expressions
 * referencing columns in a DataFrame start with "$".
 *
 * @since 0.11
 */
public interface Exp {

    /**
     * Returns an expression that evaluates to a Series containing a single value.
     */
    static <T> SeriesExp<T> $val(T value) {

        // note that wrapping the value in primitive-optimized series has only very small effects on performance
        // (slightly improves comparisons with primitive series, and slows down comparisons with object-wrapped numbers).
        // So using the same "exp" for all values.

        // TODO: explore possible performance improvement by not converting scalars to Series at all, and providing a
        //   separate evaluation path instead.

        return new SingleValueSeriesExp(
                value,
                // TODO: in case the is called as "$val((T) null)", the type of the expression will not be the one the
                //  caller expects
                value != null ? value.getClass() : Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> SeriesExp<T> $col(String name) {
        return new ColumnExp(name, Object.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> SeriesExp<T> $col(int position) {
        return new ColumnExp(position, Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> SeriesExp<T> $col(String name, Class<T> type) {
        return new ColumnExp<>(name, type);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> SeriesExp<T> $col(int position, Class<T> type) {
        return new ColumnExp<>(position, type);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame String column.
     */
    static SeriesExp<String> $str(String name) {
        return $col(name, String.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame String column at a given position.
     */
    static SeriesExp<String> $str(int position) {
        return $col(position, String.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Integer column.
     */
    static NumericSeriesExp<Integer> $int(String name) {
        return new IntColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Integer column at a given position.
     */
    static NumericSeriesExp<Integer> $int(int position) {
        return new IntColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Long column.
     */
    static NumericSeriesExp<Long> $long(String name) {
        return new LongColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Long column at a given position.
     */
    static NumericSeriesExp<Long> $long(int position) {
        return new LongColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Double column.
     */
    static NumericSeriesExp<Double> $double(String name) {
        return new DoubleColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Double column at a given position.
     */
    static NumericSeriesExp<Double> $double(int position) {
        return new DoubleColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame BigDecimal column.
     */
    static NumericSeriesExp<BigDecimal> $decimal(String name) {
        return new DecimalColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame BigDecimal column at a given position.
     */
    static NumericSeriesExp<BigDecimal> $decimal(int position) {
        return new DecimalColumn(position);
    }

    // TODO: inconsistency - unlike numeric columns that support nulls, BooleanColumn is a "Condition",
    //  that can have no nulls, and will internally convert all nulls to "false"..
    //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
    static SeriesCondition $bool(String name) {
        return new BooleanColumn(name);
    }

    static SeriesCondition $bool(int position) {
        return new BooleanColumn(position);
    }

    static SeriesCondition or(SeriesCondition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new OrSeriesCondition(conditions);
    }

    static SeriesCondition and(SeriesCondition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new AndSeriesCondition(conditions);
    }

    /**
     * A function that evaluates "exp", replacing any null values by calling "ifNullExp".
     */
    static <T> SeriesExp<T> ifNull(SeriesExp<T> exp, SeriesExp<T> ifNullExp) {
        return new IfNullFunction<>(exp, ifNullExp);
    }

    /**
     * A function that evaluates "exp", replacing any null values with "ifNull" value.
     */
    static <T> SeriesExp<T> ifNull(SeriesExp<T> exp, T ifNull) {
        return new IfNullFunction<>(exp, $val(ifNull));
    }

    /**
     * A function that does String concatenation of its arguments. Arguments can be any mix of constant values and
     * expressions.
     */
    static SeriesExp<String> concat(Object... valuesOrExps) {
        return ConcatFunction.forObjects(valuesOrExps);
    }

    /**
     * Aggregating function that returns a single-value Series with the count of rows in the input.
     */
    static SeriesExp<Integer> count() {
        return CountExp.getInstance();
    }

    static SeriesExp<Integer> count(SeriesCondition filter) {
        return new PreFilteredCountExp(filter);
    }
}
