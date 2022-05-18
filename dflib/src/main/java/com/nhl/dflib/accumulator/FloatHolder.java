package com.nhl.dflib.accumulator;

/**
 * @since 0.8
 */
public class FloatHolder implements ValueHolder<Float> {

    private float v;

    @Override
    public Float get() {
        return v;
    }

    @Override
    public void set(Float v) {
        this.v = v != null ? v : 0.f;
    }

    @Override
    public void store(Accumulator<Float> accumulator) {
        accumulator.addFloat(v);
    }

    @Override
    public void store(int pos, Accumulator<Float> accumulator) {
        accumulator.setFloat(pos, v);
    }

    @Override
    public float getFloat() {
        return v;
    }

    @Override
    public void setFloat(float v) {
        this.v = v;
    }
}
