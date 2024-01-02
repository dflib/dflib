package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.exp.Column;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class DecimalColumn extends Column<BigDecimal> implements DecimalExp {

    public DecimalColumn(String name) {
        super(name, BigDecimal.class);
    }

    public DecimalColumn(int position) {
        super(position, BigDecimal.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$decimal(" + position + ")" : name;
    }
}
