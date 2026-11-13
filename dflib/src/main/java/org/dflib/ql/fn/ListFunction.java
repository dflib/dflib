package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code list(e)} function.
 *
 * @since 2.0.0
 */
public class ListFunction implements QLFunction {

    public Exp<?> call(Exp<?> e) {
        return e.list();
    }
}
