package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.exp.Column;

import java.math.BigDecimal;


public class DecimalColumn extends Column<BigDecimal> implements DecimalExp {

    public DecimalColumn(String name) {
        super(name, BigDecimal.class);
    }

    public DecimalColumn(int position) {
        super(position, BigDecimal.class);
    }

    @Override
    public String getColumnName() {
        return position >= 0 ? "decimal(" + position + ")" : name;
    }

    @Override
    public DecimalExp castAsDecimal() {
        return this;
    }
}
