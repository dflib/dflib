package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code startsWith(e, prefix)} function.
 *
 * @since 2.0.0
 */
public class StartsWithFunction implements QLFunction {

    public Condition call(Exp<?> e, String prefix) {
        return e.startsWith(prefix);
    }
}
