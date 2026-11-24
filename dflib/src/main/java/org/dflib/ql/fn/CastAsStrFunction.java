package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsStr(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsStrFunction implements Udf1<Object, String> {

    @Override
    public Exp<String> call(Exp<Object> e) {
        return e.castAsStr();
    }
}
