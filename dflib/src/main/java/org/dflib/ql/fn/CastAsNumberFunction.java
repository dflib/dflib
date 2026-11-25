package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import static org.dflib.ql.fn.FunctionUtils.unchecked;

/**
 * QL {@code castAsNumber(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsNumberFunction implements Udf1<Object, Number> {

    @Override
    public Exp<Number> call(Exp<Object> e) {
        return unchecked(e.castAsNumber());
    }
}
