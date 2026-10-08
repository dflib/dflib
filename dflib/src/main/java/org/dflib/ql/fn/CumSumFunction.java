package org.dflib.ql.fn;

import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code cumSum(e)} function.
 *
 * @since 2.0.0
 */
public class CumSumFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e) {
        return e.cumSum();
    }
}
