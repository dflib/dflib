package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code contains(e, substring)} function.
 *
 * @since 2.0.0
 */
public class ContainsFunction implements QLFunction {

    public Condition call(Exp<?> e, String substring) {
        return e.contains(substring);
    }
}
