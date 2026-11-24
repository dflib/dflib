package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import static org.dflib.ql.fn.FunctionUtils.unchecked;

/**
 * QL {@code sum(e)} function.
 *
 * @since 2.0.0
 */
public class Sum1Function implements Udf1<Number, Number> {

    @Override
    public Exp<Number> call(Exp<Number> e) {
        return unchecked(e.castAsNumber().sum());
    }
}
