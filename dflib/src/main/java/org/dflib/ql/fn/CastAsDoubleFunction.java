package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsDouble(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDoubleFunction implements QLFunction {

    public NumExp<Double> call(Exp<?> e) {
        return e.castAsDouble();
    }
}
