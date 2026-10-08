package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code concat(..)} function.
 *
 * @since 2.0.0
 */
public class ConcatFunction implements QLFunction {

    public StrExp call(Exp<?>... exps) {
        return Exp.concat((Object[]) exps);
    }
}
