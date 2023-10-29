package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.exp.ExpScalarCondition1;
import com.nhl.dflib.series.FalseSeries;
import com.nhl.dflib.series.TrueSeries;

/**
 * @since 0.19
 */
public class BoolConstExp extends ExpScalarCondition1<Boolean> {

    private final boolean boolValue;

    public BoolConstExp(boolean value) {
        super(value);
        this.boolValue = value;
    }

    @Override
    protected BooleanSeries doEval(int height) {
        return boolValue ? new TrueSeries(height) : new FalseSeries(height);
    }
}
