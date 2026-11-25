package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.unchecked;

/**
 * QL {@code sum(e, filter)} function.
 *
 * @since 2.0.0
 */
public class Sum2Function implements Udf2<Number, Boolean, Number> {

    @Override
    public Exp<Number> call(Exp<Number> e, Exp<Boolean> filter) {
        return unchecked(e.castAsNumber().sum(filter.castAsBool()));
    }
}
