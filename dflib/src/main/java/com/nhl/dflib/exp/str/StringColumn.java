package com.nhl.dflib.exp.str;

import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.ColumnExp;

/**
 * @since 0.11
 */
public class StringColumn extends ColumnExp<String> implements StrExp {

    public StringColumn(String name) {
        super(name, String.class);
    }

    public StringColumn(int position) {
        super(position, String.class);
    }
}
