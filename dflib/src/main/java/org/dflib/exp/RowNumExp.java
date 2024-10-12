package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.series.IntSequenceSeries;

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
