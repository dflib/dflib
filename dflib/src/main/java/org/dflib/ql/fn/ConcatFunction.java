package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.UdfN;

/**
 * QL {@code concat(e1, e2, ...)} function.
 *
 * @since 2.0.0
 */
public class ConcatFunction implements UdfN<String> {

    @Override
    public Exp<String> call(Exp<?>... exps) {
        return Exp.concat((Object[]) exps);
    }
}
