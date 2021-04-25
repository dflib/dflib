package com.nhl.dflib.exp;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.num.NumericExpFactory;

/**
 * @since 0.11
 */
public interface NumericExp<N extends Number> extends ValueExp<N> {

    default NumericExp<?> plus(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).plus(this, exp);
    }

    default NumericExp<?> plus(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).plus(this, ve);
    }

    default NumericExp<?> minus(Exp<? extends Number> exp) {
        return NumericExpFactory.factory(this, exp).minus(this, exp);
    }

    default NumericExp<?> minus(Number val) {
        Exp<? extends Number> ve = Exp.$val(val);
        return NumericExpFactory.factory(this, ve).minus(this, ve);
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
