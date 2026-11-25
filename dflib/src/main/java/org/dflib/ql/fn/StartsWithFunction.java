package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code startsWith(e, prefix)} function.
 *
 * @since 2.0.0
 */
public class StartsWithFunction implements Udf2<Object, String, Boolean> {

    @Override
    public Exp<Boolean> call(Exp<Object> e, Exp<String> prefix) {
        return e.startsWith(scalar(prefix, String.class));
    }
}
