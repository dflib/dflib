package org.dflib.ql.fn;

import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code scale(e, n)} function.
 *
 * @since 2.0.0
 */
public class ScaleFunction implements QLFunction {

    public NumExp<?> call(NumExp<?> e, int scale) {
        return e.castAsDecimal().scale(scale);
    }
}
