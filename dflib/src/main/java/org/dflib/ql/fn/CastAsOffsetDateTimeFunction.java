package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.ql.QLFunction;

/**
 * QL {@code castAsOffsetDateTime(e)} and {@code castAsOffsetDateTime(e, format)} functions.
 *
 * @since 2.0.0
 */
public class CastAsOffsetDateTimeFunction implements QLFunction {

    public OffsetDateTimeExp call(Exp<?> e) {
        return e.castAsOffsetDateTime();
    }

    public OffsetDateTimeExp call(Exp<?> e, String format) {
        return e.castAsOffsetDateTime(format);
    }
}
