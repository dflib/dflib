package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code first(e)} function.
 *
 * @since 2.0.0
 */
public class First1Function implements Udf1<Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e) {
        return e.first();
    }
}
