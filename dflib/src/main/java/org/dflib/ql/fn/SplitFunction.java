package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code split(e, regex)} and {@code split(e, regex, limit)} functions.
 *
 * @since 2.0.0
 */
public class SplitFunction implements QLFunction {

    public Exp<?> call(Exp<?> e, String regex) {
        return e.castAsStr().split(regex);
    }

    public Exp<?> call(Exp<?> e, String regex, int limit) {
        return e.castAsStr().split(regex, limit);
    }
}
