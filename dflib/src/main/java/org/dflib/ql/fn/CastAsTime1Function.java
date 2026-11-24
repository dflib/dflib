package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Udf1;

import java.time.LocalTime;

/**
 * QL {@code castAsTime(e)} function.
 *
 * @since 2.0.0
 */
public class CastAsTime1Function implements Udf1<Object, LocalTime> {

    @Override
    public Exp<LocalTime> call(Exp<Object> e) {
        return e.castAsTime();
    }
}
