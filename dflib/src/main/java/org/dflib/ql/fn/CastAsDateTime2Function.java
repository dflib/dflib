package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import java.time.LocalDateTime;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code castAsDateTime(e, format)} function.
 *
 * @since 2.0.0
 */
public class CastAsDateTime2Function implements Udf2<Object, String, LocalDateTime> {

    @Override
    public Exp<LocalDateTime> call(Exp<Object> e, Exp<String> format) {
        return e.castAsDateTime(scalar(format, String.class));
    }
}
