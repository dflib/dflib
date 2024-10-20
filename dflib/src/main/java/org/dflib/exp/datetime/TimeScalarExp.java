package org.dflib.exp.datetime;

import org.dflib.TimeExp;
import org.dflib.exp.ScalarExp;

import java.time.LocalTime;

/**
 * @since 2.0.0
 */
public class TimeScalarExp extends ScalarExp<LocalTime> implements TimeExp {

    public TimeScalarExp(LocalTime value) {
        super(value, LocalTime.class);
    }
}
