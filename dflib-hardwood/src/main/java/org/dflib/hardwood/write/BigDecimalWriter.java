package org.dflib.hardwood.write;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Validates and rescales BigDecimals before they are handed to Hardwood.
 *
 * <p>Hardwood's own rescaling is governed by a single, writer-wide
 * {@code PrecisionLossPolicy}. DFLib needs "reject" semantics for decimals (silently rounding a monetary value is
 * never the right default) but "truncate" semantics for times and timestamps (which the "dflib-parquet" module has
 * always truncated to the target unit). Since one policy can not be both, the writer runs in "truncate" mode and
 * decimals are validated and rescaled here instead - so by the time a value reaches Hardwood, its scale already
 * matches the column and nothing is left to truncate.
 *
 * @since 2.0.0
 */
public class BigDecimalWriter {

    private final int precision;
    private final int scale;

    public BigDecimalWriter(DecimalConfig decimalConfig) {
        if (decimalConfig == null) {
            throw new IllegalStateException(
                    "If BigDecimal is used, a decimal configuration must be provided in the setup of HardwoodSaver");
        }

        this.precision = decimalConfig.precision();
        this.scale = decimalConfig.scale();
    }

    public BigDecimal rescale(BigDecimal value) {

        if (value == null) {
            return null;
        }

        int valueScale = value.scale();
        boolean scaleAdjusted = false;

        if (valueScale != scale) {
            try {
                value = value.setScale(scale, RoundingMode.UNNECESSARY);
                scaleAdjusted = true;
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(
                        "Cannot encode BigDecimal with scale " + valueScale + " as scale " + scale
                                + " without rounding");
            }
        }

        int valuePrecision = value.precision();
        if (valuePrecision > precision) {
            if (scaleAdjusted) {
                throw new IllegalArgumentException(
                        "Cannot encode BigDecimal with precision " + valuePrecision + " as max precision " + precision
                                + ". This is after safely adjusting scale from " + valueScale + " to required "
                                + scale);
            } else {
                throw new IllegalArgumentException(
                        "Cannot encode BigDecimal with precision " + valuePrecision + " as max precision " + precision);
            }
        }

        return value;
    }
}
