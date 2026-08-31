package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

public class LowerFunction implements Udf1<Object, String> {
    @Override
    public Exp<String> call(Exp<Object> exp) {
        return exp.lower();
    }
}
