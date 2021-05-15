package com.nhl.dflib.exp.num;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.BinaryExp;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * @since 0.11
 */
public class DecimalBinaryExp extends BinaryExp<BigDecimal, BigDecimal, BigDecimal> implements DecimalExp {

    protected DecimalBinaryExp(
            String opName,
            Exp<BigDecimal> left,
            Exp<BigDecimal> right,
            BiFunction<Series<BigDecimal>, Series<BigDecimal>, Series<BigDecimal>> op) {

        super(opName, BigDecimal.class, left, right, op);
    }
}
