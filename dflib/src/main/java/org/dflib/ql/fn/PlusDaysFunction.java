package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code plusDays(e, n)} function.
 *
 * @since 2.0.0
 */
public class PlusDaysFunction implements QLFunction {

    public DateExp call(DateExp e, int n) {
        return e.plusDays(n);
    }

    public DateTimeExp call(DateTimeExp e, int n) {
        return e.plusDays(n);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int n) {
        return e.plusDays(n);
    }
}
