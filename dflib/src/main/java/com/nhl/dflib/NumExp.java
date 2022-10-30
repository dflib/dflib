package com.nhl.dflib;

import com.nhl.dflib.exp.bool.ConditionFactory;
import com.nhl.dflib.exp.filter.PreFilteredNumExp;
import com.nhl.dflib.exp.num.NumericExpFactory;

/**
 * An expression applied to any Java primitive or object numeric columns. Provides various arithmetic, comparison and
 * statistical operations. Allows to combine arguments of different numeric types in a single expression.
 *
 * @since 0.11
 */
public interface NumExp<N extends Number> extends Exp<N> {

    default NumExp<?> add(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).add(this, exp);
    }

    default NumExp<?> add(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).add(this, ve);
    }

    /**
     * Performs subtraction operation between the values in two columns.
     */
    default NumExp<?> sub(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).sub(this, exp);
    }

    /**
     * Performs subtraction operation between a column and a scalar value.
     */
    default NumExp<?> sub(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).sub(this, ve);
    }

    /**
     * Performs multiplication operation between the values in two columns.
     */
    default NumExp<?> mul(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).mul(this, exp);
    }

    /**
     * Performs multiplication operation between a column and a scalar value.
     */
    default NumExp<?> mul(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).mul(this, ve);
    }

    /**
     * Performs a division operation between the values in two columns.
     */
    default NumExp<?> div(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).div(this, exp);
    }

    /**
     * Performs a division operation between a column and a scalar value.
     */
    default NumExp<?> div(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).div(this, ve);
    }

    default NumExp<?> mod(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).mod(this, exp);
    }

    default NumExp<?> mod(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).mod(this, ve);
    }

    default NumExp<N> abs() {
        return (NumExp<N>) NumericExpFactory.factory(this).abs(this);
    }

    /**
     * @since 0.16
     */
    @Override
    default NumExp<Integer> castAsInt() {
        return NumericExpFactory.factory(this).castAsInt(this);
    }

    @Override
    default DecimalExp castAsDecimal() {
        return NumericExpFactory.factory(this).castAsDecimal(this);
    }

    /**
     * @since 0.16
     */
    default <E extends Enum<E>> Exp<E> castAsEnum(Class<E> type) {
        return NumericExpFactory.factory(this).castAsEnum(this, type);
    }

    @Override
    default Condition eq(Exp<?> exp) {
        return Number.class.isAssignableFrom(exp.getType())
                ? NumericExpFactory.factory(this, (Exp<? extends Number>) exp).eq(this, (Exp<? extends Number>) exp)
                // we may have returned "false" without checking, except we want to handle mis-reported types, nulls, etc.
                : Exp.super.eq(exp);
    }

    @Override
    default Condition eq(Object value) {
        return value instanceof Number
                ? eq(Exp.$val(value))
                // TODO: return either null check or a "false" exp here?
                : Exp.super.eq(value);
    }

    @Override
    default Condition ne(Exp<?> exp) {
        return Number.class.isAssignableFrom(exp.getType())
                ? NumericExpFactory.factory(this, (Exp<? extends Number>) exp).ne(this, (Exp<? extends Number>) exp)
                // we may have returned "false" without checking, except we want to handle mis-reported types, nulls, etc.
                : Exp.super.ne(exp);
    }

    @Override
    default Condition ne(Object value) {
        return value instanceof Number
                ? ne(Exp.$val(value))
                // TODO: return either null check or a "false" exp here?
                : Exp.super.ne(value);
    }

    default Condition lt(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).lt(this, exp);
    }

    default Condition lt(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).lt(this, ve);
    }

    default Condition le(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).le(this, exp);
    }

    default Condition le(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).le(this, ve);
    }

    default Condition gt(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).gt(this, exp);
    }

    default Condition gt(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).gt(this, ve);
    }

    default Condition ge(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).ge(this, exp);
    }

    default Condition ge(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).ge(this, ve);
    }

    /**
     * A "running total" function that produces a cumulative sum of each row from the beginning of the DataFrame or
     * Series.
     *
     * @since 0.14
     */
    default NumExp<?> cumSum() {
        return NumericExpFactory.factory(this).cumSum(this);
    }

    default NumExp<?> sum() {
        return NumericExpFactory.factory(this).sum(this);
    }

    default NumExp<?> sum(Condition filter) {
        return new PreFilteredNumExp<>(filter, sum());
    }

    default NumExp<?> min() {
        return NumericExpFactory.factory(this).min(this);
    }

    default NumExp<?> min(Condition filter) {
        return new PreFilteredNumExp<>(filter, min());
    }

    default NumExp<?> max() {
        return NumericExpFactory.factory(this).max(this);
    }

    default NumExp<?> max(Condition filter) {
        return new PreFilteredNumExp<>(filter, max());
    }

    default NumExp<?> avg() {
        return NumericExpFactory.factory(this).avg(this);
    }

    default NumExp<?> avg(Condition filter) {
        return new PreFilteredNumExp<>(filter, avg());
    }

    default NumExp<?> median() {
        return NumericExpFactory.factory(this).median(this);
    }

    default NumExp<?> median(Condition filter) {
        return new PreFilteredNumExp<>(filter, median());
    }

    /**
     * @since 0.14
     */
    @Override
    default Condition castAsCondition() {
        return ConditionFactory.castAsCondition(this);
    }
}
