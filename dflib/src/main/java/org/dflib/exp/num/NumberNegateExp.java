package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Exp;

class NumberNegateExp extends NumberExp1 {

    NumberNegateExp(Exp<? extends Number> exp) {
        super("-", exp, NumericExpFactory::negate);
    }

    @Override
    public String toQL() {
        return NegateExp.negateToQL(opName, exp, exp.toQL(), false);
    }

    @Override
    public String toQL(DataFrame df) {
        return NegateExp.negateToQL(opName, exp, exp.toQL(df), true);
    }
}
