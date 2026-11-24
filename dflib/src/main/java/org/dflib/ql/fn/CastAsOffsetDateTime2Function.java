package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import java.time.OffsetDateTime;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code castAsOffsetDateTime(e, format)} function.
 *
 * @since 2.0.0
 */
public class CastAsOffsetDateTime2Function implements Udf2<Object, String, OffsetDateTime> {

    @Override
    public Exp<OffsetDateTime> call(Exp<Object> e, Exp<String> format) {
        return e.castAsOffsetDateTime(scalar(format, String.class));
    }
}
