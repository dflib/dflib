package com.nhl.dflib;

import com.nhl.dflib.seriesexp.num.NumericExpFactory;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public interface NumericSeriesExp<N extends Number> extends SeriesExp<N> {

    default NumericSeriesExp<?> add(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).add(this, exp);
    }

    default NumericSeriesExp<?> add(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).add(this, ve);
    }

    default NumericSeriesExp<?> subtract(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).subtract(this, exp);
    }

    default NumericSeriesExp<?> subtract(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).subtract(this, ve);
    }

    default NumericSeriesExp<?> multiply(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).multiply(this, exp);
    }

    default NumericSeriesExp<?> multiply(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).multiply(this, ve);
    }

    default NumericSeriesExp<?> divide(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).divide(this, exp);
    }

    default NumericSeriesExp<?> divide(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).divide(this, ve);
    }

    default NumericSeriesExp<?> mod(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).mod(this, exp);
    }

    default NumericSeriesExp<?> mod(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).mod(this, ve);
    }

    default NumericSeriesExp<BigDecimal> castAsDecimal(int scale) {
        return NumericExpFactory.factory(this).castAsDecimal(this, scale);
    }

    default NumericSeriesExp<?> sum() {
        return NumericExpFactory.factory(this).sum(this);
    }

    default NumericSeriesExp<?> min() {
        return NumericExpFactory.factory(this).min(this);
    }

    default NumericSeriesExp<?> max() {
        return NumericExpFactory.factory(this).max(this);
    }

    default NumericSeriesExp<?> avg() {
        return NumericExpFactory.factory(this).avg(this);
    }

    default NumericSeriesExp<?> median() {
        return NumericExpFactory.factory(this).median(this);
    }

    default SeriesCondition lt(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).lt(this, exp);
    }

    default SeriesCondition lt(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).lt(this, ve);
    }

    default SeriesCondition le(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).le(this, exp);
    }

    default SeriesCondition le(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).le(this, ve);
    }

    default SeriesCondition gt(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).gt(this, exp);
    }

    default SeriesCondition gt(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).gt(this, ve);
    }

    default SeriesCondition ge(SeriesExp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).ge(this, exp);
    }

    default SeriesCondition ge(Number val) {
        SeriesExp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).ge(this, ve);
    }
}
