package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.exp.ExpScalarCondition1;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;

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
