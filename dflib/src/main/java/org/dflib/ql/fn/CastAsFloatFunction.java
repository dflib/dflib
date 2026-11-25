package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsFloat(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsFloatFunction implements Udf1<Object, Float> {

    @Override
    public Exp<Float> call(Exp<Object> e) {
        return e.castAsFloat();
    }
}
