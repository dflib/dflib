package org.dflib;

import org.dflib.exp.AsExp;
import org.dflib.exp.Column;
import org.dflib.exp.RowNumExp;
import org.dflib.exp.ScalarExp;
import org.dflib.exp.ShiftExp;
import org.dflib.exp.agg.CountExp;
import org.dflib.exp.agg.FirstExp;
import org.dflib.exp.agg.LastExp;
import org.dflib.exp.agg.ReduceExp1;
import org.dflib.exp.agg.ReduceExp2;
import org.dflib.exp.agg.ReduceExpN;
import org.dflib.exp.agg.StringAggregators;
import org.dflib.exp.bool.AndCondition;
import org.dflib.exp.bool.BoolColumn;
import org.dflib.exp.bool.BoolScalarExp;
import org.dflib.exp.bool.ConditionFactory;
import org.dflib.exp.bool.OrCondition;
import org.dflib.exp.datetime.DateColumn;
import org.dflib.exp.datetime.DateExp1;
import org.dflib.exp.datetime.DateScalarExp;
import org.dflib.exp.datetime.DateTimeColumn;
import org.dflib.exp.datetime.DateTimeExp1;
import org.dflib.exp.datetime.DateTimeScalarExp;
import org.dflib.exp.datetime.OffsetDateTimeColumn;
import org.dflib.exp.datetime.OffsetDateTimeExp1;
import org.dflib.exp.datetime.OffsetDateTimeScalarExp;
import org.dflib.exp.datetime.TimeColumn;
import org.dflib.exp.datetime.TimeExp1;
import org.dflib.exp.datetime.TimeScalarExp;
import org.dflib.exp.flow.IfExp;
import org.dflib.exp.flow.IfNullExp;
import org.dflib.exp.map.MapCondition1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapExp1;
import org.dflib.exp.map.MapExp2;
import org.dflib.exp.num.BigintColumn;
import org.dflib.exp.num.BigintScalarExp;
import org.dflib.exp.num.DecimalColumn;
import org.dflib.exp.num.DecimalScalarExp;
import org.dflib.exp.num.DoubleColumn;
import org.dflib.exp.num.DoubleScalarExp;
import org.dflib.exp.num.FloatColumn;
import org.dflib.exp.num.FloatScalarExp;
import org.dflib.exp.num.IntColumn;
import org.dflib.exp.num.IntScalarExp;
import org.dflib.exp.num.LongColumn;
import org.dflib.exp.num.LongScalarExp;
import org.dflib.ql.QLParserInvoker;
import org.dflib.exp.sort.ExpSorter;
import org.dflib.exp.str.ConcatExp;
import org.dflib.exp.str.StrColumn;
import org.dflib.exp.str.StrExp1;
import org.dflib.exp.str.StrScalarExp;
import org.dflib.ql.antlr4.ExpParser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A columnar expression that operates either on a Series or a DataFrame.
 * <p>
 * Contains static factory methods to create various types of expressions. By convention, expressions referencing
 * columns start with a "$".
 * <p>
 * Expressions can be invoked in one of the two modes: "eval" or "reduce". The former is essentially a per-row
 * transformation producing a Series of the same size as its input Series or DataFrame. The latter is an aggregator,
 * producing a single value. Some expressions (e.g. "column" references) only work in an "eval" mode and would throw
 * when calling "reduce".
 */
public interface Exp<T> {

    /**
     * Returns an expression whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     */
    static <T> Exp<T> $val(T value) {

        // TODO: in case this is called as "$val((T) null)", the type of the expression will not be the one the
        //  caller expects

        Class type = value != null ? value.getClass() : Object.class;
        return $val(value, type);
    }

