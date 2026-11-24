package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.math.BigInteger;

/**
 * QL {@code castAsBigint(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsBigintFunction implements Udf1<Object, BigInteger> {

    @Override
    public Exp<BigInteger> call(Exp<Object> e) {
        return e.castAsBigint();
    }
}
