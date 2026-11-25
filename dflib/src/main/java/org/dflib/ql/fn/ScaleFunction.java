package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import java.math.BigDecimal;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code scale(e, scale)} function.
 *
 * @since 2.0.0
 */
public class ScaleFunction implements Udf2<Number, Number, BigDecimal> {

    @Override
    public Exp<BigDecimal> call(Exp<Number> e, Exp<Number> scale) {
        return e.castAsNumber().castAsDecimal().scale(scalar(scale, int.class));
    }
}
