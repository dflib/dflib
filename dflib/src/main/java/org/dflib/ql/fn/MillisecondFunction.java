package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code millisecond(e)} function.
 *
 * @since 2.0.0
 */
public class MillisecondFunction implements QLFunction {

    public NumExp<Integer> call(TimeExp e) {
        return e.millisecond();
    }

    public NumExp<Integer> call(DateTimeExp e) {
        return e.millisecond();
    }

    public NumExp<Integer> call(OffsetDateTimeExp e) {
        return e.millisecond();
    }
}
