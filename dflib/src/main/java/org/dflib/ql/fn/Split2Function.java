package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code split(e, regex)} function.
 *
 * @since 2.0.0
 */
public class Split2Function implements Udf2<Object, String, String[]> {

    @Override
    public Exp<String[]> call(Exp<Object> e, Exp<String> regex) {
        return e.castAsStr().split(scalar(regex, String.class));
    }
}
