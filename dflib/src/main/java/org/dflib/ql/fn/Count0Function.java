package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf0;

/**
 * QL {@code count()} function.
 *
 * @since 2.0.0
 */
public class Count0Function implements Udf0<Integer> {

    @Override
    public Exp<Integer> call() {
        return Exp.count();
    }
}
