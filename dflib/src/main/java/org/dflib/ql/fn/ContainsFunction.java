package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code contains(e, substring)} function.
 *
 * @since 2.0.0
 */
public class ContainsFunction implements Udf2<Object, String, Boolean> {

    @Override
    public Exp<Boolean> call(Exp<Object> e, Exp<String> substring) {
        return e.contains(scalar(substring, String.class));
    }
}
