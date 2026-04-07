package org.dflib.exp.fn;

import org.dflib.Exp;
import org.dflib.Udf0;

public class RowNumFunction implements Udf0<Integer> {
    @Override
    public Exp<Integer> call() {
        return Exp.rowNum();
    }
}
