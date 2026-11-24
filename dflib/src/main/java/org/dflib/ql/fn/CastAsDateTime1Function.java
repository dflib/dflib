package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.time.LocalDateTime;

/**
 * QL {@code castAsDateTime(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsDateTime1Function implements Udf1<Object, LocalDateTime> {

    @Override
    public Exp<LocalDateTime> call(Exp<Object> e) {
        return e.castAsDateTime();
    }
}
