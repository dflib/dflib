package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code substr(e, fromInclusive)} function.
 *
 * @since 2.0.0
 */
public class Substr2Function implements Udf2<Object, Number, String> {

    @Override
    public Exp<String> call(Exp<Object> e, Exp<Number> fromInclusive) {
        return e.substr(scalar(fromInclusive, int.class));
    }
}
