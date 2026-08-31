package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;

/**
 * Casts this expression to a numeric type determined at eval time by scanning actual data.
 *
 * @since 2.0.0
 */
public class CastAsNumExp extends NumberExp1 implements NumExp<Number> {

    @SuppressWarnings("unchecked")
    public CastAsNumExp(Exp<?> exp) {
        super("castAsNumber", (Exp<Number>) exp, (f, e) -> e);
    }

}
