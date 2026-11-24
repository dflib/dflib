package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.math.BigDecimal;

/**
 * QL {@code castAsDecimal(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDecimalFunction implements Udf1<Object, BigDecimal> {

    @Override
    public Exp<BigDecimal> call(Exp<Object> e) {
        return e.castAsDecimal();
    }
}
