package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.exp.ScalarExp;

import java.time.LocalDate;

/**
 * @since 2.0.0
 */
public class DateScalarExp extends ScalarExp<LocalDate> implements DateExp {

    public DateScalarExp(LocalDate value) {
        super(value, LocalDate.class);
    }
}
