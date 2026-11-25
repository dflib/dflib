package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code castAsDouble(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDoubleFunction implements Udf1<Object, Double> {

    @Override
    public Exp<Double> call(Exp<Object> e) {
        return e.castAsDouble();
    }
}
