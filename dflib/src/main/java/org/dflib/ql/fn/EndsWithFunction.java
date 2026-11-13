package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code endsWith(e, suffix)} function.
 *
 * @since 2.0.0
 */
public class EndsWithFunction implements QLFunction {

    public Condition call(Exp<?> e, String suffix) {
        return e.endsWith(suffix);
    }
}
