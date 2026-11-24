package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import static org.dflib.ql.fn.FunctionUtils.unchecked;

/**
 * QL {@code sqrt(e)} function.
 *
 * @since 2.0.0
 */
public class SqrtFunction implements Udf1<Number, Number> {

    @Override
    public Exp<Number> call(Exp<Number> e) {
        return unchecked(e.castAsNumber().sqrt());
    }
}
