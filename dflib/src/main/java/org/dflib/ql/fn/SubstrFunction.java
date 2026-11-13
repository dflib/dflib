package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code substr(e, from)} and {@code substr(e, from, len)} functions.
 *
 * @since 2.0.0
 */
public class SubstrFunction implements QLFunction {

    public StrExp call(Exp<?> e, int fromInclusive) {
        return e.substr(fromInclusive);
    }

    public StrExp call(Exp<?> e, int fromInclusive, int len) {
        return e.substr(fromInclusive, len);
    }
}
