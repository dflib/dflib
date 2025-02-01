package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.exp.ScalarExp;

import java.math.BigDecimal;

/**
 * @since 2.0.0
 */
public class DecimalScalarExp extends ScalarExp<BigDecimal> implements DecimalExp {

    public DecimalScalarExp(BigDecimal value) {
        super(value, BigDecimal.class);
    }
}
