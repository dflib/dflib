package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.util.Set;

/**
 * QL {@code set(e)} function.
 *
 * @since 2.0.0
 */
public class SetFunction implements Udf1<Object, Set<Object>> {

    @Override
    public Exp<Set<Object>> call(Exp<Object> e) {
        return e.set();
    }
}
