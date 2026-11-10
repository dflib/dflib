package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code minute(e)} function.
 *
 * @since 2.0.0
 */
public class MinuteFunction implements QLFunction {

    public NumExp<Integer> call(TimeExp e) {
        return e.minute();
    }

    public NumExp<Integer> call(DateTimeExp e) {
        return e.minute();
    }

    public NumExp<Integer> call(OffsetDateTimeExp e) {
        return e.minute();
    }
}
