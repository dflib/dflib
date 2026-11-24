package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsLong(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsLongFunction implements Udf1<Object, Long> {

    @Override
    public Exp<Long> call(Exp<Object> e) {
        return e.castAsLong();
    }
}
