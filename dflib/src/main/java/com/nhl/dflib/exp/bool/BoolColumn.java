package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Column;
import com.nhl.dflib.Condition;

/**
 * @since 0.11
 */
public class BoolColumn extends Column<Boolean> implements Condition {

    public BoolColumn(String name) {
        super(name, Boolean.class);
    }

    public BoolColumn(int position) {
        super(position, Boolean.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$bool(" + position + ")" : name;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        Series<Boolean> c = super.eval(df);
        return c instanceof BooleanSeries
                ? (BooleanSeries) c
                : BooleanSeries.forSeries(c, BooleanValueMapper.fromObject());
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        Series<Boolean> c = super.eval(s);
        return c instanceof BooleanSeries
                ? (BooleanSeries) c
                : BooleanSeries.forSeries(c, BooleanValueMapper.fromObject());
    }
}
