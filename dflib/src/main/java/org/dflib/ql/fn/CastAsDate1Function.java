package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.time.LocalDate;

/**
 * QL {@code castAsDate(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDate1Function implements Udf1<Object, LocalDate> {

    @Override
    public Exp<LocalDate> call(Exp<Object> e) {
        return e.castAsDate();
    }
}
