package org.dflib.exp.num;

import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;
import org.dflib.series.LongSingleValueSeries;

/**
 * @since 2.0.0
 */
public class LongScalarExp extends ScalarExp<Long> implements NumExp<Long> {

    private final long longValue;

    public LongScalarExp(long value) {
        super(value, Long.class);
        this.longValue = value;
    }

    @Override
    protected LongSeries doEval(int height) {
        return new LongSingleValueSeries(longValue, height);
    }
}
