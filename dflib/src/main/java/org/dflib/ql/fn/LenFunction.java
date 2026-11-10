package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code len(e)} function.
 *
 * @since 2.0.0
 */
public class LenFunction implements QLFunction {

    public NumExp<Integer> call(Exp<?> e) {
        return e.castAsStr().len();
    }
}
