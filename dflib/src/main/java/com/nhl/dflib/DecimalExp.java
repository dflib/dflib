package com.nhl.dflib;

import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.num.DecimalUnaryExp;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @since 0.11
 */
public interface DecimalExp extends NumericExp<BigDecimal> {

    @Override
    default DecimalExp castAsDecimal() {
        return this;
    }

    /**
     * Created a derived expression that guarantees specific scale for the BigDecimal result.
     */
    default DecimalExp scale(int scale) {
        return new DecimalUnaryExp<>("scale", this, UnaryExp.toSeriesOp(bd -> bd.setScale(scale, RoundingMode.HALF_UP)));
    }
}
