package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code sum(e)} and {@code sum(e, filter)} functions.
 *
 * @since 2.0.0
 */
public class SumFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e) {
        return e.sum();
    }

    public NumExp<?> call(NumExp<?> e, Condition filter) {
        return e.sum(filter);
    }
}
