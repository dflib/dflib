package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsStr(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsStrFunction implements QLFunction {

    public StrExp call(Exp<?> e) {
        return e.castAsStr();
    }
}
