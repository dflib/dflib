package com.nhl.dflib;

import com.nhl.dflib.exp.num.DecimalExpScalar2;
import com.nhl.dflib.exp.num.NumericExpFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @since 0.11
 */
public interface DecimalExp extends NumExp<BigDecimal> {

    @Override
    default DecimalExp castAsDecimal() {
        return this;
    }

    /**
     * Creates an expression that guarantees specific scale for the BigDecimal result of this expression.
     */
    default DecimalExp scale(int scale) {
        return DecimalExpScalar2.mapVal("scale", this, scale, (bd, s) -> bd.setScale(s, RoundingMode.HALF_UP));
    }

    // override super operations that return BigDecimal to return DecimalExp. This would allow caller to invoke
    // Decimal-specific methods, e.g. scale(..)

    @Override
    default DecimalExp add(Exp<? extends Number> exp) {
        return NumericExpFactory.decimalFactory().add(this, exp);
    }

    @Override
    default DecimalExp add(Number val) {
        return NumericExpFactory.decimalFactory().add(this, Exp.$val(val));
    }

    @Override
    default DecimalExp sub(Exp<? extends Number> exp) {
        return NumericExpFactory.decimalFactory().sub(this, exp);
    }

    @Override
    default DecimalExp sub(Number val) {
        return NumericExpFactory.decimalFactory().sub(this, Exp.$val(val));
    }

    @Override
    default DecimalExp div(Exp<? extends Number> exp) {
        return NumericExpFactory.decimalFactory().div(this, exp);
    }

    @Override
    default DecimalExp div(Number val) {
        return NumericExpFactory.decimalFactory().div(this, Exp.$val(val));
    }

    @Override
    default DecimalExp mul(Exp<? extends Number> exp) {
        return NumericExpFactory.decimalFactory().mul(this, exp);
    }

    @Override
    default DecimalExp mul(Number val) {
        return NumericExpFactory.decimalFactory().mul(this, Exp.$val(val));
    }

    @Override
    default DecimalExp mod(Exp<? extends Number> exp) {
        return NumericExpFactory.decimalFactory().mod(this, exp);
    }

    @Override
    default DecimalExp mod(Number val) {
        return NumericExpFactory.decimalFactory().mod(this, Exp.$val(val));
    }

    @Override
    default NumExp<BigDecimal> abs() {
        return NumericExpFactory.decimalFactory().abs(this);
    }

    @Override
    default DecimalExp avg() {
        return NumericExpFactory.decimalFactory().avg(this);
    }

    @Override
    default DecimalExp sum() {
        return NumericExpFactory.decimalFactory().sum(this);
    }

    @Override
    default DecimalExp min() {
        return NumericExpFactory.decimalFactory().min(this);
    }

    @Override
    default DecimalExp max() {
        return NumericExpFactory.decimalFactory().max(this);
    }

    @Override
    default DecimalExp median() {
        return NumericExpFactory.decimalFactory().median(this);
    }
}
