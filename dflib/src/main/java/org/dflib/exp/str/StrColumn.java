package org.dflib.exp.str;

import org.dflib.StrExp;
import org.dflib.exp.Column;

/**
 * @since 0.11
 */
public class StrColumn extends Column<String> implements StrExp {

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
