package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code lower(e)} function.
 *
 * @since 2.0.0
 */
public class LowerFunction implements QLFunction {

    public StrExp call(Exp<?> e) {
        return e.lower();
    }
}
