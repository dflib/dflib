package com.nhl.dflib.exp;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.FalseSeries;
import com.nhl.dflib.series.SingleDoubleValueSeries;
import com.nhl.dflib.series.SingleIntValueSeries;
import com.nhl.dflib.series.SingleLongValueSeries;
import com.nhl.dflib.series.SingleValueSeries;
import com.nhl.dflib.series.TrueSeries;

/**
 * @since 0.11
 */
public class ConstExp<T> extends ScalarExp<T> {
    private volatile Series cached;

    public ConstExp(T value, Class<T> type) {
        super(value, type);
    }

    @Override
    protected Series doEval(int height, Object value) {
        if (cached == null) {
            if (Integer.class == getType()) {
                cached = new SingleIntValueSeries((int) value, height);
            } else if (Long.class == getType()) {
                cached = new SingleLongValueSeries((long) value, height);
            } else if (Double.class == getType()) {
                cached = new SingleDoubleValueSeries((double) value, height);
            } else if (Boolean.class == getType()) {
                if (value == null || !(boolean) value) {
                    cached = new FalseSeries(height);
                } else {
                    cached = new TrueSeries(height);
                }
            } else {
                cached = new SingleValueSeries(value, height);
            }
        }
        return cached;

    }
}
