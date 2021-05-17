package com.nhl.dflib.exp.str;

import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.GenericColumn;

/**
 * @since 0.11
 */
public class StrColumn extends GenericColumn<String> implements StrExp {

    public StrColumn(String name) {
        super(name, String.class);
    }

    public StrColumn(int position) {
        super(position, String.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$str(" + position + ")" : name;
    }
}
