package org.dflib.exp.num;

import org.dflib.DecimalExp;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public class DecimalNegateExp extends NegateExp<BigDecimal> implements DecimalExp {

    public DecimalNegateExp(DecimalExp exp, UnaryOperator<BigDecimal> op) {
        super(BigDecimal.class, exp, op);
    }
}
