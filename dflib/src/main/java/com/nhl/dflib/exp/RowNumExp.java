package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.IntSequenceSeries;

/**
 * @since 1.0.0-M19
 */
public class RowNumExp extends Exp0<Integer> implements NumExp<Integer> {

    private static final int START_NUMBER = 1;

    private static final RowNumExp instance = new RowNumExp();

    public static RowNumExp getInstance() {
        return instance;
    }

    public RowNumExp() {
        super("rowNum()", Integer.class);
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        return new IntSequenceSeries(START_NUMBER, START_NUMBER + df.height());
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        return new IntSequenceSeries(START_NUMBER, START_NUMBER + s.size());
    }
}
