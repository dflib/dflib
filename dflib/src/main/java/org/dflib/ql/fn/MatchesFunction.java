package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code matches(e, regex)} function.
 *
 * @since 2.0.0
 */
public class MatchesFunction implements QLFunction {

    public Condition call(Exp<?> e, String regex) {
        return e.matches(regex);
    }
}
