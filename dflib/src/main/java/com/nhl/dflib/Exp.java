package com.nhl.dflib;

import com.nhl.dflib.exp.AsExp;
import com.nhl.dflib.exp.Column;
import com.nhl.dflib.exp.ConstExp;
import com.nhl.dflib.exp.agg.CountExp;
import com.nhl.dflib.exp.agg.ExpAggregator;
import com.nhl.dflib.exp.agg.StringAggregators;
import com.nhl.dflib.exp.bool.AndCondition;
import com.nhl.dflib.exp.bool.BoolColumn;
import com.nhl.dflib.exp.bool.ConditionFactory;
import com.nhl.dflib.exp.bool.OrCondition;
import com.nhl.dflib.exp.datetime.DateColumn;
import com.nhl.dflib.exp.datetime.DateConstExp;
import com.nhl.dflib.exp.datetime.DateExp1;
import com.nhl.dflib.exp.datetime.DateTimeColumn;
import com.nhl.dflib.exp.datetime.DateTimeConstExp;
import com.nhl.dflib.exp.datetime.DateTimeExp1;
import com.nhl.dflib.exp.datetime.TimeColumn;
import com.nhl.dflib.exp.datetime.TimeConstExp;
import com.nhl.dflib.exp.datetime.TimeExp1;
import com.nhl.dflib.exp.filter.PreFilterFirstMatchExp;
import com.nhl.dflib.exp.filter.PreFilteredCountExp;
import com.nhl.dflib.exp.filter.PreFilteredExp;
import com.nhl.dflib.exp.flow.IfExp;
import com.nhl.dflib.exp.flow.IfNullExp;
import com.nhl.dflib.exp.map.MapCondition1;
import com.nhl.dflib.exp.map.MapCondition2;
import com.nhl.dflib.exp.map.MapExp1;
import com.nhl.dflib.exp.map.MapExp2;
import com.nhl.dflib.exp.num.DecimalColumn;
import com.nhl.dflib.exp.num.DoubleColumn;
import com.nhl.dflib.exp.num.IntColumn;
import com.nhl.dflib.exp.num.LongColumn;
import com.nhl.dflib.exp.sort.ExpSorter;
import com.nhl.dflib.exp.str.ConcatExp;
import com.nhl.dflib.exp.str.StrColumn;
import com.nhl.dflib.exp.str.StrExp1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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

        // TODO: in case this is called as "$val((T) null)", the type of the expression will not be the one the
        //  caller expects

        Class type = value != null ? value.getClass() : Object.class;
        return $val(value, type);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single LocalDate value.
     *
     * @since 0.16
     */
    static DateExp $dateVal(LocalDate value) {
        return new DateConstExp(value);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single LocalTime value.
     *
     * @since 0.16
     */
    static TimeExp $timeVal(LocalTime value) {
        return new TimeConstExp(value);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single LocalDateTime value.
     *
     * @since 0.16
     */
    static DateTimeExp $dateTimeVal(LocalDateTime value) {
        return new DateTimeConstExp(value);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single value.
     */
    static <T, V extends T> Exp<T> $val(V value, Class<T> type) {

        // note that wrapping the value in primitive-optimized series has only very small effects on performance
        // (slightly improves comparisons with primitive series, and slows down comparisons with object-wrapped numbers).
        // So using the same "exp" for all values.

        return new ConstExp<>(value, type);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> Exp<T> $col(String name) {
        return new Column(name, Object.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> Exp<T> $col(int position) {
        return new Column(position, Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame column.
     */
    static <T> Exp<T> $col(String name, Class<T> type) {
        return new Column<>(name, type);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static <T> Exp<T> $col(int position, Class<T> type) {
        return new Column<>(position, type);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame String column.
     */
    static StrExp $str(String name) {
        return new StrColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame String column at a given position.
     */
    static StrExp $str(int position) {
        return new StrColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Integer column.
     */
    static NumExp<Integer> $int(String name) {
        return new IntColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Integer column at a given position.
     */
    static NumExp<Integer> $int(int position) {
        return new IntColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Long column.
     */
    static NumExp<Long> $long(String name) {
        return new LongColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Long column at a given position.
     */
    static NumExp<Long> $long(int position) {
        return new LongColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame Double column.
     */
    static NumExp<Double> $double(String name) {
        return new DoubleColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame Double column at a given position.
     */
    static NumExp<Double> $double(int position) {
        return new DoubleColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame BigDecimal column.
     */
    static DecimalExp $decimal(String name) {
        return new DecimalColumn(name);
    }

    /**
     * Returns an expression that evaluates to a BigDecimal column at a given position.
     */
    static DecimalExp $decimal(int position) {
        return new DecimalColumn(position);
    }

    // TODO: inconsistency - unlike numeric columns that support nulls, BooleanColumn is a "Condition",
    //  that can have no nulls, and will internally convert all nulls to "false"..
    //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
    static Condition $bool(String name) {
        return new BoolColumn(name);
    }

    static Condition $bool(int position) {
        return new BoolColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named date column.
     */
    static DateExp $date(String name) {
        return new DateColumn(name);
    }

    /**
     * Returns an expression that evaluates to a date column at a given position.
     */
    static DateExp $date(int position) {
        return new DateColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named time column.
     */
    static TimeExp $time(String name) {
        return new TimeColumn(name);
    }

    /**
     * Returns an expression that evaluates to a time column at a given position.
     */
    static TimeExp $time(int position) {
        return new TimeColumn(position);
    }


    /**
     * Returns an expression that evaluates to a named date column.
     *
     * @since 0.16
     */
    static DateTimeExp $dateTime(String name) {
        return new DateTimeColumn(name);
    }

    /**
     * Returns an expression that evaluates to a date column at a given position.
     *
     * @since 0.16
     */
    static DateTimeExp $dateTime(int position) {
        return new DateTimeColumn(position);
    }


    static Condition or(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new OrCondition(conditions);
    }

    static Condition and(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new AndCondition(conditions);
    }

    static Condition not(Condition condition) {
        return condition.not();
    }

    /**
     * A "control flow" function that evaluates condition, and executes either "ifTrue" or "ifFalse", depending
     * on the condition value. Evaluation is done per row.
     */
    static <T> Exp<T> ifExp(Condition condition, Exp<T> ifTrue, Exp<T> ifFalse) {
        return new IfExp<>(condition, ifTrue, ifFalse);
    }

    /**
     * A function that evaluates "exp", replacing any null values by calling "ifNullExp".
     */
    static <T> Exp<T> ifNull(Exp<T> exp, Exp<T> ifNullExp) {
        return new IfNullExp<>(exp, ifNullExp);
    }

    /**
     * A function that evaluates "exp", replacing any null values with "ifNull" value.
     */
    static <T> Exp<T> ifNull(Exp<T> exp, T ifNull) {
        return new IfNullExp<>(exp, $val(ifNull));
    }

    /**
     * A function that does String concatenation of its arguments. Arguments can be any mix of constant values and
     * expressions.
     */
    static StrExp concat(Object... valuesOrExps) {
        return ConcatExp.forObjects(valuesOrExps);
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
     * Returns the type of the evaluation result. The type is used internally by the DBLib expression engine to compile
     * the most optimal evaluation path.
     *
     * <p> A note on DFLib schema "fuzziness": Callers can not always predict the data schema upfront, and
     * oftentimes Java generics limitations prevent DFLib from using the right type even when it is known to the
     * caller. So many expressions will return <code>Object.class</code> instead of a more specific type. In this case the
     * expression should still evaluate properly, but possibly suboptimally.
     */
    Class<T> getType();

    /**
     * Returns a String label that should be used as the DataFrame column name for columns produced by this expression.
     * The name can be changed by calling {@link #as(String)}.
     *
     * @see #as(String)
     */
    default String getColumnName() {
        // most expressions don't have an explicit notion of column name, so by default they would produce a QL
        // String that can be used for naming
        return toQL();
    }

    /**
     * Returns a String label that should be used as the DataFrame column name for columns produced by this expression.
     * The name is resolved in a context of the DataFrame argument, allowing "positional" column references to be
     * resolved to String names. The name can be changed by calling
     * {@link #as(String)}.
     *
     * @param df a DataFrame to use for column name lookup. Usually the same DataFrame as the one passed to
     *           {@link #eval(DataFrame)}
     * @see #as(String)
     */
    default String getColumnName(DataFrame df) {
        // most expressions don't have an explicit notion of column name, so by default they would produce a QL
        // String that can be used for naming
        return toQL(df);
    }

    /**
     * Returns DFLib Query Language representation of this expression.
     */
    String toQL();

    /**
     * Returns DFLib Query Language representation of this expression, with column names resolved in the context of
     * the provided DataFrame.
     */
    String toQL(DataFrame df);

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
     * Returns a sorter that will use this expression for a descending sort.
     */
    default Sorter desc() {
        return new ExpSorter(this, false);
    }

    /**
     * Creates a copy of this expression with assigned name.
     */
    default Exp<T> as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new AsExp<>(name, this);
    }

    /**
     * Creates an expression that takes the result of this Exp and applies the provided Series transformation function.
     */
    default <R> Exp<R> map(Function<Series<T>, Series<R>> op) {
        // Exp type vagueness ALERT: this is one of those expressions where we can't infer the correct return type...
        return MapExp1.map("map", (Class<R>) Object.class, this, op);
    }

    /**
     * Creates an expression that takes the result of this Exp and applies the provided value transformation function
     * to each value. Note that DFLib will only pass non-null values to the "op" function. Any source "null" values
     * automatically evaluate to "null" result. This assumption makes writing transformation functions easier
     * (and closer to how SQL operates). If special handling of nulls is required, consider using
     * {@link #map(Function)} method instead.
     */
    default <R> Exp<R> mapVal(Function<T, R> op) {
        // Exp type vagueness ALERT: this is one of those expressions where we can't infer the correct return type...
        return MapExp1.mapVal("map", (Class<R>) Object.class, this, op);
    }

    /**
     * Creates an expression that takes the results of this and another Exp and applies the provided Series
     * transformation BiFunction.
     */
    default <S, R> Exp<R> map(Exp<S> other, BiFunction<Series<T>, Series<S>, Series<R>> op) {
        // Exp type vagueness ALERT: this is one of those expressions where we can't infer the correct return type...
        return MapExp2.map("map", (Class<R>) Object.class, this, other, op);
    }

    /**
     * Creates an expression that takes the results of this and another Exp and applies the provided value
     * transformation BiFunction. Note that DFLib will only pass non-null values to the "op" function. Any source
     * "null" values on either side of the expression automatically evaluate to "null" result. This assumption makes
     * writing transformation functions easier (and closer to how SQL operates). If special handling of nulls is
     * required, consider using {@link #map(Exp, BiFunction)} method instead.
     */
    default <S, R> Exp<R> mapVal(Exp<S> other, BiFunction<T, S, R> op) {
        // Exp type vagueness ALERT: this is one of those expressions where we can't infer the correct return type...
        return MapExp2.mapVal("map", (Class<R>) Object.class, this, other, op);
    }

    default Condition eq(Exp<?> exp) {
        return MapCondition2.map("=", this, exp, Series::eq);
    }

    default Condition ne(Exp<?> exp) {
        return MapCondition2.map("!=", this, exp, Series::ne);
    }

    default Condition eq(Object value) {
        return value != null
                ? eq(Exp.$val(value))
                : isNull();
    }

    default Condition ne(Object value) {
        return value != null
                ? ne(Exp.$val(value))
                : isNotNull();
    }

    /**
     * @since 0.18
     */
    default Condition in(Object... values) {
        return MapCondition1.map("in", this, s -> s.in(values));
    }

    /**
     * @since 0.18
     */
    default Condition notIn(Object... values) {
        return MapCondition1.map("in", this, s -> s.notIn(values));
    }

    default Condition isNull() {
        return ConditionFactory.isNull(this);
    }

    default Condition isNotNull() {
        return ConditionFactory.isNotNull(this);
    }

    /**
     * Creates an aggregating expression based on this Exp and a custom aggregation function.
     */
    default <A> Exp<A> agg(Function<Series<T>, A> aggregator) {
        return new ExpAggregator<>(this, aggregator);
    }

    /**
     * Creates an aggregating expression based on this Exp, a filter and a custom aggregation function.
     */
    default <A> Exp<A> agg(Condition filter, Function<Series<T>, A> aggregator) {
        return new PreFilteredExp<>(filter, agg(aggregator));
    }

    default Exp<T> first() {
        return agg(Series::first);
    }

    default Exp<T> last() {
        return agg(Series::last);
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
        return agg(StringAggregators.vConcat(delimiter));
    }

    default Exp<String> vConcat(Condition filter, String delimiter) {
        return agg(filter, StringAggregators.vConcat(delimiter));
    }

    /**
     * Aggregating operation that returns a single-value Series with a String of concatenated values separated by the
     * delimiter, preceded by the prefix and followed by the suffix.
     */
    default Exp<String> vConcat(String delimiter, String prefix, String suffix) {
        return agg(StringAggregators.vConcat(delimiter, prefix, suffix));
    }

    default Exp<String> vConcat(Condition filter, String delimiter, String prefix, String suffix) {
        return agg(filter, StringAggregators.vConcat(delimiter, prefix, suffix));
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

    /**
     * Converts this expression to a {@link Condition} that can be used for row filtering, etc.
     *
     * @since 0.14
     */
    default Condition castAsCondition() {
        return ConditionFactory.castAsCondition(this);
    }

    /**
     * @since 0.15
     */
    default Condition mapCondition(Function<Series<T>, BooleanSeries> op) {
        return MapCondition1.map("map", this, op);
    }

    /**
     * @since 0.15
     */
    default Condition mapConditionVal(Predicate<T> op) {
        return MapCondition1.mapVal("map", this, op);
    }

    /**
     * @since 0.16
     */
    default DateExp castAsDate() {
        return DateExp1.mapVal("castAsDate", this.castAsStr(), LocalDate::parse);
    }

    /**
     * @since 0.16
     */
    default DateExp castAsDate(String format) {
        return castAsDate(DateTimeFormatter.ofPattern(format));
    }

    /**
     * @since 0.16
     */
    default DateExp castAsDate(DateTimeFormatter formatter) {
        return DateExp1.mapVal("castAsDate", this.castAsStr(), s -> LocalDate.parse(s, formatter));
    }

    /**
     * @since 0.16
     */
    default TimeExp castAsTime() {
        return TimeExp1.mapVal("castAsTime", this.castAsStr(), LocalTime::parse);
    }

    /**
     * @since 0.16
     */
    default TimeExp castAsTime(String formatter) {
        return castAsTime(DateTimeFormatter.ofPattern(formatter));
    }

    /**
     * @since 0.16
     */
    default TimeExp castAsTime(DateTimeFormatter formatter) {
        return TimeExp1.mapVal("castAsTime", this.castAsStr(), s -> LocalTime.parse(s, formatter));
    }

    /**
     * @since 0.16
     */
    default DateTimeExp castAsDateTime() {
        return DateTimeExp1.mapVal("castAsDateTime", this.castAsStr(), LocalDateTime::parse);
    }

    /**
     * @since 0.16
     */
    default DateTimeExp castAsDateTime(String format) {
        return castAsDateTime(DateTimeFormatter.ofPattern(format));
    }

    /**
     * @since 0.16
     */
    default DateTimeExp castAsDateTime(DateTimeFormatter formatter) {
        return DateTimeExp1.mapVal("castAsDateTime", this.castAsStr(), s -> LocalDateTime.parse(s, formatter));
    }

    /**
     * @since 0.16
     */
    default StrExp castAsStr() {
        return StrExp1.mapVal("castAsStr", this, o -> o.toString());
    }

    /**
     * @since 0.16
     */
    default NumExp<Integer> castAsInt() {
        // Need to do multiple conversions, so that we can properly cast any number format.
        // Int expressions must override this method to return "this"
        return castAsStr().castAsDecimal().castAsInt();
    }

    /**
     * @since 0.16
     */
    default NumExp<Long> castAsLong() {
        // Need to do multiple conversions, so that we can properly cast any number format.
        // Long expressions must override this method to return "this"
        return castAsStr().castAsDecimal().castAsLong();
    }

    /**
     * @since 0.16
     */
    default NumExp<Double> castAsDouble() {
        return castAsStr().castAsDouble();
    }

    /**
     * @since 0.16
     */
    default DecimalExp castAsDecimal() {
        return castAsStr().castAsDecimal();
    }
}
