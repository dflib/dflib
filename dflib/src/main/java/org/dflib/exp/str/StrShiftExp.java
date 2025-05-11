package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.exp.ShiftExp;

/**
 * @since 2.0.0
 */
public class StrShiftExp extends ShiftExp<String> implements StrExp {

    public StrShiftExp(Exp<String> delegate, int offset, String filler) {
        super(delegate, offset, filler);
    }
}
