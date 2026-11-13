package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code ifNull(e, ifNull)} function.
 *
 * @since 2.0.0
 */
public class IfNullFunction implements QLFunction {

    public <T> Exp<T> call(Exp<T> e, Exp<T> ifNull) {
        return Exp.ifNull(e, ifNull);
    }
}
