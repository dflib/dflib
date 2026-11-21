package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.ql.QLFunction;

import java.math.BigInteger;

/**
 * QL {@code castAsBigint(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsBigintFunction implements QLFunction {

    public NumExp<BigInteger> call(Exp<?> e) {
        return e.castAsBigint();
    }
}
