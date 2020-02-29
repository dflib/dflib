package com.nhl.dflib.series.builder;

/**
 * @since 0.8
 */
public class BooleanHolder implements ValueHolder<Boolean> {

    private boolean v;

    @Override
    public Boolean get() {
        return v;
    }

    @Override
    public void set(Boolean v) {
        this.v = v != null ? v : false;
    }

    @Override
    public void store(Accumulator<Boolean> accumulator) {
        accumulator.addBoolean(v);
    }

    @Override
    public void store(int pos, Accumulator<Boolean> accumulator) {
        accumulator.setBoolean(pos, v);
    }

    @Override
    public boolean getBoolean() {
        return v;
    }

    @Override
    public void setBoolean(boolean v) {
        this.v = v;
    }
}
