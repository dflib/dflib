package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf3;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code split(e, regex, limit)} function.
 *
 * @since 2.0.0
 */
public class Split3Function implements Udf3<Object, String, Number, String[]> {

    @Override
    public Exp<String[]> call(Exp<Object> e, Exp<String> regex, Exp<Number> limit) {
        return e.castAsStr().split(scalar(regex, String.class), scalar(limit, int.class));
    }
}
