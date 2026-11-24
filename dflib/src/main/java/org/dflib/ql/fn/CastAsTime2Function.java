package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf2;

import java.time.LocalTime;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code castAsTime(e, format)} function.
 *
 * @since 2.0.0
 */
public class CastAsTime2Function implements Udf2<Object, String, LocalTime> {

    @Override
    public Exp<LocalTime> call(Exp<Object> e, Exp<String> format) {
        return e.castAsTime(scalar(format, String.class));
    }
}
