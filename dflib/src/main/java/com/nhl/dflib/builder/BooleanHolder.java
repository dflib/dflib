package com.nhl.dflib.builder;

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
    public void push(Boolean v) {
        this.v = v != null ? v : false;
    }

    @Override
    public void pushToStore(ValueStore<Boolean> to) {
        to.pushBoolean(v);
    }

    @Override
    public void pushToStore(ValueStore<Boolean> to, int pos) {
        to.replaceBoolean(pos, v);
    }

    @Override
    public void pushBoolean(boolean v) {
        this.v = v;
    }
}
