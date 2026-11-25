package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf0;

/**
 * QL {@code rowNum()} function.
 *
 * @since 2.0.0
 */
public class RowNumFunction implements Udf0<Integer> {

    @Override
    public Exp<Integer> call() {
        return Exp.rowNum();
    }
}
