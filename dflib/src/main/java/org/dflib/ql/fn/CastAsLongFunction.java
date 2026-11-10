package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsLong(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsLongFunction implements QLFunction {

    public NumExp<Long> call(Exp<?> e) {
        return e.castAsLong();
    }
}
