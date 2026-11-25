package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf3;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code vConcat(e, filter, delimiter)} function.
 *
 * @since 2.0.0
 */
public class VConcat3Function implements Udf3<Object, Boolean, String, String> {

    @Override
    public Exp<String> call(Exp<Object> e, Exp<Boolean> filter, Exp<String> delimiter) {
        return e.vConcat(filter.castAsBool(), scalar(delimiter, String.class), "", "");
    }
}
