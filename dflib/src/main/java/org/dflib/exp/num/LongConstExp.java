package org.dflib.exp.num;

import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.exp.ExpScalar1;
import org.dflib.series.LongSingleValueSeries;

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
