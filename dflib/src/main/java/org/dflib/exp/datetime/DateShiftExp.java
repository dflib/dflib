package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.exp.ShiftExp;

import java.time.LocalDate;

/**
 * @since 2.0.0
 */
public class DateShiftExp extends ShiftExp<LocalDate> implements DateExp {

    public DateShiftExp(Exp<LocalDate> delegate, int offset, LocalDate filler) {
        super(delegate, offset, filler);
    }
}
