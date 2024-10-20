package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.exp.ScalarExp;

import java.time.LocalDateTime;

/**
 * @since 2.0.0
 */
public class DateTimeScalarExp extends ScalarExp<LocalDateTime> implements DateTimeExp {

    public DateTimeScalarExp(LocalDateTime value) {
        super(value, LocalDateTime.class);
    }
}
