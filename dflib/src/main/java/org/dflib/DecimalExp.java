package org.dflib;

import org.dflib.exp.num.DecimalAsExp;
import org.dflib.exp.num.DecimalExp2;
import org.dflib.exp.num.NumericExpFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static org.dflib.Exp.$val;

public interface DecimalExp extends NumExp<BigDecimal> {

    /**
     * @since 2.0.0
     */
    @Override
    default DecimalExp as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new DecimalAsExp(name, this);
    }

    @Override
    default DecimalExp castAsDecimal() {
        return this;
    }

    /**
     * Creates an expression that guarantees specific scale for the BigDecimal result of this expression.
     */
    default DecimalExp scale(int scale) {
        return DecimalExp2.mapVal("scale", this, $val(scale), (bd, s) -> bd.setScale(s, RoundingMode.HALF_UP));
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
    default DecimalExp cumSum() {
        return NumericExpFactory.decimalFactory().cumSum(this);
    }

    @Override
    default DecimalExp avg() {
        return NumericExpFactory.decimalFactory().avg(this);
    }

    @Override
    default DecimalExp sum() {
        return sum(null);
    }

    @Override
    default DecimalExp sum(Condition filter) {
        return NumericExpFactory.decimalFactory().sum(this, filter);
    }

    @Override
    default DecimalExp min() {
        return min(null);
    }

    @Override
    default DecimalExp min(Condition filter) {
        return NumericExpFactory.decimalFactory().min(this, filter);
    }

    @Override
    default DecimalExp max() {
        return max(null);
    }

    @Override
    default DecimalExp max(Condition filter) {
        return NumericExpFactory.decimalFactory().max(this, filter);
    }

    @Override
    default DecimalExp median() {
        return median(null);
    }

    @Override
    default DecimalExp median(Condition filter) {
        return NumericExpFactory.decimalFactory().median(this, filter);
    }

    @Override
    default DecimalExp quantile(double q) {
        return quantile(q, null);
    }

    @Override
    default DecimalExp quantile(double q, Condition filter) {
        return NumericExpFactory.decimalFactory().quantile(this, q, filter);
    }
}
