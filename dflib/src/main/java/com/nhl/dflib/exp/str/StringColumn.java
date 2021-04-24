package com.nhl.dflib.exp.str;

import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.Exp;

/**
 * @since 0.11
 */
public class StringColumn extends ColumnExp<String> {

    public StringColumn(String name) {
        super(name, String.class);
    }

    public StringColumn(int position) {
        super(position, String.class);
    }

    public Exp<String> concat(Exp<String> c) {
        return new BinaryExp<>(
                getName() + "+" + c.getName(),
                String.class,
                this,
                c,
                BinaryExp.toSeriesOp((s1, s2) -> s1 + s2));
    }
}
