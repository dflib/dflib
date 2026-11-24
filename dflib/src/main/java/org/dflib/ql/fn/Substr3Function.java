package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf3;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code substr(e, fromInclusive, len)} function.
 *
 * @since 2.0.0
 */
public class Substr3Function implements Udf3<Object, Number, Number, String> {

    @Override
    public Exp<String> call(Exp<Object> e, Exp<Number> fromInclusive, Exp<Number> len) {
        return e.substr(scalar(fromInclusive, int.class), scalar(len, int.class));
    }
}
