package com.nhl.dflib;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.exp.ExpSorter;
import com.nhl.dflib.exp.RenamedExp;
import com.nhl.dflib.exp.SingleValueExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.CountExp;
import com.nhl.dflib.exp.agg.ExpAggregator;
import com.nhl.dflib.exp.condition.*;
import com.nhl.dflib.exp.filter.PreFilterFirstMatchExp;
import com.nhl.dflib.exp.filter.PreFilteredCountExp;
import com.nhl.dflib.exp.filter.PreFilteredExp;
import com.nhl.dflib.exp.func.ConcatFunction;
import com.nhl.dflib.exp.func.IfNullFunction;
import com.nhl.dflib.exp.num.DecimalColumn;
import com.nhl.dflib.exp.num.DoubleColumn;
import com.nhl.dflib.exp.num.IntColumn;
import com.nhl.dflib.exp.num.LongColumn;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A columnar expression that produces a Series out of either a DataFrame or a Series. Non-aggregating expressions
 * produce Series that are the same size as the source data structure, aggregating - a Series with fewer elements
 * (usually just one element).
 * <p>
 * Contains static factory methods to create various types of expressions. By convention expressions referencing
 * columns start with "$".
 *
 * @since 0.11
 */
public interface Exp<T> {

    /**
     * Returns an expression that evaluates to a Series containing a single value.
     */
    static <T> Exp<T> $val(T value) {

        // note that wrapping the value in primitive-optimized series has only very small effects on performance
        // (slightly improves comparisons with primitive series, and slows down comparisons with object-wrapped numbers).
        // So using the same "exp" for all values.

        // TODO: explore possible performance improvement by not converting scalars to Series at all, and providing a
        //   separate evaluation path instead.

        return new SingleValueExp(
                value,
                // TODO: in case the is called as "$val((T) null)", the type of the expression will not be the one the
                //  caller expects
                value != null ? value.getClass() : Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> Exp<T> $col(String name) {
        return new ColumnExp(name, Object.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> Exp<T> $col(int position) {
        return new ColumnExp(position, Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> Exp<T> $col(String name, Class<T> type) {
        return new ColumnExp<>(name, type);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> Exp<T> $col(int position, Class<T> type) {
        return new ColumnExp<>(position, type);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame String column.
     */
    static Exp<String> $str(String name) {
        return $col(name, String.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame String column at a given position.
     */
    static Exp<String> $str(int position) {
        return $col(position, String.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Integer column.
     */
    static NumericExp<Integer> $int(String name) {
        return new IntColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Integer column at a given position.
     */
    static NumericExp<Integer> $int(int position) {
        return new IntColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Long column.
     */
    static NumericExp<Long> $long(String name) {
        return new LongColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Long column at a given position.
     */
    static NumericExp<Long> $long(int position) {
        return new LongColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Double column.
     */
    static NumericExp<Double> $double(String name) {
        return new DoubleColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Double column at a given position.
     */
    static NumericExp<Double> $double(int position) {
        return new DoubleColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame BigDecimal column.
     */
    static NumericExp<BigDecimal> $decimal(String name) {
        return new DecimalColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame BigDecimal column at a given position.
     */
    static NumericExp<BigDecimal> $decimal(int position) {
        return new DecimalColumn(position);
    }

    // TODO: inconsistency - unlike numeric columns that support nulls, BooleanColumn is a "Condition",
    //  that can have no nulls, and will internally convert all nulls to "false"..
    //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
    static Condition $bool(String name) {
        return new BooleanColumn(name);
    }

    static Condition $bool(int position) {
        return new BooleanColumn(position);
    }

    static Condition or(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new OrCondition(conditions);
    }

    static Condition and(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new AndCondition(conditions);
    }

    /**
     * A function that evaluates "exp", replacing any null values by calling "ifNullExp".
     */
    static <T> Exp<T> ifNull(Exp<T> exp, Exp<T> ifNullExp) {
        return new IfNullFunction<>(exp, ifNullExp);
    }

    /**
     * A function that evaluates "exp", replacing any null values with "ifNull" value.
     */
    static <T> Exp<T> ifNull(Exp<T> exp, T ifNull) {
        return new IfNullFunction<>(exp, $val(ifNull));
    }

    /**
     * A function that does String concatenation of its arguments. Arguments can be any mix of constant values and
     * expressions.
     */
    static Exp<String> concat(Object... valuesOrExps) {
        return ConcatFunction.forObjects(valuesOrExps);
    }

    /**
     * Aggregating function that returns a single-value Series with the count of rows in the input.
     */
    static Exp<Integer> count() {
        return CountExp.getInstance();
    }

    static Exp<Integer> count(Condition filter) {
        return new PreFilteredCountExp(filter);
    }


    /**
     * Returns the type of the result Series.
     */
    Class<T> getType();

    /**
     * Returns the name of the column produced by this expression. Unlike {@link #getName(DataFrame)}, this form is
     * "context-less" and is used for Series.
     *
     * @see #as(String)
     */
    String getName();

    /**
     * Returns the name of the result Series in a context of the DataFrame argument. The name can be used to add the
     * result as a column to a DataFrame.
     *
     * @param df a DataFrame to use for column name lookup. Usually the same DataFrame as the one passed to {@link #eval(DataFrame)}
     * @see #as(String)
     */
    String getName(DataFrame df);

    /**
     * Evaluates expression against the DataFrame argument, returning a Series result.
     */
    Series<T> eval(DataFrame df);

    /**
     * Evaluates expression against the Series argument, returning a Series result.
     */
    Series<T> eval(Series<?> s);

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
    default Exp<T> as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new RenamedExp<>(name, this);
    }

    default Condition eq(Exp<?> exp) {
        return new BinaryCondition<>("eq", this, exp, Series::eq);
    }

    default Condition ne(Exp<?> exp) {
        return new BinaryCondition<>("ne", this, exp, Series::ne);
    }

    default Condition eq(Object value) {
        return value != null
                ? new BinaryCondition<>("eq", this, Exp.$val(value), Series::eq)
                : isNull();
    }

    default Condition ne(Object value) {
        return value != null
                ? new BinaryCondition<>("ne", this, Exp.$val(value), Series::ne)
                : isNotNull();
    }

    default Condition isNull() {
        return new UnaryCondition<>("isNull", this, Series::isNull);
    }

    default Condition isNotNull() {
        return new UnaryCondition<>("isNotNull", this, Series::isNotNull);
    }

    default <A> Exp<A> agg(Function<Series<T>, A> aggregator) {
        return new ExpAggregator<>(this, aggregator);
    }

    default <A> Exp<A> agg(Condition filter, Function<Series<T>, A> aggregator) {
        return new PreFilteredExp<>(filter, agg(aggregator));
    }

    default Exp<T> first() {
        return agg(AggregatorFunctions.first());
    }

    default Exp<T> first(Condition filter) {
        // special handling of "first" that avoids full condition eval
        return new PreFilterFirstMatchExp<>(filter, first());
    }

    /**
     * Aggregating operation that returns a single-value Series with a String of concatenated values separated by
     * the delimiter.
     */
    default Exp<String> vConcat(String delimiter) {
        return agg(AggregatorFunctions.concat(delimiter));
    }

    default Exp<String> vConcat(Condition filter, String delimiter) {
        return agg(filter, AggregatorFunctions.concat(delimiter));
    }

    /**
     * Aggregating operation that returns a single-value Series with a String of concatenated values separated by the
     * delimiter, preceded by the prefix and followed by the suffix.
     */
    default Exp<String> vConcat(String delimiter, String prefix, String suffix) {
        return agg(AggregatorFunctions.concat(delimiter, prefix, suffix));
    }

    default Exp<String> vConcat(Condition filter, String delimiter, String prefix, String suffix) {
        return agg(filter, AggregatorFunctions.concat(delimiter, prefix, suffix));
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single Set.
     */
    default Exp<Set<T>> set() {
        return agg(Series::toSet);
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single List.
     */
    default Exp<List<T>> list() {
        return agg(Series::toList);
    }

    /**
     * Aggregating operation that returns a single-value Series with all the values gathered into a single List.
     */
    default Exp<T[]> array(T[] template) {
        return agg(s -> s.toArray(template));
    }
}
