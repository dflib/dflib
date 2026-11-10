package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code first(e)} and {@code first(e, filter)} functions.
 *
 * @since 2.0.0
 */
public class FirstFunction implements QLFunction {

    public <T> Exp<T> call(Exp<T> e) {
        return e.first();
    }

    public <T> Exp<T> call(Exp<T> e, Condition filter) {
        return e.first(filter);
    }
}
