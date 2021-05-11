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

    default NumericExp<?> subtract(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).subtract(this, exp);
    }

    default NumericExp<?> subtract(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).subtract(this, ve);
    }

    default NumericExp<?> multiply(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).multiply(this, exp);
    }

    default NumericExp<?> multiply(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).multiply(this, ve);
    }

    default NumericExp<?> divide(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).divide(this, exp);
    }

    default NumericExp<?> divide(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).divide(this, ve);
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

    default SeriesCondition lt(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).lt(this, exp);
    }

    default SeriesCondition lt(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).lt(this, ve);
    }

    default SeriesCondition le(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).le(this, exp);
    }

    default SeriesCondition le(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).le(this, ve);
    }

    default SeriesCondition gt(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).gt(this, exp);
    }

    default SeriesCondition gt(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).gt(this, ve);
    }

    default SeriesCondition ge(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).ge(this, exp);
    }

    default SeriesCondition ge(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).ge(this, ve);
    }

    default NumericExp<?> sum() {
        return NumericExpFactory.factory(this).sum(this);
    }

    default NumericExp<?> sum(SeriesCondition filter) {
        return new PreFilteredNumericExp<>(filter, sum());
    }

    default NumericExp<?> min() {
        return NumericExpFactory.factory(this).min(this);
    }

    default NumericExp<?> min(SeriesCondition filter) {
        return new PreFilteredNumericExp<>(filter, min());
    }

    default NumericExp<?> max() {
        return NumericExpFactory.factory(this).max(this);
    }

    default NumericExp<?> max(SeriesCondition filter) {
        return new PreFilteredNumericExp<>(filter, max());
    }

    default NumericExp<?> avg() {
        return NumericExpFactory.factory(this).avg(this);
    }

    default NumericExp<?> avg(SeriesCondition filter) {
        return new PreFilteredNumericExp<>(filter, avg());
    }

    default NumericExp<?> median() {
        return NumericExpFactory.factory(this).median(this);
    }

    default NumericExp<?> median(SeriesCondition filter) {
        return new PreFilteredNumericExp<>(filter, median());
    }
}
