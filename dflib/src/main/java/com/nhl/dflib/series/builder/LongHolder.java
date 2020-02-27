package com.nhl.dflib.series.builder;

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
    public void set(Long v) {
        this.v = v != null ? v : 0L;
    }

    @Override
    public void store(Accumulator<Long> accumulator) {
        accumulator.addLong(v);
    }

    @Override
    public long getLong() {
        return v;
    }

    @Override
    public void setLong(long v) {
        this.v = v;
    }
}
