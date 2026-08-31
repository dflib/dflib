package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

public class LenFunction implements Udf1<String, Integer> {

    @Override
    public Exp<Integer> call(Exp<String> exp) {
        return exp.castAsStr().len();
    }
}
