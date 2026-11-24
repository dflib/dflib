package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

/**
 * QL {@code count(filter)} function.
 *
 * @since 2.0.0
 */
public class Count1Function implements Udf1<Boolean, Integer> {

    @Override
    public Exp<Integer> call(Exp<Boolean> filter) {
        return Exp.count(filter.castAsBool());
    }
}
