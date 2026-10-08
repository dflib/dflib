package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsBool(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsBoolFunction implements QLFunction {

    public Condition call(Exp<?> e) {
        return e.castAsBool();
    }
}
