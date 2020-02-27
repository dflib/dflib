package com.nhl.dflib.series.builder;

/**
 * @since 0.8
 */
public class DoubleHolder implements ValueHolder<Double> {

    private double v;

    @Override
    public Double get() {
        return v;
    }

    @Override
    public void set(Double v) {
        this.v = v != null ? v : 0.;
    }

    @Override
    public double getDouble() {
        return v;
    }

    @Override
    public void setDouble(double v) {
        this.v = v;
    }
}
