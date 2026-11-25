package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.util.List;

/**
 * QL {@code list(e)} function.
 *
 * @since 2.0.0
 */
public class ListFunction implements Udf1<Object, List<Object>> {

    @Override
    public Exp<List<Object>> call(Exp<Object> e) {
        return e.list();
    }
}
