package com.nhl.dflib.exp.str;

import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.ScalarExp;
import com.nhl.dflib.series.StringSingleValueSeries;

/**
 * @since 0.11
 */
public class StrScalarExp extends ScalarExp<String> implements StrExp {

    public StrScalarExp(String value) {
        super(value, String.class);
    }

    @Override
    protected Series<String> doEval(int height, String value) {
        return new StringSingleValueSeries(value, height);
    }
}
