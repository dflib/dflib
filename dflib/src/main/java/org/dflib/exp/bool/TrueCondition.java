package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.series.TrueSeries;

/**
 * @since 2.0.0
 */
public class TrueCondition implements Condition {

    @Override
    public BooleanSeries eval(DataFrame df) {
        return new TrueSeries(df.height());
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return new TrueSeries(s.size());
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return true;
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return true;
    }

    @Override
    public String toQL() {
        return "true";
    }

    @Override
    public String toQL(DataFrame df) {
        return "true";
    }
}
