package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code last(e)} function.
 *
 * @since 2.0.0
 */
public class LastFunction implements QLFunction {

    public <T> Exp<T> call(Exp<T> e) {
        return e.last();
    }
}
