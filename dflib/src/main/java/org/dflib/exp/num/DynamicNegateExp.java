package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Exp;

class DynamicNegateExp extends DynamicNumExp1 {

    DynamicNegateExp(Exp<? extends Number> exp) {
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
