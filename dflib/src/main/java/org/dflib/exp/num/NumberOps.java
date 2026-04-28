package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.Exp;

final class NumberOps {

    private NumberOps() {
    }

    interface Unary {
        Exp<? extends Number> apply(
                NumericExpFactory factory,
                Exp<? extends Number> exp
        );
    }

    interface Binary {
        Exp<? extends Number> apply(
                NumericExpFactory factory,
                Exp<? extends Number> left,
                Exp<? extends Number> right
        );
    }

    interface BinaryCondition {
        Condition apply(
                NumericExpFactory factory,
                Exp<? extends Number> left,
                Exp<? extends Number> right
        );
    }

    interface TernaryCondition {
        Condition apply(
                NumericExpFactory factory,
                Exp<? extends Number> one,
                Exp<? extends Number> two,
                Exp<? extends Number> three
        );
    }
}
