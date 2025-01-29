package org.dflib;

import org.dflib.exp.num.BigIntegerAsExp;
import org.dflib.exp.num.NumericExpFactory;

import java.math.BigInteger;
import java.util.Objects;

public interface BigIntegerExp extends NumExp<BigInteger> {

    /**
     * @since 2.0.0
     */
    @Override
    default BigIntegerExp as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new BigIntegerAsExp(name, this);
    }

    @Override
    default BigIntegerExp castAsBigInteger() {
        return this;
    }

    @Override
    default BigIntegerExp add(Exp<? extends Number> exp) {
        return NumericExpFactory.bigIntegerFactory().add(this, exp);
    }

    @Override
    default BigIntegerExp add(Number val) {
        return NumericExpFactory.bigIntegerFactory().add(this, Exp.$val(val));
    }

    @Override
    default BigIntegerExp sub(Exp<? extends Number> exp) {
        return NumericExpFactory.bigIntegerFactory().sub(this, exp);
    }

    @Override
    default BigIntegerExp sub(Number val) {
        return NumericExpFactory.bigIntegerFactory().sub(this, Exp.$val(val));
    }

    @Override
    default BigIntegerExp div(Number val) {
        return NumericExpFactory.bigIntegerFactory().div(this, Exp.$val(val));
    }

    @Override
    default BigIntegerExp mul(Exp<? extends Number> exp) {
        return NumericExpFactory.bigIntegerFactory().mul(this, exp);
    }

    @Override
    default BigIntegerExp mul(Number val) {
        return NumericExpFactory.bigIntegerFactory().mul(this, Exp.$val(val));
    }

    @Override
    default BigIntegerExp mod(Exp<? extends Number> exp) {
        return NumericExpFactory.bigIntegerFactory().mod(this, exp);
    }

    @Override
    default BigIntegerExp mod(Number val) {
        return NumericExpFactory.bigIntegerFactory().mod(this, Exp.$val(val));
    }

    @Override
    default NumExp<BigInteger> abs() {
        return NumericExpFactory.bigIntegerFactory().abs(this);
    }

    @Override
    default BigIntegerExp cumSum() {
        return NumericExpFactory.bigIntegerFactory().cumSum(this);
    }

    @Override
    default BigIntegerExp avg() {
        return NumericExpFactory.bigIntegerFactory().avg(this);
    }

    @Override
    default BigIntegerExp sum() {
        return sum(null);
    }

    @Override
    default BigIntegerExp sum(Condition filter) {
        return NumericExpFactory.bigIntegerFactory().sum(this, filter);
    }

    @Override
    default BigIntegerExp min() {
        return min(null);
    }

    @Override
    default BigIntegerExp min(Condition filter) {
        return NumericExpFactory.bigIntegerFactory().min(this, filter);
    }

    @Override
    default BigIntegerExp max() {
        return max(null);
    }

    @Override
    default BigIntegerExp max(Condition filter) {
        return NumericExpFactory.bigIntegerFactory().max(this, filter);
    }

    @Override
    default DecimalExp median() {
        return median(null);
    }

    @Override
    default DecimalExp median(Condition filter) {
        return NumericExpFactory.bigIntegerFactory().median(this, filter);
    }

    @Override
    default DecimalExp quantile(double q) {
        return quantile(q, null);
    }

    @Override
    default DecimalExp quantile(double q, Condition filter) {
        return NumericExpFactory.bigIntegerFactory().quantile(this, q, filter);
    }

    @Override
    default BigIntegerExp round() {
        return NumericExpFactory.bigIntegerFactory().round(this);
    }
}
