package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code hour(e)} function.
 *
 * @since 2.0.0
 */
public class HourFunction implements QLFunction {

    public NumExp<Integer> call(TimeExp e) {
        return e.hour();
    }

    public NumExp<Integer> call(DateTimeExp e) {
        return e.hour();
    }

    public NumExp<Integer> call(OffsetDateTimeExp e) {
        return e.hour();
    }
}
