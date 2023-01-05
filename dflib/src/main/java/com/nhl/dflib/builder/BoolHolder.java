package com.nhl.dflib.builder;

/**
 * @since 0.16
 */
public class BoolHolder implements ValueHolder<Boolean> {

    private boolean v;

    @Override
    public Boolean get() {
        return v;
    }

    @Override
    public void push(Boolean v) {
        this.v = v != null ? v : false;
    }

    @Override
    public void pushBool(boolean v) {
        this.v = v;
    }
}
