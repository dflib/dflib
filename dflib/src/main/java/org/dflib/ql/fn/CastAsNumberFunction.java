package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsNumber(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsNumberFunction implements QLFunction {

    public NumExp<?> call(Exp<?> e) {
        return e.castAsNumber();
    }
}
