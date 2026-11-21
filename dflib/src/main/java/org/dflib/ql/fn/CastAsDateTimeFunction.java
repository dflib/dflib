package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsDateTime(e)} and {@code castAsDateTime(e, format)} functions.
 *
 * @since 2.0.0
 */
public class CastAsDateTimeFunction implements QLFunction {

    public DateTimeExp call(Exp<?> e) {
        return e.castAsDateTime();
    }

    public DateTimeExp call(Exp<?> e, String format) {
        return e.castAsDateTime(format);
    }
}
