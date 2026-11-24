package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsBool(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsBoolFunction implements Udf1<Object, Boolean> {

    @Override
    public Exp<Boolean> call(Exp<Object> e) {
        return e.castAsBool();
    }
}
