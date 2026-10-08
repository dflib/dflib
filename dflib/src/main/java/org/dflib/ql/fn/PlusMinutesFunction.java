package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code plusMinutes(e, n)} function.
 *
 * @since 2.0.0
 */
public class PlusMinutesFunction implements QLFunction {

    public TimeExp call(TimeExp e, int n) {
        return e.plusMinutes(n);
    }

    public DateTimeExp call(DateTimeExp e, int n) {
        return e.plusMinutes(n);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int n) {
        return e.plusMinutes(n);
    }
}
