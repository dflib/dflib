package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf1;

public class RoundFunction implements Udf1<Number, Number> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public NumExp<Number> call(Exp<Number> exp) {
        return ((NumExp) exp).round();
    }
}
