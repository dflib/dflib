package com.nhl.dflib;

import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.num.DecimalUnaryExp;
import com.nhl.dflib.exp.num.NumericExpFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @since 0.11
 */
public interface DecimalExp extends NumericExp<BigDecimal> {

    @Override
    default DecimalExp castAsDecimal() {
        return this;
    }

    /**
     * Created a derived expression that guarantees specific scale for the BigDecimal result.
     */
    default DecimalExp scale(int scale) {
        return new DecimalUnaryExp<>("scale", this, UnaryExp.toSeriesOp(bd -> bd.setScale(scale, RoundingMode.HALF_UP)));
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
