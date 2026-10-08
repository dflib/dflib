package org.dflib.ql.fn;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsDecimal(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDecimalFunction implements QLFunction {

    public DecimalExp call(Exp<?> e) {
        return e.castAsDecimal();
    }
}
