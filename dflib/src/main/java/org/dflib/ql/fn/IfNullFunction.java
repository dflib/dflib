package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

/**
 * QL {@code ifNull(e, ifNull)} function.
 *
 * @since 2.0.0
 */
public class IfNullFunction implements Udf2<Object, Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Object> ifNull) {
        return Exp.ifNull(e, ifNull);
    }
}
