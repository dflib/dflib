package com.nhl.dflib;

import com.nhl.dflib.exp.filter.PreFilteredNumericExp;
import com.nhl.dflib.exp.num.NumericExpFactory;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public interface NumericExp<N extends Number> extends Exp<N> {

    default NumericExp<?> add(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).add(this, exp);
    }

    default NumericExp<?> add(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).add(this, ve);
    }

    /**
     * Performs subtraction operation between the values in two columns.
     */
    default NumericExp<?> sub(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).sub(this, exp);
    }

    /**
     * Performs subtraction operation between a column and a scalar value.
     */
    default NumericExp<?> sub(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).sub(this, ve);
    }

    /**
     * Performs multiplication operation between the values in two columns.
     */
    default NumericExp<?> mul(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).mul(this, exp);
    }

    /**
     * Performs multiplication operation between a column and a scalar value.
     */
    default NumericExp<?> mul(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).mul(this, ve);
    }

    /**
     * Performs a division operation between the values in two columns.
     */
    default NumericExp<?> div(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).div(this, exp);
    }

    /**
     * Performs a division operation between a column and a scalar value.
     */
    default NumericExp<?> div(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).div(this, ve);
    }

    default NumericExp<?> mod(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).mod(this, exp);
    }

    default NumericExp<?> mod(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).mod(this, ve);
    }

    default NumericExp<BigDecimal> castAsDecimal(int scale) {
        return NumericExpFactory.factory(this).castAsDecimal(this, scale);
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
                : Exp.super.eq(value);
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

    default NumericExp<?> sum() {
        return NumericExpFactory.factory(this).sum(this);
    }

    default NumericExp<?> sum(Condition filter) {
        return new PreFilteredNumericExp<>(filter, sum());
    }

    default NumericExp<?> min() {
        return NumericExpFactory.factory(this).min(this);
    }

    default NumericExp<?> min(Condition filter) {
        return new PreFilteredNumericExp<>(filter, min());
    }

    default NumericExp<?> max() {
        return NumericExpFactory.factory(this).max(this);
    }

    default NumericExp<?> max(Condition filter) {
        return new PreFilteredNumericExp<>(filter, max());
    }

    default NumericExp<?> avg() {
        return NumericExpFactory.factory(this).avg(this);
    }

    default NumericExp<?> avg(Condition filter) {
        return new PreFilteredNumericExp<>(filter, avg());
    }

    default NumericExp<?> median() {
        return NumericExpFactory.factory(this).median(this);
    }

    default NumericExp<?> median(Condition filter) {
        return new PreFilteredNumericExp<>(filter, median());
    }
}
