package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.ShiftExp;

/**
 * @since 2.0.0
 */
public class NumShiftExp<T extends Number> extends ShiftExp<T> implements NumExp<T> {

    public NumShiftExp(Exp<T> delegate, int offset, T filler) {
        super(delegate, offset, filler);
    }
}
