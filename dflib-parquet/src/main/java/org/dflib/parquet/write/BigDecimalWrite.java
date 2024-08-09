package org.dflib.parquet.write;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;

class BigDecimalWrite {

    enum DecimalMapper {
        INT, LONG, BINARY
    }

    private final int precision;
    private final int scale;
    private final DecimalMapper mapper;

    public BigDecimalWrite(DecimalConfig decimalConfig) {
        this.precision = decimalConfig.getPrecision();
        this.scale = decimalConfig.getScale();
        this.mapper = calcMapper(decimalConfig.getPrecision());
    }

    void write(RecordConsumer recordConsumer, Object value) {
        BigDecimal dec = rescaleIfPossible((BigDecimal) value);
        switch (mapper) {
        case INT:
            recordConsumer.addInteger(dec.unscaledValue().intValue());
            break;
        case LONG:
            recordConsumer.addLong(dec.unscaledValue().longValue());
            break;
        case BINARY:
            byte[] a = dec.unscaledValue().toByteArray();
            recordConsumer.addBinary(Binary.fromConstantByteArray(a));
            break;
        }
    }

    private DecimalMapper calcMapper(int precision) {
        if (precision <= 9) {
            return DecimalMapper.INT;
        }
        if (precision <= 18) {
            return DecimalMapper.LONG;
        }
        return DecimalMapper.BINARY;
    }

    // From org.apache.avro.Conversions$DecimalConversion::validate
    private BigDecimal rescaleIfPossible(BigDecimal value) {
        int valueScale = value.scale();
        boolean scaleAdjusted = false;
        if (valueScale != scale) {
            try {
                value = value.setScale(scale, RoundingMode.UNNECESSARY);
                scaleAdjusted = true;
            } catch (ArithmeticException aex) {
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
