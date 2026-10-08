package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code plusMonths(e, n)} function.
 *
 * @since 2.0.0
 */
public class PlusMonthsFunction implements QLFunction {

    public DateExp call(DateExp e, int n) {
        return e.plusMonths(n);
    }

    public DateTimeExp call(DateTimeExp e, int n) {
        return e.plusMonths(n);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int n) {
        return e.plusMonths(n);
    }
}
