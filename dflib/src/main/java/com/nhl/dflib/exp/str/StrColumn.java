package com.nhl.dflib.exp.str;

import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.ColumnExp;

/**
 * @since 0.11
 */
public class StrColumn extends ColumnExp<String> implements StrExp {

    public StrColumn(String name) {
        super(name, String.class);
    }

    public StrColumn(int position) {
        super(position, String.class);
    }
}