    /**
     * Returns a {@code NumExp<Integer>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<Integer> $intVal(int value) {
        return new IntScalarExp(value);
    }

    /**
     * Returns a {@code NumExp<Long>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<Long> $longVal(long value) {
        return new LongScalarExp(value);
    }

    /**
     * Returns a {@code NumExp<Float>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<Float> $floatVal(float value) {
        return new FloatScalarExp(value);
    }

    /**
     * Returns a {@code NumExp<Double>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<Double> $doubleVal(double value) {
        return new DoubleScalarExp(value);
    }

    /**
     * Returns a {@code NumExp<BigInteger>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<BigInteger> $bigintVal(BigInteger value) {
        return new BigintScalarExp(value);
    }

    /**
     * Returns a {@code NumExp<BigInteger>} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static NumExp<BigInteger> $bigintVal(String value) {
        return Exp.$bigintVal(value != null ? new BigInteger(value) : null);
    }

    /**
     * Returns a {@link DecimalExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static DecimalExp $decimalVal(BigDecimal value) {
        return new DecimalScalarExp(value);
    }

    /**
     * Returns a {@code DecimalExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static DecimalExp $decimalVal(String value) {
        return Exp.$decimalVal(value != null ? new BigDecimal(value) : null);
    }

    /**
     * Returns a {@code Condition} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static Condition $boolVal(boolean value) {
        return value ? BoolScalarExp.getTrue() : BoolScalarExp.getFalse();
    }

    /**
     * Returns a {@code StrExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     *
     * @since 2.0.0
     */
    static StrExp $strVal(String value) {
        return new StrScalarExp(value);
    }

    /**
     * Returns a {@code DateExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     */
    static DateExp $dateVal(LocalDate value) {
        return new DateScalarExp(value);
    }

    /**
     * Returns a {@code TimeExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     */
    static TimeExp $timeVal(LocalTime value) {
        return new TimeScalarExp(value);
    }

