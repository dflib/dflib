package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code endsWith(e, suffix)} function.
 *
 * @since 2.0.0
 */
public class EndsWithFunction implements Udf2<Object, String, Boolean> {

    @Override
    public Exp<Boolean> call(Exp<Object> e, Exp<String> suffix) {
        return e.endsWith(scalar(suffix, String.class));
    }
}
