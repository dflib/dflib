package com.nhl.dflib.builder;

/**
 * @since 0.8
 */
public class LongHolder implements ValueHolder<Long> {

    private long v;

    @Override
    public Long get() {
        return v;
    }

    @Override
    public void push(Long v) {
        this.v = v != null ? v : 0L;
    }

    @Override
    public void pushToStore(ValueStore<Long> to) {
        to.pushLong(v);
    }

    @Override
    public void pushToStore(ValueStore<Long> to, int pos) {
        to.replaceLong(pos, v);
    }

    @Override
    public void pushLong(long v) {
        this.v = v;
    }
}
