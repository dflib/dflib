package com.nhl.dflib.seriesexp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.SeriesCondition;

/**
 * @since 0.11
 */
public class BooleanColumn extends ColumnExp<Boolean> implements SeriesCondition {

    public BooleanColumn(String name) {
        super(name, Boolean.class);
    }

    public BooleanColumn(int position) {
        super(position, Boolean.class);
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
