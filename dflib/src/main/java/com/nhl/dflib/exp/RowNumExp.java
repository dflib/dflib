package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.window.RowNumberer;

/**
 * @since 0.19
 */
public class RowNumExp extends Exp0<Integer> implements NumExp<Integer> {

    private static final RowNumExp instance = new RowNumExp();

    public static RowNumExp getInstance() {
        return instance;
    }

    public RowNumExp() {
        super("rowNum()", Integer.class);
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        return RowNumberer.sequence(df.height());
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        return RowNumberer.sequence(s.size());
    }
}
