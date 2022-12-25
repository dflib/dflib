package com.nhl.dflib.builder;

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
    public void push(Double v) {
        this.v = v != null ? v : 0.;
    }

    @Override
    public void pushToStore(ValueStore<Double> to) {
        to.pushDouble(v);
    }

    @Override
    public void pushToStore(ValueStore<Double> to, int pos) {
        to.replaceDouble(pos, v);
    }

    @Override
    public void pushDouble(double v) {
        this.v = v;
    }
}
