package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.ScalarExp;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;

/**
 * @since 2.0.0
 */
public class BoolScalarExp extends ScalarExp<Boolean> implements Condition {

    private final boolean boolValue;

    private static final BoolScalarExp TRUE = new BoolScalarExp(true);
    private static final BoolScalarExp FALSE = new BoolScalarExp(false);

    public BoolScalarExp(boolean value) {
        super(value, Boolean.class);
        this.boolValue = value;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(df.height());
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(s.size());
    }

    public static Condition getTrue() {
        return BoolScalarExp.TRUE;
    }

    public static Condition getFalse() {
        return BoolScalarExp.FALSE;
    }

    private BooleanSeries doEval(int height) {
        return boolValue ? new TrueSeries(height) : new FalseSeries(height);
    }
}
