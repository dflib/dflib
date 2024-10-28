package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.AsExp;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class DecimalAsExp extends AsExp<BigDecimal> implements DecimalExp {

    public DecimalAsExp(String name, Exp<BigDecimal> delegate) {
        super(name, delegate);
    }

    @Override
    public DecimalExp as(String name) {
        return Objects.equals(name, this.name) ? this : DecimalExp.super.as(name);
    }
}
