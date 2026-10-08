package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code if(condition, ifTrue, ifFalse)} function.
 *
 * @since 2.0.0
 */
public class IfFunction implements QLFunction {

    public <T> Exp<T> call(Condition condition, Exp<T> ifTrue, Exp<T> ifFalse) {
        return Exp.ifExp(condition, ifTrue, ifFalse);
    }
}
