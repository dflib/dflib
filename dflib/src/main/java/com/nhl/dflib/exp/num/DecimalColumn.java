package com.nhl.dflib.exp.num;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.exp.GenericColumn;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class DecimalColumn extends GenericColumn<BigDecimal> implements DecimalExp {

    public DecimalColumn(String name) {
        super(name, BigDecimal.class);
    }

    public DecimalColumn(int position) {
        super(position, BigDecimal.class);
    }

    @Override
    public String getName() {
        return position >= 0 ? "$decimal(" + position + ")" : name;
    }
}
