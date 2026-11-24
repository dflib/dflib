package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

/**
 * QL {@code first(e, filter)} function.
 *
 * @since 2.0.0
 */
public class First2Function implements Udf2<Object, Boolean, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Boolean> filter) {
        return e.first(filter.castAsBool());
    }
}
