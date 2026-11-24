package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import java.time.LocalDate;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code castAsDate(e, format)} function.
 *
 * @since 2.0.0
 */
public class CastAsDate2Function implements Udf2<Object, String, LocalDate> {

    @Override
    public Exp<LocalDate> call(Exp<Object> e, Exp<String> format) {
        return e.castAsDate(scalar(format, String.class));
    }
}
