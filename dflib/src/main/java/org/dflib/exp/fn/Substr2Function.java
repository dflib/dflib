package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.Udf2;

public class Substr2Function implements Udf2<Object, Integer, String> {
    @Override
    public Exp<String> call(Exp<Object> exp1, Exp<Integer> exp2) {
        // TODO: any proper way to get this value?
        return exp1.substr(exp2.reduce((Series<?>) null));
    }
}
