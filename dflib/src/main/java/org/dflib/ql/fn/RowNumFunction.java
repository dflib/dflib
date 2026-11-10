package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code rowNum()} function.
 *
 * @since 2.0.0
 */
public class RowNumFunction implements QLFunction {

    public NumExp<Integer> call() {
        return Exp.rowNum();
    }
}
