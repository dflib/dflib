package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.time.OffsetDateTime;

/**
 * QL {@code castAsOffsetDateTime(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsOffsetDateTime1Function implements Udf1<Object, OffsetDateTime> {

    @Override
    public Exp<OffsetDateTime> call(Exp<Object> e) {
        return e.castAsOffsetDateTime();
    }
}
