package org.dflib.agg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

class DecimalOps {

    static MathContext op1Context(BigDecimal n) {
        return new MathContext(Math.max(15, 1 + n.scale()), RoundingMode.HALF_UP);
    }
}
