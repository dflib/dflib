package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.Udf2;
import org.dflib.Udf3;

public class Substr3Function implements Udf3<Object, Integer, Integer, String> {
    @Override
    public Exp<String> call(Exp<Object> exp1, Exp<Integer> exp2, Exp<Integer> exp3) {
        // TODO: any proper way to get integer values?
        return exp1.substr(exp2.reduce((Series<?>) null), exp3.reduce((Series<?>) null));
    }
}
