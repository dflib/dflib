package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.exp.ShiftExp;

import java.time.OffsetDateTime;

/**
 * @since 2.0.0
 */
public class OffsetDateTimeShiftExp extends ShiftExp<OffsetDateTime> implements OffsetDateTimeExp {

    public OffsetDateTimeShiftExp(Exp<OffsetDateTime> delegate, int offset, OffsetDateTime filler) {
        super(delegate, offset, filler);
    }
}
