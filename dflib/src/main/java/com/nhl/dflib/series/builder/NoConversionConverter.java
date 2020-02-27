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
    public void convertAndStore(T from, ValueHolder<T> holder) {
        holder.set(from);
    }

    @Override
    public void convertAndStore(T from, Accumulator<T> accumulator) {
        accumulator.add(from);
    }
}


