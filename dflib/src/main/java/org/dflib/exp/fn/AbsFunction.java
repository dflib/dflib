package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf1;

public class AbsFunction implements Udf1<Number, Number> {
    @Override
    public NumExp<Number> call(Exp<Number> exp) {
        if(exp instanceof NumExp<Number> numExp) {
            return numExp.abs();
        } else {
            throw new IllegalArgumentException("Numeric expression expected, got " + exp.getClass());
        }
    }
}
