package org.dflib.ql.fn;

import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code round(e)} function.
 *
 * @since 2.0.0
 */
public class RoundFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e) {
        return e.round();
    }
}
