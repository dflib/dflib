package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsInt(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsIntFunction implements QLFunction {

    public NumExp<Integer> call(Exp<?> e) {
        return e.castAsInt();
    }
}