    /**
     * Returns a {@code DateTimeExp} whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself.
     */
    static DateTimeExp $dateTimeVal(LocalDateTime value) {
        return new DateTimeScalarExp(value);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single OffsetDateTime value.
     *
     * @since 1.1.0
     */
    static OffsetDateTimeExp $offsetDateTimeVal(OffsetDateTime value) {
        return new OffsetDateTimeScalarExp(value);
    }

    /**
     * Returns an expression that evaluates to a Series containing a single value.
     * Returns an expression whose "eval" returns a Series with the value argument at each position, and "reduce"
     * returns the value itself. Type argument allows DFLib to optimize the expression for a specific Java type.
     */
    static <T, V extends T> Exp<T> $val(V value, Class<T> type) {
        return new ScalarExp<>(value, type);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static <T> Exp<T> $col(String name) {
        return new Column(name, Object.class);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static <T> Exp<T> $col(int position) {
        return new Column(position, Object.class);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static <T> Exp<T> $col(String name, Class<T> type) {
        return new Column<>(name, type);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static <T> Exp<T> $col(int position, Class<T> type) {
        return new Column<>(position, type);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame String column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static StrExp $str(String name) {
        return new StrColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame String column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static StrExp $str(int position) {
        return new StrColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame Integer column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Integer> $int(String name) {
        return new IntColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame Integer column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Integer> $int(int position) {
        return new IntColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame Long column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Long> $long(String name) {
        return new LongColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame Long column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Long> $long(int position) {
        return new LongColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame Float column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     *
     * @since 1.1.0
     */
    static NumExp<Float> $float(String name) {
        return new FloatColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame Float column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     *
     * @since 1.1.0
     */
    static NumExp<Float> $float(int position) {
        return new FloatColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame Double column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Double> $double(String name) {
        return new DoubleColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame Double column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static NumExp<Double> $double(int position) {
        return new DoubleColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame BigInteger column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     *
     * @since 2.0.0
     */
    static NumExp<BigInteger> $bigint(String name) {
        return new BigintColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame BigInteger column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     *
     * @since 2.0.0
     */
    static NumExp<BigInteger> $bigint(int position) {
        return new BigintColumn(position);
    }

    /**
     * Returns an expression whose "eval" returns a named DataFrame BigDecimal column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DecimalExp $decimal(String name) {
        return new DecimalColumn(name);
    }

    /**
     * Returns an expression whose "eval" returns a DataFrame BigDecimal column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DecimalExp $decimal(int position) {
        return new DecimalColumn(position);
    }

    // TODO: inconsistency - unlike numeric columns that support nulls, BooleanColumn is a "Condition",
    //  that can't have nulls, and will internally convert all nulls to "false"..
    //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
    static Condition $bool(String name) {
        return new BoolColumn(name);
    }

    static Condition $bool(int position) {
        return new BoolColumn(position);
    }

    /**
     * Returns DateExp whose "eval" returns a named DataFrame LocalDate column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DateExp $date(String name) {
        return new DateColumn(name);
    }

    /**
     * Returns a DateExp whose "eval" returns a DataFrame LocalDate column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DateExp $date(int position) {
        return new DateColumn(position);
    }

    /**
     * Returns a TimeExp whose "eval" returns a named DataFrame LocalTime column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static TimeExp $time(String name) {
        return new TimeColumn(name);
    }

    /**
     * Returns a TimeExp whose "eval" returns a DataFrame LocalTime column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static TimeExp $time(int position) {
        return new TimeColumn(position);
    }


    /**
     * Returns a DateTimeExp whose "eval" returns a named DataFrame LocalDateTime column (or the input Series object when called
     * on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DateTimeExp $dateTime(String name) {
        return new DateTimeColumn(name);
    }

    /**
     * Returns a DateTimeExp whose "eval" returns a DataFrame LocalDateTime column in the specified position (or the input Series
     * object when called on a Series). "reduce" operation is undefined and throws an exception.
     */
    static DateTimeExp $dateTime(int position) {
        return new DateTimeColumn(position);
    }

    /**
     * Returns an expression that evaluates to a named OffsetDateTime column.
     */
    static OffsetDateTimeExp $offsetDateTime(String name) {
        return new OffsetDateTimeColumn(name);
    }

    /**
     * Returns an expression that evaluates to a OffsetDateTime column at a given position.
     */
    static OffsetDateTimeExp $offsetDateTime(int position) {
        return new OffsetDateTimeColumn(position);
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
    static <T> Exp<T> ifNullVal(Exp<T> exp, T ifNull) {
        return new IfNullExp<>(exp, $val(ifNull));
    }

    /**
     * A function that does String concatenation of its arguments. Arguments can be any mix of constant values and
     * expressions.
     */
    static StrExp concat(Object... valuesOrExps) {
        return ConcatExp.of(valuesOrExps);
    }

    /**
     * Aggregating expression whose "reduce" operation returns the count of rows in the input.
     */
    static NumExp<Integer> count() {
        return CountExp.getInstance();
    }

    static NumExp<Integer> count(Condition filter) {
        return new CountExp(filter);
    }

    /**
     * Returns an expression that generates a Series with row numbers, starting with 1.
     */
    static NumExp<Integer> rowNum() {
        return RowNumExp.getInstance();
    }

    /**
     * Parses the provided String into an expression.
     *
     * @param exp an expression string to parse
     * @return expression object parsed from the string
     * @since 2.0.0
     */
    static Exp<?> parseExp(String exp) {
        return QLParserInvoker.parse(exp, ExpParser::expRoot).exp;
    }

    /**
     * Returns the type of the evaluation result. The type is used internally by the DBLib expression engine to compile
     * the most optimal evaluation path.
     *
     * <p> A note on DFLib schema "fuzziness": Callers cannot always predict the data schema upfront, and
     * oftentimes Java generics limitations prevent DFLib from using the right type even when it is known to the
     * caller. So many expressions will return <code>Object.class</code> instead of a more specific type. In this case,
     * the expression should still evaluate properly, but possibly suboptimally.
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
     * Evaluates self against the DataFrame argument, returning a Series result of the same size as the DataFrame height.
     */
    Series<T> eval(DataFrame df);

    /**
     * Evaluates self against the Series argument, returning a Series result of the same size as argument Series.
     */
    Series<T> eval(Series<?> s);

    /**
     * Evaluates self as an "aggregating" expression against the DataFrame argument, returning a single value.
     *
     * @since 2.0.0
     */
    T reduce(DataFrame df);

    /**
     * Evaluates self as an "aggregating" expression against the Series argument, returning a single value.
     *
     * @since 2.0.0
     */
    T reduce(Series<?> s);

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
     * (and closer to how SQL operates). If the "op" wants to process nulls, use {@link #mapVal(Function, boolean)}
     * with the "false" second parameter.
     */
    default <R> Exp<R> mapVal(Function<T, R> op) {
        return mapVal(op, true);
    }

    /**
     * Creates an expression that takes the result of this Exp and applies the provided value transformation function
     * to each value. If "hideNulls" argument is "true", DFLib will only pass non-null values to the "op"
     * function, so any source "null" value would automatically evaluate to "null". This makes writing
     * transformation functions easier (and closer to how SQL operates), but if nulls are relevant to the transformation
     * function, "false" can be passed to this method.
     */
    default <R> Exp<R> mapVal(Function<T, R> op, boolean hideNulls) {
        // Exp type vagueness ALERT: this is one of those expressions where we can't infer the correct return type...
        return hideNulls
                ? MapExp1.mapVal("map", (Class<R>) Object.class, this, op)
                : MapExp1.mapValWithNulls("map", (Class<R>) Object.class, this, op);
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

    default Condition in(Object... values) {
        return MapCondition1.map("in", this, s -> s.in(values));
    }

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
     * An expression that produces a Series with the same size as the source, but values shifted forward or
     * backwards depending on the sign of the offset parameter. Head or tail gaps produced by the shift are filled
     * with nulls.
     *
     * @since 1.1.0
     */
    default Exp<T> shift(int offset) {
        return shift(offset, null);
    }

    /**
     * An expression that produces a Series with the same size as the source, but values shifted forward or backwards
     * depending on the sign of the offset parameter. Head or tail gaps produced by the shift are filled with the
     * provided filler value.
     */
    default Exp<T> shift(int offset, T filler) {
        return new ShiftExp<>(this, offset, filler);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation applies a custom aggregation function to the
     * result of this expression.
     */
    default <A> Exp<A> agg(Function<Series<T>, A> aggregator) {
        return new ReduceExp1<>("_custom", (Class<A>) Object.class, this, aggregator, null);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation applies a custom aggregation function to the
     * result of this expression. Provided filter is applied to the input before evaluating this expression and
     * passing the result to the aggregator function.
     */
    default <A> Exp<A> agg(Condition filter, Function<Series<T>, A> aggregator) {
        return new ReduceExp1<>("_custom", (Class<A>) Object.class, this, aggregator, filter);
    }

    default Exp<T> first() {
        return new FirstExp<>(getType(), this, null);
    }

    default Exp<T> last() {
        return new LastExp<>(getType(), this, null);
    }

    default Exp<T> first(Condition filter) {
        return new FirstExp<>(getType(), this, filter);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation returns a String of concatenated Series values
     * separated by the delimiter.
     */
    default Exp<String> vConcat(String delimiter) {
        Function f = StringAggregators.vConcat(delimiter);
        return new ReduceExp2<>("vConcat", String.class, this, $val(delimiter), (s, d) -> (String) f.apply(s), null);
    }

    default Exp<String> vConcat(Condition filter, String delimiter) {
        Function f = StringAggregators.vConcat(delimiter);
        return new ReduceExp2<>("vConcat", String.class, this, $val(delimiter), (s, d) -> (String) f.apply(s), filter);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation returns a String of concatenated Series values
     * separated by the delimiter preceded by the prefix and followed by the suffix.
     */
    default Exp<String> vConcat(String delimiter, String prefix, String suffix) {
        Function f = StringAggregators.vConcat(delimiter, prefix, suffix);
        return new ReduceExpN<>(
                "vConcat",
                String.class,
                new Exp[]{this, $val(delimiter), $val(prefix), $val(suffix)},
                ss -> (String) f.apply(ss[0]),
                null);
    }

    default Exp<String> vConcat(Condition filter, String delimiter, String prefix, String suffix) {
        Function f = StringAggregators.vConcat(delimiter, prefix, suffix);
        return new ReduceExpN<>(
                "vConcat",
                String.class,
                new Exp[]{this, $val(delimiter), $val(prefix), $val(suffix)},
                ss -> (String) f.apply(ss[0]),
                filter);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation returns a Set containing all Series values.
     */
    default Exp<Set<T>> set() {
        Class setClass = Set.class;
        return new ReduceExp1<>("set", setClass, this, Series::toSet, null);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation returns a List containing all Series values.
     */
    default Exp<List<T>> list() {
        Class listClass = List.class;
        return new ReduceExp1<>("list", listClass, this, Series::toList, null);
    }

    /**
     * Creates an aggregating expression whose "reduce" operation returns an array containing all Series values.
     */
    default Exp<T[]> array(T[] template) {
        Class arrayClass = template.getClass();
        return new ReduceExp1<>("array", arrayClass, this, s -> s.toArray(template), null);
    }

    /**
     * Converts this expression to a {@link Condition} that can be used for row filtering, etc.
     */
    default Condition castAsBool() {
        return ConditionFactory.castAsBool(this);
    }

    default Condition mapBool(Function<Series<T>, BooleanSeries> op) {
        return MapCondition1.map("map", this, op);
    }

    default Condition mapBoolVal(Predicate<T> op) {
        return mapBoolVal(op, true);
    }

    /**
     * Creates a Condition expression that takes the results of this and another Exp and applies the provided Series
     * transformation BiFunction.
     *
     * @since 2.0.0
     */
    default <S> Condition mapBool(Exp<S> other, BiFunction<Series<T>, Series<S>, BooleanSeries> op) {
        return MapCondition2.map("map", this, other, op);
    }

    /**
     * Creates a Condition expression that takes the results of this and another Exp and applies the provided
     * BiPredicate to them. Note that DFLib will only pass non-null values to the "op" function. Any source
     * "null" values on either side of the expression automatically evaluate to "null" result. This assumption makes
     * writing transformation functions easier (and closer to how SQL operates). If special handling of nulls is
     * required, consider using {@link #mapBool(Exp, BiFunction)} method instead.
     *
     * @since 2.0.0
     */
    default <S> Condition mapBoolVal(Exp<S> other, BiPredicate<T, S> op) {
        return MapCondition2.mapVal("map", this, other, op);
    }

    default Condition mapBoolVal(Predicate<T> op, boolean hideNulls) {
        return hideNulls
                ? MapCondition1.mapVal("map", this, op)
                : MapCondition1.mapValWithNulls("map", this, op);
    }


    default DateExp castAsDate() {
        return DateExp1.mapVal("castAsDate", this.castAsStr(), LocalDate::parse);
    }


    default DateExp castAsDate(String format) {
        return castAsDate(DateTimeFormatter.ofPattern(format));
    }


    default DateExp castAsDate(DateTimeFormatter formatter) {
        return DateExp1.mapVal("castAsDate", this.castAsStr(), s -> LocalDate.parse(s, formatter));
    }


    default TimeExp castAsTime() {
        return TimeExp1.mapVal("castAsTime", this.castAsStr(), LocalTime::parse);
    }


    default TimeExp castAsTime(String formatter) {
        return castAsTime(DateTimeFormatter.ofPattern(formatter));
    }


    default TimeExp castAsTime(DateTimeFormatter formatter) {
        return TimeExp1.mapVal("castAsTime", this.castAsStr(), s -> LocalTime.parse(s, formatter));
    }


    default DateTimeExp castAsDateTime() {
        return DateTimeExp1.mapVal("castAsDateTime", this.castAsStr(), LocalDateTime::parse);
    }


    default DateTimeExp castAsDateTime(String format) {
        return castAsDateTime(DateTimeFormatter.ofPattern(format));
    }

    default DateTimeExp castAsDateTime(DateTimeFormatter formatter) {
        return DateTimeExp1.mapVal("castAsDateTime", this.castAsStr(), s -> LocalDateTime.parse(s, formatter));
    }

    /**
     * @since 1.1.0
     */
    default OffsetDateTimeExp castAsOffsetDateTime() {
        return OffsetDateTimeExp1.mapVal("castAsOffsetDateTime", this.castAsStr(), OffsetDateTime::parse);
    }

    /**
     * @since 1.1.0
     */
    default OffsetDateTimeExp castAsOffsetDateTime(String format) {
        return castAsOffsetDateTime(DateTimeFormatter.ofPattern(format));
    }

    /**
     * @since 1.1.0
     */
    default OffsetDateTimeExp castAsOffsetDateTime(DateTimeFormatter formatter) {
        return OffsetDateTimeExp1.mapVal("castAsOffsetDateTime", this.castAsStr(), s -> OffsetDateTime.parse(s, formatter));
    }

    default StrExp castAsStr() {
        return StrExp1.mapVal("castAsStr", this, o -> o.toString());
    }

    default NumExp<Integer> castAsInt() {
        // Need to do multiple conversions, so that we can properly cast any number format.
        // Int expressions must override this method to return "this"
        return castAsStr().castAsDecimal().castAsInt();
    }

    default NumExp<Long> castAsLong() {
        // Need to do multiple conversions, so that we can properly cast any number format.
        // Long expressions must override this method to return "this"
        return castAsStr().castAsDecimal().castAsLong();
    }

    /**
     * @since 1.1.0
     */
    default NumExp<Float> castAsFloat() {
        return castAsStr().castAsFloat();
    }

    default NumExp<Double> castAsDouble() {
        return castAsStr().castAsDouble();
    }

    /**
     * @since 2.0.0
     */
    default NumExp<BigInteger> castAsBigint() {
        return castAsStr().castAsBigint();
    }

    default DecimalExp castAsDecimal() {
        return castAsStr().castAsDecimal();
    }
}
