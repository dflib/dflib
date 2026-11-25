package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code last(e)} function.
 *
 * @since 2.0.0
 */
public class LastFunction implements Udf1<Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e) {
        return e.last();
    }
}
