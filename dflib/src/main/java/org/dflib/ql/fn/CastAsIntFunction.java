package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsInt(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsIntFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return e.castAsInt();
    }
}
