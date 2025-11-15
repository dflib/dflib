package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.Column;


public class BoolColumn extends Column<Boolean> implements Condition {

    public BoolColumn(String name) {
        super(name, Boolean.class);
    }

    public BoolColumn(int position) {
        super(position, Boolean.class);
    }

    @Override
    public String getColumnName() {
        return position >= 0 ? "bool(" + position + ")" : name;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        Series<Boolean> c = super.eval(df);
        return c.compactBool();
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        Series<Boolean> c = super.eval(s);
        return c.compactBool();
    }
}
