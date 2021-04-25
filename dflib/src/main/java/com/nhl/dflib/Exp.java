package com.nhl.dflib;

import com.nhl.dflib.exp.*;
import com.nhl.dflib.exp.condition.AndCondition;
import com.nhl.dflib.exp.condition.BooleanColumn;
import com.nhl.dflib.exp.condition.OrCondition;
import com.nhl.dflib.exp.num.DecimalColumn;
import com.nhl.dflib.exp.num.DoubleColumn;
import com.nhl.dflib.exp.num.IntColumn;
import com.nhl.dflib.exp.num.LongColumn;
import com.nhl.dflib.exp.ExpSorter;
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
     * Returns an expression that evaluates to a Series of Strings for a named DataFrame column.
     */
    static StringColumn $str(String name) {
        return new StringColumn(name);
    }

    /**
     * Returns an expression that evaluates to a Series of Integers for a named DataFrame column.
     */
    static NumericExp<Integer> $int(String name) {
        return new IntColumn(name);
    }

    /**
     * Returns an expression that evaluates to a Series of Longs for a named DataFrame column.
     */
    static NumericExp<Long> $long(String name) {
        return new LongColumn(name);
    }

    /**
     * Returns an expression that evaluates to a Series of Doubles for a named DataFrame column.
     */
    static NumericExp<Double> $double(String name) {
        return new DoubleColumn(name);
    }

    /**
     * Returns an expression that evaluates to a Series of BigDecimals for a named DataFrame column.
     */
    static NumericExp<BigDecimal> $decimal(String name) {
        return new DecimalColumn(name);
    }

    // TODO: inconsistency - unlike numeric columns that support nulls, BooleanColumn is a "Condition",
    //  that can have no nulls, and will internally convert all nulls to "false"..
    //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
    static Condition $bool(String name) {
        return new BooleanColumn(name);
    }

    static Condition $or(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new OrCondition(conditions);
    }

    static Condition $and(Condition... conditions) {
        return conditions.length == 1
                ? conditions[0] : new AndCondition(conditions);
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
     * Creates a new expression by renaming the current expression.
     */
    default Exp<V> named(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return name.equals(getName()) ? this : new RenamedExp<>(name, this);
    }
}
