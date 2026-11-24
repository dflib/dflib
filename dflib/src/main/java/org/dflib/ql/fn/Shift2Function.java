package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code shift(e, offset)} function.
 *
 * @since 2.0.0
 */
public class Shift2Function implements Udf2<Object, Number, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> offset) {
        return e.shift(scalar(offset, int.class));
    }
}
