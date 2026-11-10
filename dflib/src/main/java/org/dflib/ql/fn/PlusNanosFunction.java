package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code plusNanos(e, n)} function.
 *
 * @since 2.0.0
 */
public class PlusNanosFunction implements QLFunction {

    public TimeExp call(TimeExp e, int n) {
        return e.plusNanos(n);
    }

    public DateTimeExp call(DateTimeExp e, int n) {
        return e.plusNanos(n);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int n) {
        return e.plusNanos(n);
    }
}
