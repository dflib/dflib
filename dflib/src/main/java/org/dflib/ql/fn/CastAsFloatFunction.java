package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsFloat(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsFloatFunction implements QLFunction {

    public NumExp<Float> call(Exp<?> e) {
        return e.castAsFloat();
    }
}
