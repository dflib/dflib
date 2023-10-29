package com.nhl.dflib.exp.num;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.exp.ExpScalar1;
import com.nhl.dflib.series.LongSingleValueSeries;

/**
 * @since 0.19
 */
public class LongConstExp extends ExpScalar1<Long> implements NumExp<Long> {

    private final long longValue;

    public LongConstExp(long value) {
        super(value, Long.class);
        this.longValue = value;
    }

    @Override
    protected LongSeries doEval(int height) {
        return new LongSingleValueSeries(longValue, height);
    }
}
