package org.dflib.parquet.write;



public class DecimalConfig {

    private final int precision;
    private final int scale;

    public DecimalConfig(int precision, int scale) {
        this.precision = precision;
        this.scale = scale;
        if (precision <= 0) {
            throw new IllegalArgumentException("precision must be greater than 0");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("scale must be zero or a positive value");
        }
        if (scale > precision) {
            throw new IllegalArgumentException("scale must be less than or equal to the precision");
        }
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

}
