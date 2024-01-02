package org.dflib.exp.datetime;

import org.dflib.TimeExp;
import org.dflib.exp.ConstExp;

import java.time.LocalTime;

/**
 * @since 0.16
 */
public class TimeConstExp extends ConstExp<LocalTime> implements TimeExp {

    public TimeConstExp(LocalTime value) {
        super(value, LocalTime.class);
    }
}
