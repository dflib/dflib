package com.nhl.dflib.builder;

/**
 * @since 0.8
 */
public class IntHolder implements ValueHolder<Integer> {

    private int v;

    @Override
    public Integer get() {
        return v;
    }

    @Override
    public void push(Integer v) {
        this.v = v != null ? v : 0;
    }

    @Override
    public void store(ValueAccum<Integer> to) {
        to.pushInt(v);
    }

    @Override
    public void store(ValueAccum<Integer> to, int pos) {
        to.replaceInt(pos, v);
    }

    @Override
    public void pushInt(int v) {
        this.v = v;
    }
}
