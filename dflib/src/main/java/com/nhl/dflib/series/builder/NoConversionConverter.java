package com.nhl.dflib.series.builder;

/**
 * @since 0.8
 */
public class NoConversionConverter<T> implements ValueConverter<T, T> {

    private static final NoConversionConverter instance = new NoConversionConverter();

    public static <T> NoConversionConverter<T> getInstance() {
        return instance;
    }

    @Override
    public void convertAndStore(T v, ValueHolder<T> holder) {
        holder.set(v);
    }

    @Override
    public void convertAndStore(T v, Accumulator<T> accumulator) {
        accumulator.add(v);
    }

    @Override
    public void convertAndStore(int pos, T v, Accumulator<T> accumulator) {
        accumulator.set(pos, v);
    }
}


