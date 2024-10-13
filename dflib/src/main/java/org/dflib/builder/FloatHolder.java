package org.dflib.builder;

/**
 * @since 1.1.0
 */
public class FloatHolder implements ValueHolder<Float> {

    private float v;

    @Override
    public Float get() {
        return v;
    }

    @Override
    public void push(Float v) {
        this.v = v != null ? v : 0f;
    }

    @Override
    public void pushFloat(float v) {
        this.v = v;
    }
}
