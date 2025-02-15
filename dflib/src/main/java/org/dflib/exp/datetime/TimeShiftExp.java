package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.TimeExp;
import org.dflib.exp.ShiftExp;

import java.time.LocalTime;

/**
 * @since 2.0.0
 */
public class TimeShiftExp extends ShiftExp<LocalTime> implements TimeExp {

    public TimeShiftExp(Exp<LocalTime> delegate, int offset, LocalTime filler) {
        super(delegate, offset, filler);
    }
}
