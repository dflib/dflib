package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.exp.ShiftExp;

import java.time.LocalDateTime;

/**
 * @since 2.0.0
 */
public class DateTimeShiftExp extends ShiftExp<LocalDateTime> implements DateTimeExp {

    public DateTimeShiftExp(Exp<LocalDateTime> delegate, int offset, LocalDateTime filler) {
        super(delegate, offset, filler);
    }
}
