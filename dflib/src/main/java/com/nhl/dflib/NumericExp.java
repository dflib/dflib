package com.nhl.dflib;

import com.nhl.dflib.exp.num.NumericExpFactory;

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

    default NumericExp<?> castAsDecimal(int scale) {
        return NumericExpFactory.factory(this).castAsDecimal(this, scale);
    }

    default Condition lt(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).lt(this, exp);
    }

    default Condition lt(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).ge(this, ve);
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
}
