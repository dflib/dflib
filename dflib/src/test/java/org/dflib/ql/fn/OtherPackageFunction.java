package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.ql.QLFunction;

/**
 * A test fixture of a {@link QLFunction} in a package other than {@code org.dflib.ql}.
 */
public class OtherPackageFunction implements QLFunction {

    public NumExp<Integer> call(Exp<?> e) {
        return e.castAsStr().len();
    }

    public NumExp<?> call(NumExp<?> e, int offset) {
        return e.shift(offset);
    }

    public StrExp call(Exp<?>... exps) {
        return Exp.concat((Object[]) exps);
    }
}
