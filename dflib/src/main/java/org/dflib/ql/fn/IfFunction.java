package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf3;

/**
 * QL {@code if(condition, ifTrue, ifFalse)} function.
 *
 * @since 2.0.0
 */
public class IfFunction implements Udf3<Boolean, Object, Object, Object> {

    @Override
    public Exp<Object> call(Exp<Boolean> condition, Exp<Object> ifTrue, Exp<Object> ifFalse) {
        return Exp.ifExp(condition.castAsBool(), ifTrue, ifFalse);
    }
}
