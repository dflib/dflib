package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code matches(e, regex)} function.
 *
 * @since 2.0.0
 */
public class MatchesFunction implements Udf2<Object, String, Boolean> {

    @Override
    public Exp<Boolean> call(Exp<Object> e, Exp<String> regex) {
        return e.matches(scalar(regex, String.class));
    }
}
