package com.nhl.dflib;

import com.nhl.dflib.exp.*;
import com.nhl.dflib.exp.condition.AndCondition;
import com.nhl.dflib.exp.condition.BooleanColumn;
import com.nhl.dflib.exp.condition.OrCondition;
import com.nhl.dflib.exp.func.IfNullFunction;
import com.nhl.dflib.exp.num.DecimalColumn;
import com.nhl.dflib.exp.num.DoubleColumn;
import com.nhl.dflib.exp.num.IntColumn;
import com.nhl.dflib.exp.num.LongColumn;
import com.nhl.dflib.exp.str.StringColumn;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An expression that can be evaluated against a DataFrame to produce a Series. Provides a number of factory methods
 * (all starting with "$") to create various basic expressions that can be combined in more complex expressions.
 *
 * @since 0.11
 */
public interface Exp<V> {

    /**
     * Returns an expression that evaluates to a Series containing a single value.
     */
    static <V> ValueExp<V> $val(V value) {

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
    static ValueExp<?> $col(String name) {
        return new ColumnExp(name, Object.class);
    }

    /**
     * Returns an expression that evaluates to a DataFrame column at a given position
     */
    static ValueExp<?> $col(int position) {
        return new ColumnExp(position, Object.class);
    }

    /**
     * Returns an expression that evaluates to a named DataFrame String column.
     */
    static StringColumn $str(String name) {
        return new StringColumn(name);
    }

    /**
     * Returns an expression that evaluates to a DataFrame String column at a given position.
     */
    static StringColumn $str(int position) {
        return new StringColumn(position);
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
    static <V> Exp<V> ifNull(Exp<V> exp, Exp<V> ifNullExp) {
        return new IfNullFunction(exp, ifNullExp);
    }

    /**
     * A function that evaluates "exp", replacing any null values with "ifNull" value.
     */
    static <V> Exp<V> ifNull(Exp<V> exp, V ifNull) {
        return new IfNullFunction(exp, $val(ifNull));
    }

    String getName();

    Class<V> getType();

    Series<V> eval(DataFrame df);

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
    default Exp<V> as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return name.equals(getName()) ? this : new RenamedExp<>(name, this);
    }
}
