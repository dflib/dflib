package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;

/**
 * @since 2.0.0
 */
public class LongScalarExp extends ScalarExp<Long> implements NumExp<Long> {

    public LongScalarExp(long value) {
        super(value, Long.class);
    }
}
